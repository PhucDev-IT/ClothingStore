package vn.clothing.store.activities.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import org.json.JSONObject
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.activities.settings.SettingsMainActivity
import vn.clothing.store.adapter.RvPayOrderAdapter
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PaymentMethod
import vn.clothing.store.common.PopupDialog

import vn.clothing.store.databinding.ActivityPayOrderBinding
import vn.clothing.store.interfaces.PayOrderContract
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.models.VoucherModel
import vn.clothing.store.networks.request.OrderItemRequestModel
import vn.clothing.store.networks.request.OrderRequestModel
import vn.clothing.store.networks.response.CartResponseModel
import vn.clothing.store.presenter.PayOrderPresenter
import vn.clothing.store.utils.FormatCurrency
import vn.clothing.store.zalopay.Api.CreateOrder
import vn.clothing.store.zalopay.Constant.AppInfo
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener

class PayOrderActivity : BaseActivity(), PayOrderContract.View {
    private lateinit var binding: ActivityPayOrderBinding
    private lateinit var presenter: PayOrderPresenter
    private var address: DeliveryInformation? = null
    private lateinit var cartItems: ArrayList<CartResponseModel.CartItemResponseModel>
    private var adapter: RvPayOrderAdapter? = null
    private val FEESHIP = 20000f
    private var totalMoney: Double = 0.0
    private var voucher:VoucherModel?=null
    private var paymentMethod:PaymentMethod = PaymentMethod.ZALOPAY
    private var handler = Handler(Looper.getMainLooper())

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (intent.hasExtra(IntentData.KEY_LIST_CART_ITEM)) {
            cartItems =
                intent.getSerializableExtra(IntentData.KEY_LIST_CART_ITEM) as ArrayList<CartResponseModel.CartItemResponseModel>
        } else {
            CoreConstant.showToast(
                this,
                getString(R.string.has_error_please_retry),
                CoreConstant.ToastType.ERROR
            )
            finish()
        }
        presenter = PayOrderPresenter(this)
        adapter = RvPayOrderAdapter(cartItems)
        binding.rvProduct.adapter = adapter
        val dividerItemDecoration =
            DividerItemDecoration(binding.rvProduct.context, DividerItemDecoration.VERTICAL)
        binding.rvProduct.addItemDecoration(dividerItemDecoration)
        binding.rvProduct.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val policy :StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
    }

    override fun populateData() {
        presenter.getDefaultAddress()
        initUi()
    }

    override fun setListener() {
        binding.btnBuyProduct.setOnClickListener {
            payment()
        }

        binding.header.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSelectAddress.setOnClickListener {
            startActivity(Intent(this,SettingsMainActivity::class.java))
        }

        binding.tvChooseVoucher.setOnClickListener {
            val intent = Intent(this, SelectCouponActivity::class.java)
            intent.putExtra(IntentData.KEY_VOUCHER,voucher)
            startActivityForResult(intent, REQUEST_SELECT_VOUCHER)
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityPayOrderBinding.inflate(layoutInflater)
            return binding.root
        }


    private fun initUi() {
        binding.tvShippingCost.text = FormatCurrency.numberFormat.format(FEESHIP)
        calculate()
    }

    private fun calculate() {
        totalMoney = 0.0
        for (item in cartItems) {
            totalMoney += item.price!! * item.quantity!!
        }
        binding.tvTotal.text = FormatCurrency.numberFormat.format(totalMoney)

        binding.tvRealTotal.text = FormatCurrency.numberFormat.format(totalMoney + FEESHIP)
    }

    private fun payment() {
        if (address == null) {
            return
        }
        val orderItems = arrayListOf<OrderItemRequestModel>()
        for (item in cartItems) {
            orderItems.add(
                OrderItemRequestModel(
                    item.quantity!!,
                    item.price!!,
                    item.color!!,
                    item.size!!,
                    item.productId!!
                )
            )
        }
        val deliveryDetails = "${address!!.fullName}|${address!!.numberPhone}|${address!!.details}"
        val orderRequestModel = OrderRequestModel(
            totalMoney,
            totalMoney + FEESHIP,
            deliveryDetails,
            null,
            feeShip = FEESHIP,
            orderItems
        )
        val cartIds = cartItems.map { it.id!! }
        if(paymentMethod == PaymentMethod.ZALOPAY){
            payWithZaloPay(orderRequestModel,cartIds)
        }
    }

    private fun payWithZaloPay(orderRequestModel: OrderRequestModel, cardIds:List<Int>){
        onShowLoading()
        val orderApi = CreateOrder()
        try{
            val data:JSONObject = orderApi.createOrder(totalMoney.toInt().toString())
            val code = data.getString("returncode")
            if(code == "1"){
                val token = data.getString("zptranstoken")
                ZaloPaySDK.getInstance().payOrder(this@PayOrderActivity,token,"demozpdk://app", object : PayOrderListener{
                    override fun onPaymentSucceeded(transactionId: String?, transToken: String?, appTransID: String?) {
                        handler.post {
                            onHideLoading()
                            presenter.payment(orderRequestModel, cardIds)
                        }
                    }

                    override fun onPaymentCanceled(zpTransToken: String?, appTransID: String?) {
                        handler.post {
                            onHideLoading()
                            Toast.makeText(
                                this@PayOrderActivity,
                                "Hủy thanh toán",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPaymentError(zaloPayError: ZaloPayError?, zpTransToken: String?, appTransID: String?) {
                        handler.post {
                            onHideLoading()
                            Toast.makeText(
                                this@PayOrderActivity,
                                "Lỗi thanh toán",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }else{
                Toast.makeText(this@PayOrderActivity,"Khác 1",Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            onHideLoading()
            e.printStackTrace();
        }
    }

    //====================================
    // region PayOrderContract.View
    //=====================================
    override fun onShowLoading() {
        PopupDialog.showDialogLoading(this)
    }

    override fun onHideLoading() {
        PopupDialog.closeDialog()
    }

    override fun onShowPopup(message: String, type: PopupDialog.PopupType) {
        PopupDialog.showDialog(this, type, null, message) {}
    }

    override fun onPaymentSuccess(orderId: String) {
        CoreConstant.showToast(this, getString(R.string.success), CoreConstant.ToastType.SUCCESS)
        startActivity(Intent(this, PurchaseHistoryActivity::class.java))
        finish()
    }

    override fun onResultAddress(addresses: List<DeliveryInformation>?) {
        if (!addresses.isNullOrEmpty()) {
            binding.btnBuyProduct.isEnabled = true
            for (address in addresses) {
                if (address.isDefault) {
                    this.address
                    break
                }
            }
            if (address == null) {
                this.address = addresses[0]
            }
            displayAddress(address!!)
        } else {
            binding.btnBuyProduct.isEnabled = false
            PopupDialog.showDialog(
                this,
                PopupDialog.PopupType.CONFIRM,
                null,
                getString(R.string.please_add_address)
            ) {

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayAddress(address: DeliveryInformation) {
        binding.tvAddress.text = address.details
        binding.tvContract.text = "${address.numberPhone} - ${address.fullName}"
    }

    //====================================
    // endregion PayOrderContract.View
    //=====================================

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                REQUEST_SELECT_VOUCHER -> {
                    val voucher = data?.getSerializableExtra(IntentData.KEY_VOUCHER) as VoucherModel?
                    this.voucher = voucher
                    binding.tvChooseVoucher.text = voucher?.id
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }

    companion object{
        private const val REQUEST_SELECT_VOUCHER = 235
    }
}