package com.example.clothingstoreapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvProductHomeAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.CategoryService
import com.example.clothingstoreapp.Service.ProductService
import com.example.clothingstoreapp.databinding.ActivitySearchScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class SearchScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySearchScreenBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var productService: ProductService
    private lateinit var adapter: RvProductHomeAdapter
    private lateinit var categoryService: CategoryService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        productService = ProductService(db)
        categoryService = CategoryService(db)
        initView()
        handle()
    }

    private fun initView() {
        adapter = RvProductHomeAdapter(emptyList(), object : ClickObjectInterface<Product> {
            override fun onClickListener(t: Product) {
                val intent = Intent(application, ProductDetailsScreen::class.java)
                intent.putExtra("product", t)
                startActivity(intent)
            }
        })

        val linearLayoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.layoutManager = linearLayoutManager

        getCategory()
    }

    @SuppressLint("SetTextI18n")
    private fun searchProduct() {
        if (binding.edtInputSearch.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Nhập tên sản phẩm cần tìm", Toast.LENGTH_SHORT).show()
        } else {
            binding.progressLoading.visibility = View.VISIBLE
            binding.lnResultSearch.visibility = View.VISIBLE
            binding.tvKeySearch.text = "\"${binding.edtInputSearch.text.toString().trim()}\""
            productService.searchProduct(binding.edtInputSearch.text.toString().trim()) { list ->
                adapter.setData(list)

                binding.progressLoading.visibility = View.GONE
            }
        }
    }

    private fun handle() {

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.edtInputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT
            ) {
                searchProduct()
                true
            } else {
                false
            }
        }
    }

    //Lấy danh sách danh mục
    private fun getCategory() {
        var index = 0
        categoryService.getAllCategory { list ->
            val inflater = LayoutInflater.from(this)
            val radioButtonLayout = R.layout.viewholder_category_two

            for (value in list) {
                val radioButton =
                    inflater.inflate(
                        radioButtonLayout,
                        binding.rdoGroupCategory,
                        false
                    ) as RadioButton

                radioButton.text = value.nameCategory
                radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                    if (ischecked) {
                        findProductByCategory(value.id!!)
                    }
                }
                binding.rdoGroupCategory.addView(radioButton)
                if (index == 0) {
                    radioButton.isChecked = true
                }
                index++
            }
        }
    }

    //Láy sản phẩm dựa trên danh mục
    private fun findProductByCategory(id:String){
        binding.lnResultSearch.visibility = View.GONE
        binding.progressLoading.visibility = View.VISIBLE
        productService.selectProductByCategory(id){list->
            adapter.setData(list)

            binding.progressLoading.visibility = View.GONE
        }
    }

}