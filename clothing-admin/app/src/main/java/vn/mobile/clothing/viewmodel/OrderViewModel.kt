package vn.mobile.clothing.viewmodel

import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import vn.mobile.clothing.common.AppManager
import vn.mobile.clothing.network.ApiService
import vn.mobile.clothing.network.response.OrderResponseModel
import vn.mobile.clothing.network.response.PurchaseHistoryResponseModel
import vn.mobile.clothing.network.response.ResponseModel
import vn.mobile.clothing.network.rest.BaseCallback

class OrderViewModel : ViewModel() {

    private val _orders = MutableLiveData<List<OrderResponseModel>>()
    val orders:LiveData<List<OrderResponseModel>> = _orders

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading:LiveData<Boolean> = _isLoading

    private var totalPage: Int = 0
    private val limit = 10
    private var page = 1

    fun getFirstOrder(state: String) {
        totalPage = 0
        page = 1
        getOrders(state, limit, page)
    }

    private fun getOrders(state: String, limitOrder: Int, currentPage: Int) {
        showLoading()
        ApiService.APISERVICE.getService(AppManager.token).getOrders(state, limitOrder, currentPage)
            .enqueue(object : BaseCallback<ResponseModel<PurchaseHistoryResponseModel>>() {
                override fun onSuccess(model: ResponseModel<PurchaseHistoryResponseModel>) {
                    hideLoading()
                    if (model.success && model.data != null) {
                        totalPage = model.data!!.pagination!!.totalPages!!
                        if(!model.data?.orders.isNullOrEmpty()) {
                            _orders.value = model.data!!.orders!!
                        }else{

                        }
                    } else {
//                        view?.onShowError(model.error?.message ?: "")
//                        view?.onNotFoundItem()
                    }
                }

                override fun onError(message: String) {
                    hideLoading()
//                    view?.onShowError(message)
//                    view?.onNotFoundItem()
                }
            })
    }

    fun getNextPage(status:String){
        if(page>totalPage) return
        page++
        getOrders(status,limit, page)

    }

    private fun showLoading(){
        _isLoading.value = true
    }

    private fun hideLoading(){
        _isLoading.value = false
        Log.d("YourViewModel", "isLoading: ${isLoading.value}")
    }

    companion object{
        @JvmStatic
        @BindingAdapter("shimmer_layout")
        fun setVisibility(view: ShimmerFrameLayout, isLoading: Boolean?) {
            view.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
    }
}