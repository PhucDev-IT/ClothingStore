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
            view?.onRequestSeenDetail(it)
        }
    }

    private var totalPage: Int = 0
    private val limit = 10
    private var page = 1

    override fun getFirstOrder(state: String) {
        totalPage = 0
        page = 1
        adapter.clearData()
        getOrders(state, limit, page)
    }

   private fun getOrders(state: String, limitOrder: Int, currentPage: Int) {
        view?.onShowLoading()
        APISERVICE.getService(AppManager.token).getOrders(state, limitOrder, currentPage)
            .enqueue(object : BaseCallback<ResponseModel<PurchaseHistoryResponseModel>>() {
                override fun onSuccess(model: ResponseModel<PurchaseHistoryResponseModel>) {
                    if (model.success && model.data != null) {
                        view?.onHideLoading()
                        totalPage = model.data!!.pagination!!.totalPages!!
                        if(!model.data?.orders.isNullOrEmpty()) {
                            adapter.setData(model.data!!.orders!!)
                            view?.onLoadedData(model.data!!.orders!!)
                        }else{
                            view?.onNotFoundItem()
                        }
                    } else {
                        view?.onShowError(model.error?.message ?: "")
                        view?.onNotFoundItem()
                    }
                }

                override fun onError(message: String) {
                    view?.onShowError(message)
                    view?.onNotFoundItem()
                }
            })
    }

    fun getNextPage(status:String){
        if(page>totalPage) return
        page++
        getOrders(status,limit, page)

    }
}