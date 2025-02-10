package vn.clothing.store.activities.order

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import vn.clothing.store.BuildConfig
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.adapter.RvCheckoutAdapter
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PaymentMethod
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityTrackOrderBinding
import vn.clothing.store.models.EOrderStatus
import vn.clothing.store.models.ItemQrCodeEmbed
import vn.clothing.store.models.Order
import vn.clothing.store.models.TypeVoucher
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.response.OrderDetailsResponseModel
import vn.clothing.store.networks.response.OrderItemResponseModel
import vn.clothing.store.networks.response.OrderResponseModel
import vn.clothing.store.networks.response.OrderStatus
import vn.clothing.store.utils.FormatCurrency
import vn.clothing.store.utils.Utils
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback
import java.util.Date

class TrackOrderActivity : BaseActivity() {
    companion object{
        private val TAG = TrackOrderActivity::class.java.name
    }
    private lateinit var binding: ActivityTrackOrderBinding
    private var orderId:String?=null

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.header.tvName.text = getString(R.string.label_info_detail)

        orderId = intent.getSerializableExtra(IntentData.KEY_ORDER) as String
        val userId:String = intent.getStringExtra(IntentData.KEY_USER_ID) ?: AppManager.user?.id?:""
        if(orderId == null || orderId.isNullOrEmpty() || userId.isEmpty()) finish()
        if(AppManager.user?.id != userId) {
            CoreConstant.showToast(this,"Yêu cầu không hợp lệ",CoreConstant.ToastType.ERROR)
            finish()
        }
        loadData(orderId!!)
    }

    @SuppressLint("SetTextI18n")
    override fun populateData() {
    }

    override fun setListener() {
        binding.header.toolbar.setNavigationOnClickListener { finish() }

        binding.btnCancelOrder.setOnClickListener {
            openDialogCancelOrder()
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityTrackOrderBinding.inflate(layoutInflater)
            return binding.root
        }

    private fun displayInfo(order:OrderResponseModel){
        binding.tvIdOrder.text = order.orderId
        binding.tvFeeShip.text = FormatCurrency.numberFormat.format(order.feeShip)
        binding.tvOrderDate.text = FormatCurrency.dateTimeFormat.format(order.orderDate)
        binding.tvRealTotal.text = FormatCurrency.numberFormat.format(order.realTotal)
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

        val itemQr = ItemQrCodeEmbed(orderId!!, BuildConfig.APPLICATION_ID,"Order",null,order.userId)
        val itemString = Gson().toJson(itemQr)
        val encrypt = Utils.encodeToBase64(itemString)

        val image = Utils.generateQRCode(encrypt,300,300)
        binding.imgQrcode.setImageBitmap(image)
        Log.w("Phuc","encrypt = $encrypt")
    }

    @SuppressLint("SetTextI18n")
    private fun onResultData(orderDetails:OrderDetailsResponseModel){

        orderDetails.order?.let { displayInfo(it) }

        orderDetails.orderItems?.let {
            val adapter = RvCheckoutAdapter(it)
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            binding.rvOrders.adapter = adapter
            binding.rvOrders.layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(binding.rvOrders.context, DividerItemDecoration.VERTICAL)
            binding.rvOrders.addItemDecoration(dividerItemDecoration)
        }


        if(!orderDetails.orderStatus.isNullOrEmpty()){
            val sortedOrderStatus = orderDetails.orderStatus!!
                .filter { it.updatedAt != null } // Lọc bỏ phần tử có `updatedAt` là null
                .sortedBy { it.updatedAt } // Sắp xếp tăng dần theo `updatedAt`


            binding.lnCancelOrder.visibility = View.GONE
            binding.btnCancelOrder.visibility = View.GONE

            val color = ContextCompat.getColor(this, R.color.colorPrimary)
            for(model in sortedOrderStatus){
                when(model.status){
                    EOrderStatus.PENDING.name ->{
                        binding.btnCancelOrder.visibility = View.VISIBLE
                        binding.llOrdered.visibility = View.VISIBLE
                        binding.imgCircleDaDatHang.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                        binding.tvTimeOrderDate.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                    }
                    EOrderStatus.CANCELLED.name ->{
                        binding.btnCancelOrder.visibility = View.GONE
                        binding.lnCancelOrder.visibility = View.VISIBLE
                        binding.tvTimeCancel.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                        binding.tvReasonCancel.text = binding.tvReasonCancel.text.toString() + model.note
                    }
                    EOrderStatus.PACKING.name ->{
                        binding.imgCirclePackaging.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                        binding.lineBottomDaDatHang.setBackgroundColor(color)
                        binding.lineTopPackaging.setBackgroundColor(color)
                        binding.tvTimePackagingOrder.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                    }
                    EOrderStatus.SHIPPING.name ->{
                        binding.lineTopTransport.setBackgroundColor(color)
                        binding.lineBottomPackaging.setBackgroundColor(color)
                        binding.imgCircleTransport.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                        binding.tvTimeTransport.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                    }
                    EOrderStatus.DELIVERED.name ->{
                        binding.imgCircleDelivered.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                        binding.lineTopDelivered.setBackgroundColor(color)
                        binding.lineBottomTransport.setBackgroundColor(color)
                        binding.tvTimeDelivered.text = FormatCurrency.dateTimeFormat.format(model.updatedAt)
                    }
                }
            }
        }
    }


    private fun loadData(orderId:String){
        APISERVICE.getService(AppManager.token).findOrder(orderId).enqueue(object : BaseCallback<ResponseModel<OrderDetailsResponseModel>>(){
            override fun onSuccess(model: ResponseModel<OrderDetailsResponseModel>) {
                if(model.success && model.data!=null){
                    onResultData(model.data!!)
                }else{
                   CoreConstant.showToast(this@TrackOrderActivity,model.error?.message?:getString(R.string.has_error_please_retry), CoreConstant.ToastType.ERROR)
                }
            }

            override fun onError(message: String) {
              PopupDialog.showDialog(this@TrackOrderActivity,PopupDialog.PopupType.NOTIFICATION,getString(R.string.can_not_success),message){}
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

            if (checkedRadioButtonId != -1 && checkedRadioButtonId!=null && orderId != null) {
                val orderStatus = OrderStatus().apply {
                    userId = AppManager.user?.id
                    orderId = this@TrackOrderActivity.orderId
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
                        binding.btnCancelOrder.visibility = View.GONE
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