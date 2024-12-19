package vn.clothing.store.presenter

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.clothing.store.common.AppManager
import vn.clothing.store.interfaces.ShoppingCartContract
import vn.clothing.store.models.CartModel
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.request.DeleteCartRequest
import vn.clothing.store.networks.response.CartResponseModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback


class ShoppingCartPresenter(private var view: ShoppingCartContract.View?) :
    ShoppingCartContract.Presenter {
    companion object{
        private val TAG = ShoppingCartPresenter::class.java.name
    }
    override fun getAllCarts(){
        view?.onShowLoading()
        APISERVICE.getService(AppManager.token).getAllCart().enqueue(object : BaseCallback<ResponseModel<CartResponseModel>>(){
            override fun onSuccess(model: ResponseModel<CartResponseModel>) {
                view?.onHiddenLoading()
                view?.onResultCarts(model.data)
            }

            override fun onError(message: String) {
                view?.showError(message)
            }
        })
    }

    fun removeCart(id:Int){
        view?.onShowLoading()
        APISERVICE.getService(AppManager.token).deleteCart(DeleteCartRequest(listOf(id))).enqueue(object : BaseCallback<ResponseModel<Boolean>>(){
            override fun onSuccess(model: ResponseModel<Boolean>) {
                view?.onHiddenLoading()
            }

            override fun onError(message: String) {
                view?.onHiddenLoading()
            }
        })
    }

    override fun onDestroy() {
        view =null
    }
}