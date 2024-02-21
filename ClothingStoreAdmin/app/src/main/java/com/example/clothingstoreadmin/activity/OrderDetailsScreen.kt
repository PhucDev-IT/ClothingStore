package com.example.clothingstoreadmin.activity

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.adapter.CustomDialog
import com.example.clothingstoreadmin.adapter.RvCheckoutAdapter
import com.example.clothingstoreadmin.api.ApiNotificationService
import com.example.clothingstoreadmin.databinding.ActivityOrderDetailsScreenBinding
import com.example.clothingstoreadmin.model.Data
import com.example.clothingstoreadmin.model.DataMessageNotification
import com.example.clothingstoreadmin.model.FormatCurrency
import com.example.clothingstoreadmin.model.NotificationModel
import com.example.clothingstoreadmin.model.OrderModel
import com.example.clothingstoreadmin.model.ProgressOrder
import com.example.clothingstoreadmin.model.TypeVoucher
import com.example.clothingstoreadmin.service.NotificationService
import com.example.clothingstoreadmin.service.OrderService
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.util.Date
import java.util.Locale


class OrderDetailsScreen : AppCompatActivity() {
    private lateinit var binding:ActivityOrderDetailsScreenBinding
    private lateinit var adapter: RvCheckoutAdapter
    private lateinit var order:OrderModel
    private lateinit var customDialog: CustomDialog
    private  val db = Firebase.firestore
    private val notificationService = NotificationService(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customDialog = CustomDialog(this)

        order = intent.getSerializableExtra("order") as OrderModel
        handleClick()
        initView()
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun initView(){

        adapter = RvCheckoutAdapter(order.carts)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
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
        binding.tvIdOrder.text = order.id?.take(8)?.toUpperCase(Locale.ROOT)

        order.orderStatus?.forEach { (key, value) ->
            val color = ContextCompat.getColor(this, R.color.primaryColor)
            binding.btnConfirmOrder.visibility = View.GONE
            when(key){
                ProgressOrder.WaitConfirmOrder.name -> {
                    binding.imgCircleDaDatHang.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    binding.tvTimeOrderDate.text = FormatCurrency.dateTimeFormat.format(value)
                    binding.btnConfirmOrder.visibility = View.VISIBLE
                }

                ProgressOrder.PackagingOrder.name ->{
                    binding.imgCirclePackaging.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    binding.tvTimePackagingOrder.text = FormatCurrency.dateTimeFormat.format(value)

                    binding.lineBottomDaDatHang.setBackgroundColor(color)
                    binding.lineTopPackaging.setBackgroundColor(color)
                }
                ProgressOrder.Shipping.name ->{
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

    private fun confirmOrder(){
        order.orderStatus?.put(ProgressOrder.PackagingOrder.name , Date())
        order.currentStatus = ProgressOrder.PackagingOrder.name
        customDialog.dialogBasic("Chờ một xíu ...")
        OrderService().confirmOrder(order){b->
            if(b){
                Toast.makeText(this,"Đơn hàng đã được xác nhận",Toast.LENGTH_SHORT).show()
                onSendMess()
                initView()
                postNotification()
            }else{
                Toast.makeText(this,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
            }
            customDialog.closeDialog()
        }


    }

    private fun postNotification(){
        val notification = NotificationModel()
        notification.title = "Đơn hàng của bạn đã được xác nhận"
        notification.content = "Đơn hàng \"${binding.tvIdOrder.text}\" đã được người bán chấp nhận, đơn hàng sẽ sớm vận chuyển đến bạn"
        notification.timeSend = Date()
        notification.img = order.carts?.get(0)?.product?.imgPreview
        notification.isRead = false

        order.user?.userId?.let {
            notificationService.addNewNotificationForUser(it,notification){ b ->
                if(b){
                    Log.d(TAG,"Send is success")
                }else{
                    Log.d(TAG,"Send is failed")
                }
            }
        }
    }

    private fun onSendMess(){

        val api =ApiNotificationService.create()
        order.user?.tokenFCM?.let {

            api.sendNotification(DataMessageNotification(it,
                Data("Cập nhật thông tin đơn hàng","Đơn hàng của bạn đang được chẩn bị")))
                .enqueue(object : Callback<DataMessageNotification>{
                    override fun onResponse(
                        call: Call<DataMessageNotification>?,
                        response: Response<DataMessageNotification>?
                    ) {
                        if(response?.isSuccessful == true){
                            Toast.makeText(application,"Gửi thành công",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(application,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DataMessageNotification>?, t: Throwable?) {
                        Toast.makeText(application,t?.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                })
        }

    }


    private fun handleClick(){
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnConfirmOrder.setOnClickListener {
            confirmOrder()
        }
    }
}