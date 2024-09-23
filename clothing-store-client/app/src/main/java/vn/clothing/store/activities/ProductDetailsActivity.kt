package vn.clothing.store.activities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
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
import com.stfalcon.imageviewer.StfalconImageViewer
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityProductDetailsBinding
import vn.clothing.store.interfaces.ProductDetailsContract
import vn.clothing.store.models.Image
import vn.clothing.store.models.Product
import vn.clothing.store.models.ProductDetails
import vn.clothing.store.networks.request.CartRequestModel
import vn.clothing.store.presenter.ProductDetailsPresenter
import vn.clothing.store.utils.FormatCurrency

class ProductDetailsActivity : BaseActivity(), ProductDetailsContract.View {
    private lateinit var binding: ActivityProductDetailsBinding
    private var product: Product? = null
    private var images = arrayListOf<String?>()
    private var selectQuantity: Int = 1
    private var presenter: ProductDetailsPresenter? = null
    private var price : Float = 1000000000f
    private var productDetails:List<ProductDetails>?=null

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
        product = intent.getSerializableExtra(IntentData.KEY_PRODUCT) as Product
        images.add(product!!.imgPreview)
    }

    override fun populateData() {
        product?.id?.let {
            presenter?.loadInformationProduct(it)
            presenter?.loadImages(it)
        }
        showData()
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

        binding.btnAddToCart.setOnClickListener { addToCart() }
    }

    override val layoutView: View
        get() {
            binding = ActivityProductDetailsBinding.inflate(layoutInflater)
            return binding.root
        }


    private fun showData() {

        showImages()
        binding.tvNameProduct.text = product?.name
        binding.tvDescription.text = product?.description
        binding.tvPrice.text = FormatCurrency.numberFormat.format(product?.price ?: 1000000000)
        binding.tvRateEvaluate.text = product?.rate.toString()


    }

    private fun showImages(){
        val imageList = ArrayList<SlideModel>() // Create image list
        images.map {
            imageList.add(SlideModel(it, ScaleTypes.CENTER_CROP))
        }
        binding.imgProduct.setImageList(imageList)
        binding.imgProduct.setSlideAnimation(AnimationTypes.ZOOM_OUT)
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

    private fun addToCart(){
        val colorSelected = binding.rdoGroupClassifies.checkedRadioButtonId
        val sizeSelected = binding.rdoGroupSize.checkedRadioButtonId

        if(colorSelected!=-1 && sizeSelected!=-1){
            val colorText = findViewById<RadioButton>(colorSelected).text.toString()
            val sizeText = findViewById<RadioButton>(sizeSelected).text.toString()

            val productDetailsId = productDetails?.firstOrNull { it.color == colorText && it.size == sizeText }?.id

            if(productDetailsId!=null && AppManager.user?.id!=null){
                val model = CartRequestModel(productDetailsId,selectQuantity,colorText,sizeText)
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

    override fun onResultProductDetails(productDetails: List<ProductDetails>) {
        this.productDetails = productDetails
        initClassify(productDetails)
    }

    override fun onResultImages(images: List<Image>) {
        images.map { this.images.add(it.path) }
        showImages()

    }

    override fun getContext(): Context {
        return this
    }

    //================================================
    // endregion
    //================================================
}