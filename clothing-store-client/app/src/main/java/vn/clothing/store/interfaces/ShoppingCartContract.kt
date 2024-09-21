package vn.clothing.store.interfaces

import vn.clothing.store.networks.response.CartResponseModel

interface ShoppingCartContract {
    interface View{
        fun onShowLoading()
        fun onHiddenLoading()
        fun onLoadedData(data:CartResponseModel)
        fun showError(message:String?)
    }

    interface Presenter{

    }
}