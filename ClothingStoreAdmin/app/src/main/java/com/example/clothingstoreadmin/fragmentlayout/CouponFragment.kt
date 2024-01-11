package com.example.clothingstoreadmin.fragmentlayout

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.activity.AddNewVoucherScreen
import com.example.clothingstoreadmin.adapter.RvVoucherAdapter
import com.example.clothingstoreadmin.databinding.FragmentCouponBinding
import com.example.clothingstoreadmin.model.Voucher
import com.example.clothingstoreadmin.service.VoucherService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class CouponFragment : Fragment() {
    private lateinit var _binding: FragmentCouponBinding
    private val binding get() = _binding
    private lateinit var db : FirebaseFirestore
    private lateinit var voucherService : VoucherService
    private lateinit var adapter : RvVoucherAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCouponBinding.inflate(inflater,container,false)

        db = Firebase.firestore
        voucherService = VoucherService(db)

        initView()
        handleClick()
        return binding.root
    }

    private fun initView(){


        adapter = RvVoucherAdapter(object : ClickObjectInterface<Voucher> {
            override fun onClickListener(t: Voucher) {
                Toast.makeText(context,"Định sửa voucher", Toast.LENGTH_SHORT).show()
            }
        },object : ClickObjectInterface<String>{
            override fun onClickListener(t: String) {
                context?.let {
                    copyToClipboard(it,t)
                    Toast.makeText(it,"Đã lưu vào bộ nhớ tạm", Toast.LENGTH_SHORT).show()
                }
            }
        })

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.layoutManager = linearLayoutManager


        getData()
    }

    private fun getData(){

        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility =  View.VISIBLE
        voucherService.selectAllVouchers{list->
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


    private fun handleClick(){
        binding.btnAddNewCoupon.setOnClickListener {
            val intent = Intent(context,AddNewVoucherScreen::class.java)
            startActivity(intent)
        }
    }
}