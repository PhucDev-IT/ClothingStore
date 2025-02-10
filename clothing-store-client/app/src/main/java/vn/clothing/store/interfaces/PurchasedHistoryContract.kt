package vn.clothing.store.interfaces

import vn.clothing.store.networks.response.OrderResponseModel

interface PurchasedHistoryContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun onShowError(message:String)
        fun onRequestSeenDetail(orderId:String)
        fun onLoadedData()
        fun onNotFoundItem()
    }

    interface Presenter{
        fun getFirstOrder(state:String)
    }
}