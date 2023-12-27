package com.example.clothingstoreapp.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvMyVoucherAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.VoucherService
import com.example.clothingstoreapp.databinding.ActivityMyVoucherScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MyVoucherScreen : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private lateinit var voucherService : VoucherService
    private lateinit var adapter : RvMyVoucherAdapter
    private lateinit var binding:ActivityMyVoucherScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyVoucherScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        voucherService = VoucherService(db)

        initView()
    }

    private fun initView(){


        adapter = RvMyVoucherAdapter(object : ClickObjectInterface<String> {
            override fun onClickListener(t: String) {
               copyToClipboard(applicationContext,t)
                Toast.makeText(applicationContext,"Đã lưu vào bộ nhớ tạm", Toast.LENGTH_SHORT).show()
            }
        })

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.layoutManager = linearLayoutManager


        getData()
    }

    private fun getData(){
        val uid  = UserManager.getInstance().getUserID()
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility =  View.VISIBLE
        voucherService.selectAllVoucherNotUse(uid!!){list->
            adapter.setData(list)

            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
        }

    }

    // Hàm này sẽ được gọi khi người dùng nhấn vào nút "Copy"
    fun copyToClipboard(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Clothing code", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}