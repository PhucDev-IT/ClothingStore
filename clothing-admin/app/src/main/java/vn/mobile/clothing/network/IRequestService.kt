package vn.mobile.clothing.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import vn.mobile.clothing.models.User
import vn.mobile.clothing.network.request.LoginRequest
import vn.mobile.clothing.network.request.RegisterRequestModel
import vn.mobile.clothing.network.response.LoginResponseModel
import vn.mobile.clothing.network.response.OrderDetailsResponseModel
import vn.mobile.clothing.network.response.PurchaseHistoryResponseModel
import vn.mobile.clothing.network.response.ResponseModel


interface IRequestService {

    //=================================================
    //  region AUTHENTICATION
    //=================================================
    @POST("api/auth/admin/login")
    fun login(@Body login: LoginRequest):Call<ResponseModel<LoginResponseModel>>


    @POST("api/auth/register")
    fun signUp(@Body request: RegisterRequestModel):Call<ResponseModel<User>>
    //=================================================
    //  endregion
    //=================================================

    //==================== ORDER ======================================//
    @GET("/api/orders/")
    fun getOrders(@Query("status") status:String, @Query("limit") limit:Int, @Query("page") page:Int):Call<ResponseModel<PurchaseHistoryResponseModel>>
    @GET("/api/orders/{id}")
    fun findOrder(@Path("id") id:String):Call<ResponseModel<OrderDetailsResponseModel>>

}