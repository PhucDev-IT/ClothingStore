package vn.clothing.store.activities

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.stfalcon.imageviewer.StfalconImageViewer
import vn.clothing.store.BuildConfig
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.customview.CircleAnimationUtil
import vn.clothing.store.databinding.ActivityProductDetailsBinding
import vn.clothing.store.interfaces.ProductDetailsContract
import vn.clothing.store.models.Image
import vn.clothing.store.models.ItemQrCodeEmbed
import vn.clothing.store.models.Product
import vn.clothing.store.models.ProductDetails
import vn.clothing.store.models.ProductFavorite
import vn.clothing.store.networks.request.CartRequestModel
import vn.clothing.store.presenter.ProductDetailsPresenter
import vn.clothing.store.utils.FormatCurrency
import vn.clothing.store.utils.Utils

class ProductDetailsActivity : BaseActivity(), ProductDetailsContract.View {
    private lateinit var binding: ActivityProductDetailsBinding
    private var mProduct: Product? = null
    private var images = arrayListOf<String?>()
    private var selectQuantity: Int = 1
    private var presenter: ProductDetailsPresenter? = null
    private var price: Float = 1000000000f

    private var isFavorite = false
    private val TAG = "ProductDetailsActivity"

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!intent.hasExtra(IntentData.KEY_PRODUCT)) {
            CoreConstant.showToast(
                this,
                getString(R.string.has_error_please_retry),
                CoreConstant.ToastType.ERROR
            )
            finish()
            return
        }

        presenter = ProductDetailsPresenter(this)
        val productId = intent.getSerializableExtra(IntentData.KEY_PRODUCT) as String

        if (productId.isNotEmpty()) {
            presenter?.loadInformationProduct(productId)
            presenter?.loadImages(productId)
            showQrcode(productId)
            presenter?.checkFavorite(productId)
        }
    }

    override fun populateData() {
    }

    override fun setListener() {
        binding.btnUp.setOnClickListener {
            if (selectQuantity < binding.tvQuantity.text.toString().toInt()) {
                selectQuantity++
                upDownQuantity()
            }

        }

        binding.btnMinus.setOnClickListener {
            if (selectQuantity > 1) {
                selectQuantity--
                upDownQuantity()
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddToCart.setOnClickListener {
            animationAddCart(binding.imgPreview)
            addToCart()
        }

        binding.btnIsLike.setOnClickListener {
            presenter?.upsertProductFavorite(
                ProductFavorite(
                    mProduct!!.id!!,
                    mProduct!!.name!!,
                    mProduct!!.price!!,
                    mProduct!!.imgPreview!!
                )
            )
            isFavorite = !isFavorite
            handleLikeProduct()
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityProductDetailsBinding.inflate(layoutInflater)
            return binding.root
        }


    private fun handleLikeProduct() {
        if (!isFavorite) {
            isFavorite = false
            binding.btnIsLike.setImageResource(R.drawable.icons8_heart_empty_30)
        } else {
            isFavorite = true
            binding.btnIsLike.setImageResource(R.drawable.ic_heart_fill)
        }
    }

    private fun showData(product: Product) {

        binding.tvNameProduct.text = product?.name
        binding.tvDescription.text = product?.description
        binding.tvPrice.text = FormatCurrency.numberFormat.format(product?.price ?: 1000000000)
        binding.tvRateEvaluate.text = product?.rate.toString()


    }

    private fun animationAddCart(targetView:ImageView){

        val anim = CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(1000).setDestView(binding.btnAddToCart).setAnimationListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                Toast.makeText(this@ProductDetailsActivity, "Continue Shopping...", Toast.LENGTH_SHORT).show();
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        }).startAnimation()
    }

    private fun showImages() {
        try {
            val imageList = ArrayList<SlideModel>() // Create image list
            images.map {
                imageList.add(SlideModel(it, ScaleTypes.CENTER_CROP))
            }
            binding.imgProduct.setImageList(imageList)
            binding.imgProduct.setSlideAnimation(AnimationTypes.ZOOM_OUT)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi hiển thị ảnh")

        }
    }

    private fun showQrcode(id: String) {
        try {
            val image = Utils.generateEAN13Barcode(id, 700, 200)
            binding.imgQrcode.setImageBitmap(image)
            binding.tvIdBarcode.text = id
        } catch (e: Exception) {
            Log.e("Phuc", "Lỗi")
            e.printStackTrace()
        }
    }

    private fun upDownQuantity() {
        binding.tvNumberBuyProduct.text = selectQuantity.toString()
        binding.btnAddToCart.text = (resources.getString(
            R.string.default_btn_add_to_cart,
            FormatCurrency.numberFormat.format(price * selectQuantity)
        ))
    }


    private fun initSizes(list: List<ProductDetails>) {
        binding.rdoGroupSize.removeAllViews()
        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.viewholder_category
        list.forEach { prod ->

            val radioButton =
                inflater.inflate(radioButtonLayout, binding.rdoGroupSize, false) as RadioButton

            radioButton.text = prod.size
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    price = prod.price
                    binding.tvQuantity.text = prod.quantity.toString()
                    selectQuantity = 1
                    upDownQuantity()

                }
            }
            binding.rdoGroupSize.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
    }

    private fun initClassify(listClassify: List<ProductDetails>) {

        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.viewholder_category


        listClassify.forEach { product ->
            val radioButton =
                inflater.inflate(
                    radioButtonLayout,
                    binding.rdoGroupClassifies,
                    false
                ) as RadioButton

            radioButton.text = product.color
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    binding.tvSelectClassify.text = product.color
                    binding.tvPrice.text = FormatCurrency.numberFormat.format(product.price)
                    val sizes = listClassify.filter { it.color == product.color }
                    initSizes(sizes)
                }
            }
            binding.rdoGroupClassifies.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
    }

    private fun addToCart() {
        val colorSelected = binding.rdoGroupClassifies.checkedRadioButtonId
        val sizeSelected = binding.rdoGroupSize.checkedRadioButtonId

        if (colorSelected != -1 && sizeSelected != -1) {
            val colorText = findViewById<RadioButton>(colorSelected).text.toString()
            val sizeText = findViewById<RadioButton>(sizeSelected).text.toString()

            val productDetailsId =
                mProduct?.productDetails?.firstOrNull { it.color == colorText && it.size == sizeText }?.id

            if (productDetailsId != null && AppManager.user?.id != null) {
                val model = CartRequestModel(productDetailsId, selectQuantity, colorText, sizeText)
                presenter?.addToCart(model)
            }

        }

    }

    //==================================================
    // region LISTENER
    //===================================================
    override fun onShowLoading() {
        PopupDialog.showDialogLoading(this)
    }

    override fun onHiddenLoading() {
        PopupDialog.closeDialog()
    }

    override fun onShowError(message: String?) {
        PopupDialog.showDialog(
            this,
            PopupDialog.PopupType.NOTIFICATION,
            null,
            message ?: getString(R.string.has_error_please_retry)
        ) {}
    }

    override fun onShowToast(message: String?, type: CoreConstant.ToastType) {
        CoreConstant.showToast(this, message ?: getString(R.string.has_error_please_retry), type)
    }

    override fun onResultProduct(product: Product) {
        this.mProduct = product
        images.add(product.imgPreview)
        Glide.with(this).load(product.imgPreview).into(binding.imgPreview)
        showData(product)
        product.productDetails?.let { initClassify(it) }
    }

    override fun onResultImages(images: List<Image>) {
        images.map { this.images.add(it.path) }
        showImages()
    }

    override fun getContext(): Context {
        return this
    }

    override fun isProductIsFavorite(isFavorite: Boolean) {
        this.isFavorite = isFavorite
        handleLikeProduct()
    }

    //================================================
    // endregion
    //================================================
}