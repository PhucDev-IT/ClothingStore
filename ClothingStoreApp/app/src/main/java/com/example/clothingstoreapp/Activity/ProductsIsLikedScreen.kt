package com.example.clothingstoreapp.Activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvProductHomeAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.PaginationScrollListener
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.Model.ProductIsLiked
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.ProductService
import com.example.clothingstoreapp.database.ProductDatabase
import com.example.clothingstoreapp.databinding.ActivityProductDetailsScreenBinding
import com.example.clothingstoreapp.databinding.ActivityProductsIsLikedScreenBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductsIsLikedScreen : AppCompatActivity() {
    private lateinit var binding:ActivityProductsIsLikedScreenBinding
    private lateinit var productAdapter:RvProductHomeAdapter
    private var isLoading:Boolean = false
    private var isLastPage = false
    private lateinit var productService: ProductService
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsIsLikedScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productService = ProductService(db)
        initView()



        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    private fun getProduct(){
        val list: List<ProductIsLiked>? = ProductDatabase.getInstance(this).productDao().selectAllProducts()

        if (list != null) {
            productService.getProductsIsLiked(list){value->
                Log.w(TAG,"Size: ${value.size}")
                productAdapter.setData(value)
            }
        }

    }

    private fun initView(){


        productAdapter = RvProductHomeAdapter(emptyList(),object : ClickObjectInterface<Product> {
            override fun onClickListener(t: Product) {
                val intent = Intent(application,ProductDetailsScreen::class.java)
                intent.putExtra("product",t)
                startActivity(intent)
            }
        })

        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = productAdapter
        binding.rvProducts.layoutManager = linearLayoutManager

        binding.rvProducts.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager){
            override fun loadMoreItem() {

            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun isLastPage(): Boolean {
                return  isLastPage
            }
        })

        getProduct()
    }
}