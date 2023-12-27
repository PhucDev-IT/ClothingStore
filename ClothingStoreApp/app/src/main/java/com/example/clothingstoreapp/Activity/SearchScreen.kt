package com.example.clothingstoreapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvProductHomeAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.ProductService
import com.example.clothingstoreapp.databinding.ActivitySearchScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class SearchScreen : AppCompatActivity() {
    private lateinit var binding:ActivitySearchScreenBinding
    private lateinit var db:FirebaseFirestore
    private lateinit var productService: ProductService
    private lateinit var adapter:RvProductHomeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db =  Firebase.firestore
        productService = ProductService(db)
        initView()
    }

    private fun initView(){
         adapter = RvProductHomeAdapter(emptyList(),object : ClickObjectInterface<Product>{
            override fun onClickListener(t: Product) {

            }
        })

        val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.layoutManager  = linearLayoutManager

        searchProduct()
    }

    private fun searchProduct(){
        if(binding.edtInputSearch.text.toString().trim().isEmpty()){
            Toast.makeText(this,"Nhập tên sản phẩm cần tìm",Toast.LENGTH_SHORT).show()
        }else{

            binding.lnSearchRecent.visibility = View.GONE
            binding.lnShowProductSearch.visibility = View.VISIBLE

            productService.searchProduct(binding.edtInputSearch.text.toString().trim()){list->
                adapter.setData(list)
            }
        }
    }

    private fun handle(){

    }
}