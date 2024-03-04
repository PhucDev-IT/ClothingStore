package com.example.clothingstoreadmin.FragmentLayout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.activity.AddNewProduct
import com.example.clothingstoreadmin.activity.ProductDetailsScreen
import com.example.clothingstoreadmin.adapter.RvProductAdapter
import com.example.clothingstoreadmin.databinding.FragmentManagerProductBinding
import com.example.clothingstoreadmin.model.Product
import com.example.clothingstoreapp.Service.CategoryService
import com.example.clothingstoreapp.Service.ProductService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ManagerProductFragment : Fragment() {

    private lateinit var _binding:FragmentManagerProductBinding
    private val binding get() = _binding
    private lateinit var db: FirebaseFirestore
    private lateinit var productService: ProductService
    private lateinit var adapter: RvProductAdapter
    private lateinit var categoryService: CategoryService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManagerProductBinding.inflate(inflater,container,false)

        db = Firebase.firestore
        productService = ProductService(db)
        categoryService = CategoryService()
        initView()
        handle()


        return binding.root
    }


    private fun initView() {
        adapter = RvProductAdapter(emptyList(), object : ClickObjectInterface<Product> {
            override fun onClickListener(t: Product) {
                val intent = Intent(context, ProductDetailsScreen::class.java)
                intent.putExtra("product", t)
                startActivity(intent)
            }
        })

        val linearLayoutManager = GridLayoutManager(context, 2)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.layoutManager = linearLayoutManager

        getCategory()
    }

    @SuppressLint("SetTextI18n")
    private fun searchProduct() {
        if (binding.edtInputSearch.text.toString().trim().isEmpty()) {
            Toast.makeText(context, "Nhập tên sản phẩm cần tìm", Toast.LENGTH_SHORT).show()
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

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(context,AddNewProduct::class.java)
            startActivity(intent)
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
            val inflater = LayoutInflater.from(context)
            val radioButtonLayout = R.layout.custom_viewholder_radio

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