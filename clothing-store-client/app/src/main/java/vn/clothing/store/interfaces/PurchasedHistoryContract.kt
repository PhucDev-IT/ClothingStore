package vn.clothing.store.interfaces

import vn.clothing.store.networks.response.OrderResponseModel

interface PurchasedHistoryContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun onShowError(message:String)
        fun onRequestSeenDetail(order:OrderResponseModel)
        fun onLoadedData(data:List<OrderResponseModel>)
        fun onNotFoundItem()
    }

    interface Presenter{
        fun getFirstOrder(state:String)
    }
}