package vn.clothing.store.activities

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.adapter.RvMyVoucherAdapter
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityMyVoucherBinding
import vn.clothing.store.models.VoucherModel
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.response.VoucherResponseModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class MyVoucherActivity : BaseActivity() {
    private lateinit var binding:ActivityMyVoucherBinding
    private var adapter:RvMyVoucherAdapter?=null

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = RvMyVoucherAdapter{

        }

        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val dividerItemDecoration = DividerItemDecoration(binding.rvCoupons.context, DividerItemDecoration.VERTICAL)
        binding.rvCoupons.addItemDecoration(dividerItemDecoration)
    }

    override fun populateData() {
        loadVoucher()
        binding.header.tvName.text = getString(R.string.title_header_voucher)
        binding.header.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun setListener() {

    }

    override val layoutView: View
        get() {
            binding = ActivityMyVoucherBinding.inflate(layoutInflater)
            return  binding.root
        }

    private fun onLoading(){
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    private fun onHideLoading(){
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
    }

    private fun loadVoucher(){
        onLoading()
        APISERVICE.getService(AppManager.token).getAllVoucher().enqueue(object : BaseCallback<ResponseModel<VoucherResponseModel>>(){
            override fun onSuccess(model: ResponseModel<VoucherResponseModel>) {
                if(model.success && model.data?.vouchers!=null){
                    adapter?.setData(model.data!!.vouchers!!)
                }else{
                    CoreConstant.showToast(this@MyVoucherActivity,model.error?.message?:"",CoreConstant.ToastType.ERROR)
                }
                onHideLoading()
            }

            override fun onError(message: String) {
                onShowError(message)
                onHideLoading()
            }
        })
    }

    private fun onShowError(message: String){
        PopupDialog.showDialog(this,PopupDialog.PopupType.NOTIFICATION,getString(R.string.can_not_success),message){}
    }
}