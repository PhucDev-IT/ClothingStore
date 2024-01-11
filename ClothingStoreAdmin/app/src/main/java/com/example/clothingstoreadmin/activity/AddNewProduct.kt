package com.example.clothingstoreadmin.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.adapter.RvAddColorsAdapter
import com.example.clothingstoreadmin.databinding.ActivityAddNewProductBinding
import com.example.clothingstoreapp.Service.CategoryService
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.nvt.color.ColorPickerDialog


class AddNewProduct : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST_1 = 1
    private val PICK_IMAGE_REQUEST_2 = 2
    private val PICK_IMAGE_REQUEST_3 = 3
    private val PICK_IMAGE_REQUEST_4 = 4


    private lateinit var binding:ActivityAddNewProductBinding
    private var listImagePreview = mutableListOf<Uri>()
    private lateinit var adapterColor:RvAddColorsAdapter
    private val categoryService = CategoryService()
    private var colorSelected:Int? = null
    private var listColors: MutableMap<String, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        handleClick()
    }

    private fun initView(){
        categoryService.getAllCategory { list->
            val adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, list.map { it.nameCategory })

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }

        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Xử lý khi không có mục nào được chọn
            }
        }

        //Init adapter new colors
        adapterColor = RvAddColorsAdapter(object : ClickObjectInterface<Map<String,Int>>{
            override fun onClickListener(t: Map<String, Int>) {
                t.forEach { (key, value) ->
                    listColors[key] = value
                }
            }
        },object : ClickObjectInterface<String>{
            override fun onClickListener(t: String) {
                listColors.remove(t)
            }
        })
        val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvColors.layoutManager = linearLayoutManager
        binding.rvColors.adapter = adapterColor

    }

    private fun handleClick(){
        binding.tvSelectedColor.setOnClickListener {
            val colorPicker = ColorPickerDialog(
                this,
                Color.BLACK, // color init
                true, // true is show alpha
                object : ColorPickerDialog.OnColorPickerListener {
                    override fun onCancel(dialog: ColorPickerDialog?) {
                        // handle click button Cancel
                    }

                    override fun onOk(dialog: ColorPickerDialog?, colorPicker: Int) {
                        binding.tvSelectedColor.imageTintList = ColorStateList.valueOf(colorPicker)
                        colorSelected = colorPicker
                    }
                })
            colorPicker.show()
        }

        binding.btnPickImage.setOnClickListener {
            openFileChooseImage(PICK_IMAGE_REQUEST_1)
        }

        binding.containerImg1.setOnClickListener {
            openFileChooseImage(PICK_IMAGE_REQUEST_2)
        }

        binding.containerImg2.setOnClickListener {
            openFileChooseImage(PICK_IMAGE_REQUEST_3)
        }

        binding.containerImg3.setOnClickListener {
            openFileChooseImage(PICK_IMAGE_REQUEST_4)
        }

        binding.imageRemove1.setOnClickListener {
            binding.imgProduct1.setImageURI(null)
        }
        binding.imageRemove2.setOnClickListener {
            binding.imgProduct2.setImageURI(null)
        }
        binding.imageRemove3.setOnClickListener {
            binding.imgProduct3.setImageURI(null)
        }

        binding.btnAddColor.setOnClickListener {
            addColor()
        }
    }


    //----------------Xử lý chọn ảnh từ thiết bị cục bộ----------------------
    private fun openFileChooseImage(requestID:Int) {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, requestID)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ( resultCode == RESULT_OK && data != null && data.data != null) {
            val url = data.data

            when(requestCode){
                PICK_IMAGE_REQUEST_1 -> binding.imgProduct.setImageURI(url)
                PICK_IMAGE_REQUEST_2 -> binding.imgProduct1.setImageURI(url)
                PICK_IMAGE_REQUEST_3 -> binding.imgProduct2.setImageURI(url)
                PICK_IMAGE_REQUEST_4 -> binding.imgProduct3.setImageURI(url)
            }

        }
    }


    private fun addColor(){
        if(binding.edtColor.text.toString().trim().isEmpty()){
            binding.edtColor.error = "Chưa nhập màu"
        }else{
            if(colorSelected!=null)
                listColors[binding.edtColor.text.toString().trim()] = colorSelected!!
            adapterColor.insertData(binding.edtColor.text.toString().trim(),colorSelected)
        }
    }


}