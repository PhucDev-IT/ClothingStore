package vn.clothing.store.activities.order

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.adapter.RvSelectedVoucherAdapter
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivitySelectCouponBinding
import vn.clothing.store.models.VoucherModel
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.response.VoucherResponseModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class SelectCouponActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectCouponBinding
    private var adapter: RvSelectedVoucherAdapter? = null
    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val voucher = intent.getStringExtra(IntentData.KEY_VOUCHER) as VoucherModel?
        adapter = RvSelectedVoucherAdapter(voucher?.id) {

        }
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.layoutManager = linearLayoutManager
    }

    override fun populateData() {
        binding.layoutHeader.tvName.text = getString(R.string.title_header_voucher)
        getData()
    }

    override fun setListener() {
        binding.layoutHeader.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override val layoutView: View
        get() {
            binding = ActivitySelectCouponBinding.inflate(layoutInflater)
            return binding.root
        }


    private fun getData() {
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility = View.VISIBLE

        APISERVICE.getService(AppManager.token).getAllVoucher().enqueue(object : BaseCallback<ResponseModel<List<VoucherModel>>>(){
            override fun onSuccess(model: ResponseModel<List<VoucherModel>>) {
                if(model.success && model.data!=null){
                    adapter?.setData(model.data!!)
                }
                onHideLoading()
            }

            override fun onError(message: String) {
                onHideLoading()
                PopupDialog.showDialog(this@SelectCouponActivity,PopupDialog.PopupType.NOTIFICATION,getString(R.string.can_not_success),message){}
            }
        })

    }

    private fun onHideLoading(){
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
    }
}
