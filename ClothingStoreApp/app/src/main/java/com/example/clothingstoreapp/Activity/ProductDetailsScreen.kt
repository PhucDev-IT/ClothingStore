package com.example.clothingstoreapp.Activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Adapter.RvImagePreviewAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ActivityProductDetailsScreenBinding
import android.media.MediaPlayer
import android.util.Log
import com.example.clothingstoreapp.Adapter.CustomDialog
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.ProductDetails
import com.example.clothingstoreapp.Model.ProductIsLiked
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Service.CartService
import com.example.clothingstoreapp.Service.ProductService
import com.example.clothingstoreapp.database.ProductDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsScreen : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsScreenBinding
    private lateinit var product: Product
    private var selectQuantity: Int = 1
    private var isLikeProduct: Boolean = false

    // Tạo một biến MediaPlayer
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var cartService: CartService
    private lateinit var customDialog: CustomDialog

    private var isOpenPreview = true
    private lateinit var adapter: RvImagePreviewAdapter

    private var cart = ItemCart()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        cartService = CartService(db)
        customDialog = CustomDialog(this)
        initView()

        handleEventClick()
    }

    private fun callProductDetails(id: String) {
        val db = Firebase.firestore

        ProductService(db).getProductDetails(id) { list ->

            if (list.isNotEmpty())
                initClassify(list)
        }
    }

    private fun initView() {
        product = intent.getSerializableExtra("product") as Product

        product.id?.let { callProductDetails(it) }

        cart = ItemCart(
            product.id!!, selectQuantity
        )


        Glide.with(this).load(product.images?.get(0)).into(binding.imgProduct)
        binding.tvNameProduct.text = product.name
        binding.tvPrice.text = FormatCurrency.numberFormat.format(product.price)
        binding.tvDescription.text = product.description
        binding.tvNumberBuyProduct.text = selectQuantity.toString()
        binding.tvRateEvaluate.text = product.rateEvaluate.toString()

        product.images?.let {
            adapter = RvImagePreviewAdapter(it, object : ClickObjectInterface<String> {
                override fun onClickListener(t: String) {
                    Glide.with(applicationContext).load(t).into(binding.imgProduct)
                }
            })
        }

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPreviewImage.adapter = adapter
        binding.rvPreviewImage.layoutManager = linearLayoutManager


        binding.btnAddToCart.text = (resources.getString(
            R.string.default_btn_add_to_cart,
            FormatCurrency.numberFormat.format(product.price)
        ))


        checkProductIsLike()
    }

    //Check sản phâẩm códduwocjc yêu thích không
    private fun checkProductIsLike() {
        product.id?.let {
            if (ProductDatabase.getInstance(this).productDao().findByID(it) > 0) {
                isLikeProduct = true
                binding.btnIsLike.setImageResource(R.drawable.icons8_heart_full_30)
            } else {
                isLikeProduct = false
                binding.btnIsLike.setImageResource(R.drawable.icons8_heart_empty_30)
            }
        }
    }

    private fun initClassify(listClassify: List<ProductDetails>) {

        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.viewholder_classify_product


        listClassify.forEach { product ->
            val radioButton =
                inflater.inflate(
                    radioButtonLayout,
                    binding.rdoGroupClassifies,
                    false
                ) as RadioButton

            radioButton.text = product.nameClassify
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    cart
                    binding.tvSelectClassify.text = product.nameClassify
                    val sizes = listClassify.filter { it.nameClassify == product.nameClassify }
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

    private fun initSizes(list: List<ProductDetails>) {
        binding.rdoGroupSize.removeAllViews()
        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.viewholder_classify_product
        list.forEach { size ->

            val radioButton =
                inflater.inflate(radioButtonLayout, binding.rdoGroupSize, false) as RadioButton

            radioButton.text = size.size
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    binding.tvQuantity.text = size.quantity.toString()
                    selectQuantity = 1
                    upDownQuantity()
                    cart.classifyId = size.id
                }
            }
            binding.rdoGroupSize.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
    }

    private fun handleEventClick() {

        binding.btnControlPreview.setOnClickListener {
            if (isOpenPreview) {
                adapter.setData(listOf(product.images?.get(0)!!))
                isOpenPreview = false
            } else {
                product.images?.let { it1 -> adapter.setData(it1) }
                isOpenPreview = true
                binding.btnControlPreview.setImageResource(R.drawable.baseline_keyboard_double_arrow_right_24)
            }
        }

        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ChatDetailsScreen::class.java)
            startActivity(intent)
        }

        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }

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

        binding.btnIsLike.setOnClickListener {
            mediaPlayer?.release()
            mediaPlayer = null
            // Tạo mới MediaPlayer và phát âm thanh từ tập tin raw
            mediaPlayer = MediaPlayer.create(this, R.raw.interface_124464)
            mediaPlayer?.start()
            isLikeProduct = if (!isLikeProduct) {
                saveProductLiked()
                true
            } else {
                isNotLikedProduct()
                false
            }

        }
    }

    //Lưu sản phẩm vào database khi người dùng ấn tym
    private fun saveProductLiked() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productIsLiked = ProductIsLiked()
                productIsLiked.idProduct = product.id.toString()
                // productIsLiked.idCategory = product.idCategory
                ProductDatabase.getInstance(application).productDao().insert(productIsLiked)
                binding.btnIsLike.setImageResource(R.drawable.icons8_heart_full_30)
            } catch (e: Exception) {
                Log.e(TAG, "Có lỗi: $e")
            }
        }
    }

    //Hủy tym
    private fun isNotLikedProduct() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productIsLiked = ProductIsLiked()
                productIsLiked.idProduct = product.id.toString()
                // productIsLiked.idCategory = product.idCategory
                ProductDatabase.getInstance(application).productDao().delete(productIsLiked)
                binding.btnIsLike.setImageResource(R.drawable.icons8_heart_empty_30)
            } catch (e: Exception) {
                Log.e(TAG, "Có lỗi: $e")
            }
        }
    }

    //TĂNG GIẢM SỐ LƯƠNG
    private fun upDownQuantity() {

        binding.tvNumberBuyProduct.text = selectQuantity.toString()

        binding.btnAddToCart.text = (resources.getString(
            R.string.default_btn_add_to_cart,
            FormatCurrency.numberFormat.format(product.price?.times(selectQuantity) ?: 0)
        ))

        cart.quantity = selectQuantity

    }

    //Thêm vào giỏ hàng
    private fun addToCart() {

        customDialog.dialogLoadingBasic("Đang xử lý...")

        val idUerCurrent: String? = UserManager.getInstance().getUserID()

        if (idUerCurrent == null) {
            Toast.makeText(
                this,
                "Có lỗi xảy ra khi lấy thông tin người dùng\nHãy đăng nhập lại!",
                Toast.LENGTH_SHORT
            ).show()
            customDialog.closeDialog()
        } else {

            cartService.addToCart(idUerCurrent, cart) { b ->
                if (b) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
                    customDialog.closeDialog()
                } else {
                    Toast.makeText(this, "Có lỗi xảy ra !", Toast.LENGTH_SHORT).show()
                }
                customDialog.closeDialog()
            }


        }
    }


    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher.onBackPressed()"))
    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        onBackPressedDispatcher.onBackPressed()
    }
}