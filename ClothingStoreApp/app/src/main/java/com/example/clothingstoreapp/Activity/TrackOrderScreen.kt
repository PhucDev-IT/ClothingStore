package com.example.clothingstoreapp.Activity

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.ui.text.toUpperCase
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvCheckoutAdapter
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.Model.TypeVoucher
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ActivityTrackOrderScreenBinding
import com.google.firestore.v1.StructuredQuery.Order

class TrackOrderScreen : AppCompatActivity() {
    private lateinit var binding:ActivityTrackOrderScreenBinding
    private lateinit var adapter:RvCheckoutAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClick()
        initView()
    }


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun initView(){
        val order = intent.getSerializableExtra("order") as OrderModel

        adapter = RvCheckoutAdapter(order.carts)

        val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvOrders.adapter = adapter
        binding.rvOrders.layoutManager = linearLayoutManager


        binding.tvOrderDate.text = order.orderDate?.let { FormatCurrency.dateTimeFormat.format(it) }
        binding.tvTotalMoney.text = FormatCurrency.numberFormat.format(order.totalMoney)


        if(order.voucher!=null )
        {
            if(order.voucher!!.typeVoucher == TypeVoucher.FREESHIP.name){
                binding.tvUserVoucher.text = FormatCurrency.numberFormat.format(order.feeShip)
            }else if(order.voucher!!.typeVoucher == TypeVoucher.DISCOUNTMONEY.name){
                binding.tvUserVoucher.text = FormatCurrency.numberFormat.format(order.voucher!!.discount)
            }else if(order.voucher!!.typeVoucher == TypeVoucher.DISCOUNTPERCENT.name){
                binding.tvUserVoucher.text = "${order.voucher!!.discount}%"
            }
        }else{
            binding.tvUserVoucher.text = "0đ"
        }

        binding.tvTypeAddress.text = order.deliveryAddress?.typeAddress
        binding.tvFullName.text = order.deliveryAddress?.fullName
        binding.tvNumberPhone.text = order.deliveryAddress?.numberPhone
        binding.tvDetailsAddress.text = order.deliveryAddress?.addressDetails
        binding.tvInForAddress.text = "${order.deliveryAddress?.phuongXa}, ${order.deliveryAddress?.quanHuyen}, ${order.deliveryAddress?.tinhThanhPho}"
        binding.tvIdOrder.text = order.id?.take(8)?.toUpperCase()

        order.orderStatus?.forEach { (key, value) ->
            val color = ContextCompat.getColor(this, R.color.primarykeyColor)
            when(key){
                ProgressOrder.WaitConfirmOrder.name -> {
                    binding.imgCircleDaDatHang.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    binding.tvTimeOrderDate.text = FormatCurrency.dateTimeFormat.format(value)
                }

                ProgressOrder.PackagingOrder.name ->{
                    binding.imgCirclePackaging.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    binding.tvTimePackagingOrder.text = FormatCurrency.dateTimeFormat.format(value)

                    binding.lineBottomDaDatHang.setBackgroundColor(color)
                    binding.lineTopPackaging.setBackgroundColor(color)
                }
                ProgressOrder.TransportingOrder.name ->{
                    binding.imgCircleTransport.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    binding.tvTimeTransport.text = FormatCurrency.dateTimeFormat.format(value)

                    binding.lineTopTransport.setBackgroundColor(color)
                    binding.lineBottomPackaging.setBackgroundColor(color)
                }

                ProgressOrder.DeliveredOrder.name ->{
                    binding.imgCircleDelivered.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    binding.tvTimeDelivered.text = FormatCurrency.dateTimeFormat.format(value)

                    binding.lineTopDelivered.setBackgroundColor(color)
                    binding.lineBottomTransport.setBackgroundColor(color)
                }
            }
        }

    }

    private fun handleClick(){
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}