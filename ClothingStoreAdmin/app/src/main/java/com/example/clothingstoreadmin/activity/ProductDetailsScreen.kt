package com.example.clothingstoreadmin.activity

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

import android.media.MediaPlayer
import android.util.Log
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.adapter.CustomDialog
import com.example.clothingstoreadmin.adapter.RvImagePreviewAdapter
import com.example.clothingstoreadmin.databinding.ActivityProductDetailsScreenBinding
import com.example.clothingstoreadmin.model.FormatCurrency
import com.example.clothingstoreadmin.model.ItemCart
import com.example.clothingstoreadmin.model.Product
import com.example.clothingstoreadmin.service.CartService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsScreen : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsScreenBinding
    private lateinit var product: Product
    private var selectQuantity:Int = 1

    private lateinit var db:FirebaseFirestore
    private lateinit var cartService: CartService
    private lateinit var customDialog: CustomDialog
    private lateinit var cart: ItemCart
    private var isOpenPreview = true
    private lateinit var adapter: RvImagePreviewAdapter

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

        binding.tvRateEvaluate.text = product.rateEvaluate.toString()

         product.img_preview?.let {
            adapter = RvImagePreviewAdapter(it,object : ClickObjectInterface<String> {
            override fun onClickListener(t: String) {
                Glide.with(applicationContext).load(t).into(binding.imgProduct)
            }
        }) }

        val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvPreviewImage.adapter = adapter
        binding.rvPreviewImage.layoutManager = linearLayoutManager

        initClassify()
        initColors()


    }


    private fun initClassify() {

        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.custom_viewholder_radio
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
        val radioButtonLayout = R.layout.custom_viewholder_radio
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

        binding.btnControlPreview.setOnClickListener {
            if(isOpenPreview){
                adapter.setData(listOf(product.img_preview?.get(0) ?: ""))
                isOpenPreview = false
            }else{
                product.img_preview?.let { it1 -> adapter.setData(it1) }
                isOpenPreview = true
                binding.btnControlPreview.setImageResource(R.drawable.baseline_keyboard_double_arrow_right_24)
            }
        }



        binding.btnBack.setOnClickListener {
            onBackPressed()
        }


    }


    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher.onBackPressed()"))
    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        onBackPressedDispatcher.onBackPressed()
    }
}