package vn.clothing.store.presenter

import vn.clothing.store.adapter.RvPurchasedHistoryAdapter
import vn.clothing.store.common.AppManager
import vn.clothing.store.interfaces.PurchasedHistoryContract
import vn.clothing.store.models.Order
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.response.PurchaseHistoryResponseModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class PurchaseHistoryPresenter(private var view: PurchasedHistoryContract.View?) :
    PurchasedHistoryContract.Presenter {
        var adapter: RvPurchasedHistoryAdapter
    init {
        adapter = RvPurchasedHistoryAdapter {

        }
    }
    private var totalPage: Int = 0
    private val limit = 10
    private var page = 1


    override fun getOrders(state: String) {
        view?.onShowLoading()
        APISERVICE.getService(AppManager.token).getOrders(state, limit, page)
            .enqueue(object : BaseCallback<ResponseModel<PurchaseHistoryResponseModel>>() {
                override fun onSuccess(model: ResponseModel<PurchaseHistoryResponseModel>) {
                    if(model.success && model.data!=null){
                        view?.onHideLoading()
                        adapter.setData(model.data!!.orders!!)
                    }else{
                        view?.onShowError(model.error?.message?:"")
                    }
                }

                override fun onError(message: String) {
                    view?.onShowError(message)
                }
            })
    }
}