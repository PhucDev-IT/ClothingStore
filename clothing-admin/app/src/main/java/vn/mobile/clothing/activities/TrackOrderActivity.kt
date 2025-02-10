package vn.mobile.clothing.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import vn.mobile.clothing.BuildConfig
import vn.mobile.clothing.R
import vn.mobile.clothing.activities.base.BaseActivity
import vn.mobile.clothing.adapters.RvCheckoutAdapter
import vn.mobile.clothing.common.AppManager
import vn.mobile.clothing.common.CoreConstant
import vn.mobile.clothing.common.IntentData
import vn.mobile.clothing.common.PopupDialog
import vn.mobile.clothing.databinding.ActivityTrackOrderBinding
import vn.mobile.clothing.models.EOrderStatus
import vn.mobile.clothing.models.ItemQrCodeEmbed
import vn.mobile.clothing.models.PaymentMethod
import vn.mobile.clothing.models.TypeVoucher
import vn.mobile.clothing.network.ApiService.Companion.APISERVICE
import vn.mobile.clothing.network.response.OrderDetailsResponseModel
import vn.mobile.clothing.network.response.OrderResponseModel
import vn.mobile.clothing.network.response.OrderStatus
import vn.mobile.clothing.network.response.ResponseModel
import vn.mobile.clothing.network.rest.BaseCallback
import vn.mobile.clothing.utils.FormatCurrency
import vn.mobile.clothing.utils.Utils

import java.util.Date

class TrackOrderActivity : BaseActivity() {
    companion object{
        private val TAG = TrackOrderActivity::class.java.name
    }
    private lateinit var binding: ActivityTrackOrderBinding
    private var order: OrderResponseModel?=null
    private var orderId:String?=null
    private var isShowControlButton = true

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.header.tvName.text = getString(R.string.label_info_detail)

