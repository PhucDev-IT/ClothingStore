package vn.mobile.clothing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.mobile.clothing.common.AppManager
import vn.mobile.clothing.network.ApiService
import vn.mobile.clothing.network.response.PreviewOrderResponseModel
import vn.mobile.clothing.network.response.ResponseModel
import vn.mobile.clothing.network.response.StatisticalRevenueYear
import vn.mobile.clothing.network.response.StatisticalStatusOrderResModel
import vn.mobile.clothing.network.response.TopProductResponseModel
import vn.mobile.clothing.network.rest.BaseCallback

class DashboardViewModel : ViewModel() {

    private val _statisticalOrders = MutableLiveData<List<StatisticalStatusOrderResModel>>()
    val statisticalOrders: LiveData<List<StatisticalStatusOrderResModel>> = _statisticalOrders

    private val _statisticalRevenue = MutableLiveData<List<StatisticalRevenueYear>>()
    val statisticalRevenue: LiveData<List<StatisticalRevenueYear>> = _statisticalRevenue

    private val _topProducts = MutableLiveData<List<TopProductResponseModel>>()
    val topProducts: LiveData<List<TopProductResponseModel>> = _topProducts

    fun loadData() {
        loadStatisticalOrders()
        statisticalRevenueOfYear()
        statisticalTopProduct()
    }

    private fun loadStatisticalOrders(){
        ApiService.APISERVICE.getService(AppManager.token).getStatistical().enqueue(object : BaseCallback<ResponseModel<List<StatisticalStatusOrderResModel>>>(){
            override fun onSuccess(model: ResponseModel<List<StatisticalStatusOrderResModel>>) {
                if(model.success && model.data!=null){
                    _statisticalOrders.value = model.data
                }
            }
            override fun onError(message: String) {

            }
        })
    }

    fun statisticalRevenueOfYear(){
        ApiService.APISERVICE.getService(AppManager.token).getStatisticalRevenue().enqueue(object : BaseCallback<ResponseModel<List<StatisticalRevenueYear>>>(){
            override fun onSuccess(model: ResponseModel<List<StatisticalRevenueYear>>) {
                if(model.success && model.data!=null){
                    _statisticalRevenue.value = model.data
                }
            }
            override fun onError(message: String) {

            }
        })
    }


    fun statisticalTopProduct(){
        ApiService.APISERVICE.getService(AppManager.token).getTopProducts().enqueue(object : BaseCallback<ResponseModel<List<TopProductResponseModel>>>(){
            override fun onSuccess(model: ResponseModel<List<TopProductResponseModel>>) {
                if(model.success && model.data!=null){
                    _topProducts.value = model.data
                }
            }
            override fun onError(message: String) {

            }
        })
    }

}