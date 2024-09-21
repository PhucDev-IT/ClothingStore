package vn.clothing.store.networks

import retrofit2.Call
import vn.clothing.store.networks.response.CartResponseModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback


interface IRequestService {


    //=================================================
    //  region PRODUCT
    //=================================================


    //=================================================
    //  endregion
    //=================================================



    //=================================================
    //  region SHOPPING CART
    //=================================================
     fun getAllCart():Call<ResponseModel<CartResponseModel>>

    //=================================================
    //  endregion
    //=================================================
}