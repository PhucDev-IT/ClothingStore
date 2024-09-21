package vn.clothing.store.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.clothing.store.interfaces.ShoppingCartContract
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.service.CartService

class ShoppingCartPresenter(private var view: ShoppingCartContract.View?) :
    ShoppingCartContract.Presenter {
    private var cartService: CartService? = null


    init {
        cartService = CartService()
    }

    private fun loadData() {
        view?.onShowLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = cartService?.getAllCart()
                withContext(Dispatchers.Main) {
                    if (response != null && response.success && response.data != null) {
                        view?.onLoadedData(response.data!!)
                    }
                }

            } catch (e: Exception) {

            } finally {
                withContext(Dispatchers.Main) {
                    view?.onHiddenLoading()
                }
            }
        }
    }
}