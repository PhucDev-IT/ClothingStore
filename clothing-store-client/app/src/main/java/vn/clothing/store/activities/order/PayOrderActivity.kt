package vn.clothing.store.activities.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.adapter.RvPayOrderAdapter
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PopupDialog

import vn.clothing.store.databinding.ActivityPayOrderBinding
import vn.clothing.store.interfaces.PayOrderContract
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.networks.request.OrderItemRequestModel
import vn.clothing.store.networks.request.OrderRequestModel
import vn.clothing.store.networks.response.CartResponseModel
import vn.clothing.store.presenter.PayOrderPresenter
import vn.clothing.store.utils.FormatCurrency

class PayOrderActivity : BaseActivity(), PayOrderContract.View {
    private lateinit var binding: ActivityPayOrderBinding
    private lateinit var presenter: PayOrderPresenter
    private var address: DeliveryInformation? = null
    private lateinit var cartItems: ArrayList<CartResponseModel.CartItemResponseModel>
    private var adapter: RvPayOrderAdapter? = null
    private val FEESHIP = 20000
    private var totalMoney: Double = 0.0

    override fun initView() {
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
    }

    override fun populateData() {
        presenter.getDefaultAddress()
        initUi()
    }

    override fun setListener() {
        binding.btnBuyProduct.setOnClickListener {
            payment()
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

        val orderRequestModel = OrderRequestModel(
            totalMoney,
            totalMoney + FEESHIP,
            address!!.details!!,
            null,
            "PENDING",
            orderItems
        )
        presenter.payment(orderRequestModel)
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
        PopupDialog.showDialog(this, type, null, message){}
    }

    override fun onPaymentSuccess(orderId: String) {
        CoreConstant.showToast(this,getString(R.string.success),CoreConstant.ToastType.SUCCESS)
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
            if(address == null){
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

}