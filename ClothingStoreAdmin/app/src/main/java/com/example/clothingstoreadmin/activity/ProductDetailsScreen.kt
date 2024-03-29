package com.example.clothingstoreadmin.activity

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.adapter.CustomDialog
import com.example.clothingstoreadmin.adapter.RvImagePreviewAdapter
import com.example.clothingstoreadmin.databinding.ActivityProductDetailsScreenBinding
import com.example.clothingstoreadmin.model.FormatCurrency
import com.example.clothingstoreadmin.model.Product
import com.example.clothingstoreadmin.model.ProductDetails
import com.example.clothingstoreapp.Service.ProductService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ProductDetailsScreen : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsScreenBinding
    private lateinit var product: Product

    private lateinit var db:FirebaseFirestore
    private lateinit var customDialog: CustomDialog
    private var isOpenPreview = true
    private lateinit var adapter: RvImagePreviewAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        customDialog = CustomDialog(this)
        initView()
        handleEventClick()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initView(){
         product = intent.getSerializableExtra("product",Product::class.java)!!
        product.id?.let { callProductDetails(it) }

        Glide.with(this).load(product.images?.get(0)).into(binding.imgProduct)
        binding.tvNameProduct.text = product.name
        binding.tvPrice.text = FormatCurrency.numberFormat.format(product.price)
        binding.tvDescription.text = product.description

        binding.tvRateEvaluate.text = product.rateEvaluate.toString()

         product.images?.let {
            adapter = RvImagePreviewAdapter(it,object : ClickObjectInterface<String> {
            override fun onClickListener(t: String) {
                Glide.with(applicationContext).load(t).into(binding.imgProduct)
            }
        }) }

        val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvPreviewImage.adapter = adapter
        binding.rvPreviewImage.layoutManager = linearLayoutManager

    }

    private fun callProductDetails(id:String){
        val db = Firebase.firestore

        ProductService(db).getProductDetails(id){ list->

            if(list.isNotEmpty())
                initClassify(list)
            }
        }


    private fun initClassify(listClassify:List<ProductDetails>) {

        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.custom_viewholder_radio


        listClassify.forEach { product ->
            val radioButton =
                inflater.inflate(radioButtonLayout, binding.rdoGroupClassifies, false) as RadioButton

            radioButton.text = product.nameClassify
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
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

    private fun initSizes(list:List<ProductDetails>) {
        binding.rdoGroupSize.removeAllViews()
        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.custom_viewholder_radio
        list.forEach { size ->

            val radioButton =
                inflater.inflate(radioButtonLayout, binding.rdoGroupSize, false) as RadioButton

            radioButton.text = size.size
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    binding.tvQuantity.text = size.quantity.toString()
                }
            }
            binding.rdoGroupSize.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
    }

    private fun handleEventClick(){

        binding.btnControlPreview.setOnClickListener {
            if(isOpenPreview){
                product.images?.let {
                    adapter.setData(it)
                }
                isOpenPreview = false
            }else{
                product.images?.let { it1 -> adapter.setData(it1) }
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