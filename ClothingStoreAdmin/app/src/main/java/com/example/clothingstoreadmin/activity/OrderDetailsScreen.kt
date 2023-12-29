package com.example.clothingstoreadmin.activity

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.adapter.CustomDialog
import com.example.clothingstoreadmin.adapter.RvCheckoutAdapter
import com.example.clothingstoreadmin.databinding.ActivityOrderDetailsScreenBinding
import com.example.clothingstoreadmin.model.FormatCurrency
import com.example.clothingstoreadmin.model.OrderModel
import com.example.clothingstoreadmin.model.ProgressOrder
import com.example.clothingstoreadmin.model.TypeVoucher
import com.example.clothingstoreadmin.service.OrderService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Date
import java.util.Locale


class OrderDetailsScreen : AppCompatActivity() {
    private lateinit var binding:ActivityOrderDetailsScreenBinding
    private lateinit var adapter: RvCheckoutAdapter
    private lateinit var order:OrderModel
    private lateinit var customDialog: CustomDialog
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

    private fun confirmOrder(){
        order.orderStatus?.put(ProgressOrder.PackagingOrder.name , Date())
        order.currentStatus = ProgressOrder.PackagingOrder.name
        customDialog.dialogBasic("Chờ một xíu ...")
        OrderService().confirmOrder(order){b->
            if(b){
                Toast.makeText(this,"Đơn hàng đã được xác nhận",Toast.LENGTH_SHORT).show()
                onSendMess()
                initView()
            }else{
                Toast.makeText(this,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
            }
            customDialog.closeDialog()
        }


    }

    private fun onSendMess(){
         val JSON: MediaType = "application/json".toMediaType()
        val client : OkHttpClient = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val requestBody = FormBody.Builder()
            .add("title", "Cập nhật đơn hàng")
            .add("key2", "Đơn hàng đã được xác nhận")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization","key AAAAbjZgOC4:APA91bHTS6nx9yFOrB4OiDR-72eWoQdn4KCINa6vNnx5P8OIgfUG9bf5vnvYoxInwq0I8lozT0MzqIjEPnADWZlvn8e6MWNiN42ZdD0erCipDoYR3tw1yGIhsae_q49bM0rO2h38KT8y")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Xử lý khi gặp lỗi
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
               Toast.makeText(applicationContext,responseData,Toast.LENGTH_SHORT).show()
            }})
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