package com.example.clothingstoreapp.Activity

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.example.clothingstoreapp.Model.ProductIsLiked
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Service.CartService
import com.example.clothingstoreapp.database.ProductDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsScreen : AppCompatActivity() {
    private lateinit var binding:ActivityProductDetailsScreenBinding
    private lateinit var product: Product
    private var selectQuantity:Int = 1
    private var isLikeProduct:Boolean = false
    // Tạo một biến MediaPlayer
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var db:FirebaseFirestore
    private lateinit var cartService:CartService
    private lateinit var customDialog: CustomDialog
    private lateinit var cart:ItemCart


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

    private fun initView(){
         product = intent.getSerializableExtra("product") as Product

        cart = ItemCart(product.id!!,selectQuantity)

        Glide.with(this).load(product.img_preview?.get(0)).into(binding.imgProduct)
        binding.tvNameProduct.text = product.name
        binding.tvPrice.text = FormatCurrency.numberFormat.format(product.price)
        binding.tvDescription.text = product.description
        binding.tvNumberBuyProduct.text = selectQuantity.toString()
        binding.tvRateEvaluate.text = product.rateEvaluate.toString()

        val rvPreview = product.img_preview?.let { RvImagePreviewAdapter(it,object : ClickObjectInterface<String>{
            override fun onClickListener(t: String) {
                Glide.with(applicationContext).load(t).into(binding.imgProduct)
            }
        }) }

        val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvPreviewImage.adapter = rvPreview
        binding.rvPreviewImage.layoutManager = linearLayoutManager

        initClassify()
        initColors()
        binding.btnAddProduct.text = (resources.getString(
            R.string.default_btn_add_to_cart,
            FormatCurrency.numberFormat.format(product.price)
        ))


        checkProductIsLike()
    }

    //Check sản phâẩm códduwocjc yêu thích không
    private fun checkProductIsLike(){
        product.id?.let {
            if(ProductDatabase.getInstance(this).productDao().findByID(it)>0){
                isLikeProduct = true
                binding.btnIsLike.setImageResource(R.drawable.icons8_heart_full_30)
            }else{
                isLikeProduct = false
                binding.btnIsLike.setImageResource(R.drawable.icons8_heart_empty_30)
            }
        }
    }

    private fun initClassify() {

        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.viewholder_category_two
        product.classifies?.forEach { value ->
            val radioButton =
                inflater.inflate(radioButtonLayout, binding.rdoGroupClassifies, false) as RadioButton

            radioButton.text = value
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    cart.classify = buttonView.text.toString()
                }
            }
            binding.rdoGroupClassifies.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
    }

    private fun initColors() {

        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.viewholder_category_two
        product.colors?.forEach { (key,value) ->
            val radioButton =
                inflater.inflate(radioButtonLayout, binding.rdoGroupColors, false) as RadioButton

            radioButton.text = key
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    binding.tvSelectedColor.text = buttonView.text
                    binding.tvSelectedColor.setTextColor(Color.parseColor(value))
                    cart.color = buttonView.text.toString()
                }
            }
            binding.rdoGroupColors.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
    }

    private fun handleEventClick(){
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }

        binding.btnUp.setOnClickListener {
            selectQuantity++
            upDownQuantity()
        }

        binding.btnMinus.setOnClickListener {
            if(selectQuantity>1){
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
            isLikeProduct = if(!isLikeProduct){
                saveProductLiked()
                true
            }else{
                isNotLikedProduct()
                false
            }

        }
    }

    //Lưu sản phẩm vào database khi người dùng ấn tym
    private fun saveProductLiked(){
        CoroutineScope(Dispatchers.IO).launch {
           try {
               val productIsLiked = ProductIsLiked()
               productIsLiked.idProduct = product.id.toString()
               productIsLiked.idCategory = product.idCategory
               ProductDatabase.getInstance(application).productDao().insert(productIsLiked)
               binding.btnIsLike.setImageResource(R.drawable.icons8_heart_full_30)
           }catch (e:Exception){
               Log.e(TAG,"Có lỗi: $e")
           }
        }
    }

    //Hủy tym
    private fun isNotLikedProduct(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productIsLiked = ProductIsLiked()
                productIsLiked.idProduct = product.id.toString()
                productIsLiked.idCategory = product.idCategory
                ProductDatabase.getInstance(application).productDao().delete(productIsLiked)
                binding.btnIsLike.setImageResource(R.drawable.icons8_heart_empty_30)
            }catch (e:Exception){
                Log.e(TAG,"Có lỗi: $e")
            }
        }
    }

    //TĂNG GIẢM SỐ LƯƠNG
    private fun upDownQuantity(){

         binding.tvNumberBuyProduct.text = selectQuantity.toString()

        binding.btnAddProduct.text = (resources.getString(
            R.string.default_btn_add_to_cart,
            FormatCurrency.numberFormat.format(product.price?.times(selectQuantity) ?: 0)
        ))

        cart.quantity = selectQuantity
    }

    //Thêm vào giỏ hàng
    private fun addToCart(){
        if(selectQuantity > product.quantity!!){
            Toast.makeText(this,"Số lượng còn lại không đủ",Toast.LENGTH_SHORT).show()
        }else{
            customDialog.dialogBasic("Đang xử lý...")

            val idUerCurrent: String? = UserManager.getInstance().getUserID()

            if(idUerCurrent == null){
                Toast.makeText(this,"Có lỗi xảy ra khi lấy thông tin người dùng\nHãy đăng nhập lại!",Toast.LENGTH_SHORT).show()
                customDialog.closeDialog()
            }else{

                cartService.addToCart(idUerCurrent,cart){b->
                    if(b){
                        Toast.makeText(this,"Thêm thành công",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"Có lỗi xảy ra !",Toast.LENGTH_SHORT).show()
                    }
                    customDialog.closeDialog()
                }

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