package com.example.clothingstoreapp.FragmentLayout

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.clothingstoreapp.Activity.ProductDetailsScreen
import com.example.clothingstoreapp.Activity.SearchScreen
import com.example.clothingstoreapp.Adapter.RvProductHomeAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.PaginationScrollListener
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.ProductService
import com.example.clothingstoreapp.databinding.FragmentHomeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val categories: List<String> = listOf("All", "Mới nhất", "Phổ biến", "Nam", "Nữ","Đặc biệt")
    private var countExit = 0
    private lateinit var productAdapter:RvProductHomeAdapter
    private var isLoading:Boolean = false
    private var isLastPage = false
    private lateinit var productService: ProductService
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        productService = ProductService(db)
        flashSale()
        initSlider()
        initViewProduct()
        initCategory()
        handleClick()
        return binding.root
    }


    private fun initCategory() {

        var index = 0
        val inflater = LayoutInflater.from(context)
        val radioButtonLayout = R.layout.viewholder_category_two
        categories.forEach { value ->
            val radioButton =
                inflater.inflate(radioButtonLayout, binding.radioGroup, false) as RadioButton

            radioButton.text = value
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    selectedCategory(buttonView.text.toString())

                }
            }
            binding.radioGroup.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
    }

    private fun initSlider() {
        val imageList = ArrayList<SlideModel>() // Create image list

        imageList.add(SlideModel(R.drawable.slider1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.slider2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.slider3, ScaleTypes.CENTER_CROP))


        binding.imageSlider.setImageList(imageList)
    }


    private fun flashSale() {
            object : CountDownTimer(3 * 60 * 60 * 1000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    val hours = millisUntilFinished / (60 * 60 * 1000) // tính giờ
                    val minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000) // tính phút
                    val seconds = (millisUntilFinished % (60 * 1000)) / 1000 // tính giây

                    binding.tvHours.text = hours.toString()
                    binding.tvMinute.text = minutes.toString()
                    binding.tvSecond.text = seconds.toString()
                }

                override fun onFinish() {
                   binding.lnTimeSale.visibility = View.GONE
                }
            }.start()

        }

    private fun initViewProduct(){
        productAdapter = RvProductHomeAdapter(emptyList(),object : ClickObjectInterface<Product>{
            override fun onClickListener(t: Product) {
                val intent = Intent(context,ProductDetailsScreen::class.java)
                intent.putExtra("product",t)
                startActivity(intent)
            }
        })

        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 2)
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
    }

    private fun selectedCategory(name:String){
        isLoading = true
        binding.progressCircular.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.GONE

        when(name){
            "All" ->{
                productService.selectAllFirstPage { list->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false
                    binding.swipRefresh.isRefreshing = false
                }
            }
            "Mới nhất" ->{
                productService.selectNewest { list->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false
                }
            }
            "Phổ biến"->{
                productService.selectPopular{ list->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false
                }
            }

            "Nam" ->{
                productService.selectByTags("NAM") { list->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false

                }
            }
            "Nữ" ->{
                productService.selectByTags("NỮ") { list->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false

                }
            }
            else -> {
                productService.selectAllFirstPage { list->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false
                }
            }
        }

    }

    private fun handleClick(){
        binding.tvSearch.setOnClickListener {
            val intent = Intent(context,SearchScreen::class.java)
            startActivity(intent)
        }

        binding.swipRefresh.setOnRefreshListener {
            selectedCategory("All")
        }
    }


}