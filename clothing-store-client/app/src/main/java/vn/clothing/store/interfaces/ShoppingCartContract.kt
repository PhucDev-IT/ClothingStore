package vn.clothing.store.interfaces

import vn.clothing.store.models.CartModel
import vn.clothing.store.networks.response.CartResponseModel

interface ShoppingCartContract {
    interface View{
        fun onShowLoading()
        fun onHiddenLoading()
        fun showError(message:String?)
        fun onResultCarts(cart:CartResponseModel)
    }

    interface Presenter{
        fun getAllCarts()
        fun onDestroy()
    }
}