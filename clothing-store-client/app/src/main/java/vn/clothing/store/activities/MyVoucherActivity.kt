package vn.clothing.store.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class MyVoucherActivity : BaseActivity() {
    private lateinit var binding:ActivityMyVoucherBinding
    private var adapter:RvMyVoucherAdapter?=null

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = RvMyVoucherAdapter{

        }

        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    }

    override fun populateData() {
        loadVoucher()
        binding.header.tvName.text = getString(R.string.title_header_voucher)
        binding.header.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun setListener() {
        binding.swipeRefresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                loadVoucher()
                binding.swipeRefresh.isRefreshing = false
            },2000)
        }
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
        APISERVICE.getService(AppManager.token).getAllVoucher().enqueue(object : BaseCallback<ResponseModel<List<VoucherModel>>>(){
            override fun onSuccess(model: ResponseModel<List<VoucherModel>>) {
                if(model.success && model.data!=null){
                    if(model.data.isNullOrEmpty()){
                        binding.llNotFound.visibility = View.VISIBLE
                        binding.rvCoupons.visibility = View.GONE
                    }else{
                        binding.llNotFound.visibility = View.GONE
                        binding.rvCoupons.visibility = View.VISIBLE
                    }
                    adapter?.setData(model.data!!)
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