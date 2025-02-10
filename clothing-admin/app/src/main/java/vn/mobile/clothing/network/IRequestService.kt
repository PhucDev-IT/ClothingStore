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
import vn.mobile.clothing.models.VoucherModel
import vn.mobile.clothing.network.request.AddVoucherRequestModel
import vn.mobile.clothing.network.request.LoginRequest
import vn.mobile.clothing.network.request.RegisterRequestModel
import vn.mobile.clothing.network.response.LoginResponseModel
import vn.mobile.clothing.network.response.OrderDetailsResponseModel
import vn.mobile.clothing.network.response.OrderStatus
import vn.mobile.clothing.network.response.PurchaseHistoryResponseModel
import vn.mobile.clothing.network.response.ResponseModel
import vn.mobile.clothing.network.response.StatisticalRevenueYear
import vn.mobile.clothing.network.response.StatisticalStatusOrderResModel
import vn.mobile.clothing.network.response.TopProductResponseModel


interface IRequestService {

    //=================================================
    //  region AUTHENTICATION
    //=================================================
    @POST("api/auth/admin/login")
    fun login(@Body login: LoginRequest):Call<ResponseModel<LoginResponseModel>>

    @GET("api/auth/find_user")
    fun findUser(@Query("email") email:String):Call<ResponseModel<User>>

    @POST("api/auth/register")
    fun signUp(@Body request: RegisterRequestModel):Call<ResponseModel<User>>
    //=================================================
    //  endregion
    //=================================================

    //==================== ORDER ======================================//
    @GET("/api/admin/orders/{status}")
    fun getOrders(@Path("status") status:String, @Query("limit") limit:Int, @Query("page") page:Int):Call<ResponseModel<PurchaseHistoryResponseModel>>
    @GET("/api/admin/trackOrder/{id}")
    fun findOrder(@Path("id") id:String):Call<ResponseModel<OrderDetailsResponseModel>>
    @POST("/api/orders/order_status")
    fun updateOrderStatus(@Body model: OrderStatus):Call<ResponseModel<OrderStatus>>

    @GET("/api/admin/orders/statistical")
    fun getStatistical():Call<ResponseModel<List<StatisticalStatusOrderResModel>>>

    @GET("/api/admin/statistical/sale")
    fun getStatisticalRevenue():Call<ResponseModel<List<StatisticalRevenueYear>>>

    @GET("/api/admin/statistical/top_products")
    fun getTopProducts():Call<ResponseModel<List<TopProductResponseModel>>>

    //============================ VOUCHER -=============================================
    @POST("api/vouchers")
    fun addVoucher(@Body voucher: AddVoucherRequestModel): Call<ResponseModel<VoucherModel>>

}