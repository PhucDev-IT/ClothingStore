package com.example.clothingstoreadmin.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreadmin.Interface.CkbObjectInterface
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.Interface.RvPosInterface
import com.example.clothingstoreadmin.adapter.CustomDialog
import com.example.clothingstoreadmin.adapter.RvAddColorsAdapter
import com.example.clothingstoreadmin.adapter.RvCategoryAdapter
import com.example.clothingstoreadmin.databinding.ActivityAddNewProductBinding
import com.example.clothingstoreadmin.model.ProductDetails
import com.example.clothingstoreadmin.model.Product
import com.example.clothingstoreapp.Service.CategoryService
import com.example.clothingstoreapp.Service.ProductService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.util.Date


class AddNewProduct : AppCompatActivity() {

    val PICK_IMAGE_REQUEST = 1 // Định danh yêu cầu chọn ảnh
    private lateinit var getContentSingle: ActivityResultLauncher<String>
    private lateinit var getContentMultiple: ActivityResultLauncher<String>
    private lateinit var binding: ActivityAddNewProductBinding
    private var images = mutableListOf<Uri>()
    private  var imgDemo:Uri? = null
    private lateinit var adapterColor: RvAddColorsAdapter
    private val categoryService = CategoryService()
    private lateinit var productService:ProductService
    private lateinit var db:FirebaseFirestore
    private lateinit var itemClassifies: MutableSet<ProductDetails>
    private var categories:MutableList<String> = mutableListOf()
    private lateinit var customLoading:CustomDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        db = Firebase.firestore
        productService = ProductService(db)
        customLoading = CustomDialog(this)
        initView()
        handleClick()

        getContentSingle = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Ảnh đã được chọn, bạn có thể hiển thị nó hoặc thực hiện các thao tác khác tại đây
                binding.imgProduct.setImageURI(uri)
                imgDemo =uri
            }
        }

        getContentMultiple = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
            uris?.let {

                var index = 0
                // Hiển thị danh sách ảnh
                for (uri in uris) {
                    if(index++ >3) break
                    images.add(uri)
                    when(index){
                        1 -> {
                            binding.containerImg1.visibility = View.VISIBLE
                            binding.imgProduct1.setImageURI(uri)
                        }
                        2->{
                            binding.containerImg2.visibility = View.VISIBLE
                            binding.imgProduct2.setImageURI(uri)
                        }

                        3->{
                            binding.containerImg3.visibility = View.VISIBLE
                            binding.imgProduct3.setImageURI(uri)
                        }
                    }
                }
            }
        }

    }



    private fun initView() {
        categoryService.getAllCategory { list ->
            val layoutManager = GridLayoutManager(this, 2)
            binding.rvCategory.layoutManager = layoutManager

            val adapter = RvCategoryAdapter(list, object : CkbObjectInterface<String> {
                override fun isChecked(m: String) {
                    categories.add(m)
                }

                override fun isNotChecked(m: String) {
                    categories.remove(m)
                }
            })

            binding.rvCategory.adapter = adapter
        }

        itemClassifies = mutableSetOf()

        //Init adapter new colors
        adapterColor = RvAddColorsAdapter(object : ClickObjectInterface<String> {
            override fun onClickListener(t: String) {
                itemClassifies.removeIf{it.id == t}
            }
        })
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvColors.layoutManager = linearLayoutManager
        binding.rvColors.adapter = adapterColor

    }

    private fun handleClick() {


        binding.btnPickImage.setOnClickListener {
           openFileChooseImage()
        }


        binding.addClassify.setOnClickListener {
            addColor()
        }
        binding.imgProduct1.setOnClickListener {
            getContentMultiple.launch("image/*")
        }
        binding.btnAddProduct.setOnClickListener {
            lifecycleScope.launch {
                addProduct()
            }
        }

        binding.btnRemoveAllImg.setOnClickListener {
            if(images.isNotEmpty()){
                images.clear()
                binding.imgProduct1.setImageURI(null)
                binding.containerImg2.visibility = View.GONE
                binding.containerImg3.visibility = View.GONE
            }
        }
    }


    //----------------Xử lý chọn ảnh từ thiết bị cục bộ----------------------
    private fun openFileChooseImage() {
        getContentSingle.launch("image/*")

    }


    private fun addColor() {
        if (binding.edtColor.text.toString().trim().isEmpty()) {
            binding.edtColor.error = "Chưa nhập màu"
        } else if (binding.edtSize.text.toString().trim().isEmpty())
            binding.edtSize.error = "Nhập kích thước"
        else if (binding.edtQuantity.text.toString().trim().isEmpty()) {
            binding.edtSize.error = "Nhập số lượng"
        } else {
            //Kiểm tra classify đã tồn tại hay chưa
            //Nếu tồn tại rồi th kiểm tra size đó đã tồn tại hay chưa

            val index = itemClassifies.indexOfFirst {
                it.nameClassify == binding.edtColor.text.toString()
                    .trim() && it.size == binding.edtSize.text.toString().trim()
            }
            if (index != -1) {
                itemClassifies.elementAt(index).quantity =
                    binding.edtQuantity.text.toString().trim().toInt()
            } else {

                val itemClassify = ProductDetails(
                    binding.edtColor.text.toString().trim(),
                    binding.edtSize.text.toString().trim(),
                    binding.edtQuantity.text.toString().trim().toInt()
                )
                itemClassifies.add(itemClassify)
            }
            adapterColor.setData(itemClassifies.toList())
        }
    }


    private suspend fun addProduct(){
//        if(imgDemo ==null){
//            Toast.makeText(this,"Chưa thêm ảnh",Toast.LENGTH_SHORT).show()
//        }else
            if(binding.edtNameProduct.text.toString().trim().isEmpty()){
            binding.edtNameProduct.error = "Tên sản phẩm?"
        }else if(binding.edtPrice.text.toString().trim().isEmpty()){
            binding.edtPrice.error = "Chưa nhập giá bán"
        }else if(binding.edtDescription.text.toString().trim().isEmpty()){
            binding.edtDescription.error = "Nhập mô tả sản phẩm"
        }else if(itemClassifies.isEmpty()){
            Toast.makeText(this,"Cần phải thêm loại sản phẩm",Toast.LENGTH_SHORT).show()
        }else if(categories.isEmpty()){
            Toast.makeText(this,"Chưa chọn danh mục sản phẩm",Toast.LENGTH_SHORT).show()
        }
        else{
            
            imgDemo?.let { images.add(0,it) }
            customLoading.dialogLoadingBasic("Đang xử lý...")
            productService.addImages(images){links->
                if(links.isNotEmpty()){
                    val product = Product()
                    product.name = binding.edtNameProduct.text.toString().trim()
                    product.price = binding.edtPrice.text.toString().trim().toDouble()
                    product.description = binding.edtDescription.text.toString().trim()
                    product.sale = binding.edtSale.text.toString().trim().toDouble()
                    product.images = links
                    product.idCategory = categories
                    product.createdTime = Date()

                    productService.addProduct(product,itemClassifies.toList()){b->
                        if(b){
                            Toast.makeText(this,"Thêm thành công",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
                        }
                        customLoading.closeDialog()
                    }
                }else{
                    Toast.makeText(this,"Có lôĩ xảy ra, không thêm được ảnh",Toast.LENGTH_SHORT).show()
                    customLoading.closeDialog()
                }
            }



        }
    }
    


}