        orderId = intent.getSerializableExtra(IntentData.KEY_ORDER) as String
        if(orderId == null || orderId.isNullOrEmpty()){
            finish()
        }

    }

    @SuppressLint("SetTextI18n")
    override fun populateData() {
        loadData(orderId!!)
    }

    override fun setListener() {
        binding.header.toolbar.setNavigationOnClickListener { finish() }

        binding.btnCancel.setOnClickListener {
            openDialogCancelOrder()
        }
        binding.btnConfirm.setOnClickListener {
            confirmOrder()
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityTrackOrderBinding.inflate(layoutInflater)
            return binding.root
        }

    private fun displayInfo(order: OrderResponseModel){
        binding.tvIdOrder.text = order.orderId
        binding.tvNameCustomer.text = order.userId
        binding.tvFeeShip.text = FormatCurrency.numberFormat.format(order.feeShip)
        binding.tvOrderDate.text = FormatCurrency.dateTimeFormat.format(order.orderDate)
        binding.tvTotalMoney.text = FormatCurrency.numberFormat.format(order.realTotal)
        binding.tvTotal.text = FormatCurrency.numberFormat.format(order.total)
        binding.tvUserVoucher.text = FormatCurrency.numberFormat.format(order.discount)

        binding.tvMethodPayment.text = if(order.paymentMethod == PaymentMethod.HOME.name){
            getString(R.string.label_home_payment)
        }else if(order.paymentMethod == PaymentMethod.ZALOPAY.name){
            getString(R.string.label_zalo_pay_payment)
        }else{
            getString(R.string.label_momo_payment)
        }

        try{
            val delivery = order.shippingAddress!!.split('|')
            binding.tvContact.text = "${delivery[0]} - ${delivery[1]}"
            binding.tvAddress.text = delivery[2]
        }catch (e:Exception){
            Log.e(TAG,"Fail: ${e.message}")
        }


        val itemQr = ItemQrCodeEmbed(orderId!!,BuildConfig.APPLICATION_ID,"Order",null,order.userId)
        val itemString = Gson().toJson(itemQr)
        val encrypt = Utils.encodeToBase64(itemString)

        val image = Utils.generateQRCode(encrypt,300,300)
        binding.imgQrcode.setImageBitmap(image)
        Log.w("Phuc","encrypt = $encrypt")
    }

    @SuppressLint("SetTextI18n")
    private fun onResultData(orderDetails: OrderDetailsResponseModel){

        orderDetails.order?.let { displayInfo(it)
            this.order = it}

        orderDetails.orderItems?.let {
            val adapter = RvCheckoutAdapter(it)
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            binding.rvOrders.adapter = adapter
            binding.rvOrders.layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(binding.rvOrders.context, DividerItemDecoration.VERTICAL)
            binding.rvOrders.addItemDecoration(dividerItemDecoration)
        }

        if(!orderDetails.orderStatus.isNullOrEmpty()){
            binding.llOrdered.visibility = View.GONE
            binding.lnCancelOrder.visibility = View.GONE
            binding.llPacking.visibility = View.GONE
            binding.llShipping.visibility = View.GONE
            binding.llDelivered.visibility = View.GONE

            val sortedOrderStatus = orderDetails.orderStatus!!
                .filter { it.updatedAt != null } // Lọc bỏ phần tử có `updatedAt` là null
                .sortedBy { it.updatedAt } // Sắp xếp tăng dần theo `updatedAt`

            for(model in sortedOrderStatus){
                when(model.status){
                    EOrderStatus.PENDING.name ->{
                        binding.llControl.visibility = View.VISIBLE
                        binding.btnConfirm.text = "Xác nhận"
                        binding.llOrdered.visibility = View.VISIBLE
                        binding.tvTimeOrderDate.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                    }

                    EOrderStatus.CANCELLED.name ->{
                        binding.llControl.visibility = View.GONE
                        binding.lnCancelOrder.visibility = View.VISIBLE
                        binding.tvTimeCancel.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                        binding.tvReasonCancel.text = binding.tvReasonCancel.text.toString() + model.note
                    }
                    EOrderStatus.PACKING.name ->{
                        binding.llControl.visibility = View.VISIBLE
                        binding.btnConfirm.text = "Giao hàng"
                        binding.llPacking.visibility = View.VISIBLE
                        binding.tvTimePackagingOrder.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                    }
                    EOrderStatus.SHIPPING.name ->{
                        binding.llControl.visibility = View.VISIBLE
                        binding.btnConfirm.text = "Thành công"
                        binding.llShipping.visibility = View.VISIBLE
                        binding.tvTimeTransport.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                    }
                    EOrderStatus.DELIVERED.name ->{
                        binding.llControl.visibility = View.GONE
                        binding.llDelivered.visibility = View.VISIBLE
                        binding.tvTimeDelivered.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                    }
                }
            }
        }
        if(!isShowControlButton){
            binding.llControl.visibility = View.GONE
        }
    }


    private fun loadData(id:String){
        PopupDialog.showDialogLoading(this)
        APISERVICE.getService(AppManager.token).findOrder(id).enqueue(object : BaseCallback<ResponseModel<OrderDetailsResponseModel>>(){
            override fun onSuccess(model: ResponseModel<OrderDetailsResponseModel>) {
                PopupDialog.closeDialog()
                if(model.success && model.data!=null){
                    onResultData(model.data!!)
                }else{
                   CoreConstant.showToast(this@TrackOrderActivity,model.error?.message?:getString(R.string.has_error_please_retry), CoreConstant.ToastType.ERROR)
                }
            }

            override fun onError(message: String) {
                PopupDialog.closeDialog()
              PopupDialog.showDialog(this@TrackOrderActivity,PopupDialog.PopupType.NOTIFICATION,getString(R.string.can_not_success),message){}
            }
        })
    }


    private fun confirmOrder(){
        if(order == null) return
        var note = "Đơn hàng ${order?.orderId} đã được xác nhận"
        var status :String = if(binding.btnConfirm.text.toString() == "Xác nhận"){
            EOrderStatus.PACKING.name
        }else if(binding.btnConfirm.text.toString() == "Giao hàng"){
            note = "Đơn hàng ${order?.orderId} của bạn đang được vận chuyển"
            EOrderStatus.SHIPPING.name
        }else if(binding.btnConfirm.text.toString() == "Thành công"){
            note = "Giao hàng thành công đơn hàng ${order?.orderId}, trải nghiệm chất lượng sản phẩm"
            EOrderStatus.DELIVERED.name
        }else{
            "Unknown error"
        }
        val orderStatus = OrderStatus().apply {
            orderId = order!!.orderId
            userId = order!!.userId
            this.status = status
            this.note = note
        }
        PopupDialog.showDialogLoading(this)
        APISERVICE.getService(AppManager.token).updateOrderStatus(orderStatus).enqueue(object : BaseCallback<ResponseModel<OrderStatus>>(){
            override fun onSuccess(model: ResponseModel<OrderStatus>) {
                PopupDialog.closeDialog()
                if(model.success && model.data!=null){
                    isShowControlButton = false
                    CoreConstant.showToast(this@TrackOrderActivity,"Xác nhận thành công",CoreConstant.ToastType.SUCCESS)
                    binding.llControl.visibility = View.GONE
                    loadData(orderId!!)
                }else{
                    CoreConstant.showToast(this@TrackOrderActivity,"Có lỗi xảy ra: ${model.error?.message}",CoreConstant.ToastType.ERROR)
                }
            }

            override fun onError(message: String) {
                PopupDialog.closeDialog()
                PopupDialog.showDialog(this@TrackOrderActivity,PopupDialog.PopupType.NOTIFICATION,null,message,{})
            }
        })
    }




    private fun openDialogCancelOrder(){
        val dialog = Dialog(this)
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

        btnConfirm?.setOnClickListener {
            val checkedRadioButtonId = radioGroup?.checkedRadioButtonId

            if (checkedRadioButtonId != -1 && checkedRadioButtonId!=null && order?.orderId != null) {
                val orderStatus = OrderStatus().apply {
                    orderId = order?.orderId
                    status = EOrderStatus.CANCELLED.name
                    note = radioGroup.findViewById<RadioButton>(checkedRadioButtonId).text.toString()
                }
                PopupDialog.showDialogLoading(this@TrackOrderActivity)
                APISERVICE.getService(AppManager.token).updateOrderStatus(orderStatus).enqueue(object : BaseCallback<ResponseModel<OrderStatus>>(){
                    override fun onSuccess(model: ResponseModel<OrderStatus>) {
                        if(model.success && model.data!=null){
                            Toast.makeText(this@TrackOrderActivity,"Hủy đơn hàng thành công",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@TrackOrderActivity,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
                        }
                        binding.llControl.visibility = View.GONE
                        dialog.dismiss()
                        PopupDialog.closeDialog()
                    }

                    override fun onError(message: String) {
                        PopupDialog.closeDialog()
                        dialog.dismiss()
                        PopupDialog.showDialog(this@TrackOrderActivity,PopupDialog.PopupType.NOTIFICATION,getString(R.string.can_not_success),message){}
                    }
                })
            } else {
                Toast.makeText(this, "Chọn lý do trước khi tiếp tục", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()

    }

}