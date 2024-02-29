package com.example.clothingstoreapp.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.compose.ui.text.toUpperCase
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.CustomDialog
import com.example.clothingstoreapp.Adapter.RvCheckoutAdapter
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.Model.TypeVoucher
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.OrderService
import com.example.clothingstoreapp.databinding.ActivityTrackOrderScreenBinding
import com.google.firestore.v1.StructuredQuery.Order
import java.util.Date
import java.util.Locale

class TrackOrderScreen : AppCompatActivity() {
    private lateinit var binding:ActivityTrackOrderScreenBinding
    private lateinit var adapter:RvCheckoutAdapter
    private lateinit var orderM:OrderModel
    private lateinit var customLoading: CustomDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customLoading = CustomDialog(this)
        handleClick()
        initView()
    }


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun initView(){
        val id = intent.getStringExtra("id")
        var status = intent.getStringExtra("status")

        if(id==null || status==null) return
        if(status == ProgressOrder.WaitConfirmOrder.name || status == ProgressOrder.Shipping.name)
            status = "WaitingDelivery"


        OrderService().getInformationOrderByID(status,id){order->
            if(order!=null){
                orderM = order
                adapter = RvCheckoutAdapter(order.carts)

                val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                binding.rvOrders.adapter = adapter
                binding.rvOrders.layoutManager = linearLayoutManager


                binding.tvOrderDate.text = order.orderDate.let { FormatCurrency.dateTimeFormat.format(it) }
                binding.tvTotalMoney.text = FormatCurrency.numberFormat.format(order.totalMoney)
                binding.tvFeeShip.text = FormatCurrency.numberFormat.format(order.feeShip)

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
                binding.tvInForAddress.text = "${order.deliveryAddress?.province?.ProvinceName}, " +
                        "${order.deliveryAddress?.district?.districtName}, ${order.deliveryAddress?.ward?.WardName}"
                binding.tvIdOrder.text = order.id?.take(8)?.toUpperCase(Locale.ROOT)


                order.orderStatus.forEach { (key, value) ->
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
                            binding.btnCancelOrder.visibility = View.GONE
                        }

                        ProgressOrder.Shipping.name ->{
                            binding.imgCircleTransport.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                            binding.tvTimeTransport.text = FormatCurrency.dateTimeFormat.format(value)

                            binding.lineTopTransport.setBackgroundColor(color)
                            binding.lineBottomPackaging.setBackgroundColor(color)
                            binding.btnCancelOrder.visibility = View.GONE
                        }

                        ProgressOrder.DeliveredOrder.name ->{
                            binding.imgCircleDelivered.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                            binding.tvTimeDelivered.text = FormatCurrency.dateTimeFormat.format(value)

                            binding.lineTopDelivered.setBackgroundColor(color)
                            binding.lineBottomTransport.setBackgroundColor(color)
                            binding.btnCancelOrder.visibility = View.GONE
                        }
                        ProgressOrder.OrderCanceled.name ->{

                            binding.lnCancelOrder.visibility = View.VISIBLE
                            binding.tvTimeCancel.text = FormatCurrency.dateTimeFormat.format(value)
                            binding.tvReasonCancel.text = "Lý do: "+order.reasonCancel
                            binding.btnCancelOrder.visibility = View.GONE
                        }

                    }
                }

            }
        }


    }

    private fun handleClick(){
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnCancelOrder.setOnClickListener {
            openDialogCancelOrder()
        }
    }

    private fun openDialogCancelOrder(){
        val dialog:Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_cancel_order)

        val window: Window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute:WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.BOTTOM
        window.attributes = windowAttribute

        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)

        btnConfirm.setOnClickListener {


            val checkedRadioButtonId = radioGroup.checkedRadioButtonId

            if (checkedRadioButtonId != -1) {
                customLoading.dialogLoadingBasic("Đang xử lý")
                val radioButton = radioGroup.findViewById<RadioButton>(checkedRadioButtonId)
                val selectedText = radioButton.text.toString()

                orderM.reasonCancel = selectedText
                orderM.currentStatus = ProgressOrder.OrderCanceled.name
                orderM.orderStatus[ProgressOrder.OrderCanceled.name] = Date()
                OrderService().cancelOrder(orderM){b->
                    if(b){
                        customLoading.closeDialog()
                        dialog.dismiss()
                        onBackPressed()
                        finish()
                    }else{
                        customLoading.closeDialog()
                        Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Chọn lý do trước khi tiếp tục", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()

    }
}