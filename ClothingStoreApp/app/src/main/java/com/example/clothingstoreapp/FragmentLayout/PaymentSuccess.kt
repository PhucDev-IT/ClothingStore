package com.example.clothingstoreapp.FragmentLayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clothingstoreapp.Activity.PurchasedHistoryScreen
import com.example.clothingstoreapp.Model.PushNotification
import com.example.clothingstoreapp.databinding.FragmentPaymentSuccessBinding


class PaymentSuccess : Fragment() {
   private lateinit var _binding:FragmentPaymentSuccessBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentSuccessBinding.inflate(inflater,container,false)

        initView()

        binding.btnViewOrder.setOnClickListener {
            val intent = Intent(context,PurchasedHistoryScreen::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }

        return binding.root
    }


    //Thông báo đến người dùng
    private fun initView(){
        context?.let {
            PushNotification().sendNotification(it,"Đặt hàng thành công","Bạn có một đơn hàng mới")
        }
    }

    //Gửi thông báo


    //Gửi thông báo đến admin



}