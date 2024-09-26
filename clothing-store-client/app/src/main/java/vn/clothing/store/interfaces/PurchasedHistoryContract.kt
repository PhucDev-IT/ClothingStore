package vn.clothing.store.interfaces

interface PurchasedHistoryContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun onShowError(message:String)
    }

    interface Presenter{
        fun getOrders(state:String)
    }
}