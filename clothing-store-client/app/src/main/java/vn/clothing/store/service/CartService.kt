package vn.clothing.store.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await
import vn.clothing.store.interfaces.ShoppingCartContract
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.response.CartResponseModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class CartService {
    suspend fun getAllCart():ResponseModel<CartResponseModel>{
       return APISERVICE.getService().getAllCart().await()
    }
}