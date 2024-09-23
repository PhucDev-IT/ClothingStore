package vn.clothing.store.networks

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import vn.clothing.store.models.CartModel
import vn.clothing.store.models.Category
import vn.clothing.store.models.Image
import vn.clothing.store.models.Product
import vn.clothing.store.models.ProductDetails
import vn.clothing.store.models.User
import vn.clothing.store.networks.request.CartRequestModel
import vn.clothing.store.networks.request.LoginGoogleRequest
import vn.clothing.store.networks.request.LoginRequest
import vn.clothing.store.networks.request.RegisterRequestModel
import vn.clothing.store.networks.response.CartResponseModel
import vn.clothing.store.networks.response.LoginResponseModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback


interface IRequestService {

    //=================================================
    //  region AUTHENTICATION
    //=================================================
    @POST("api/auth/login")
    fun login(@Body login:LoginRequest):Call<ResponseModel<LoginResponseModel>>

    @POST("api/auth/login_google")
    fun loginGoogle(@Body login:LoginGoogleRequest):Call<ResponseModel<User>>


    @POST("api/auth/register")
    fun signUp(@Body request:RegisterRequestModel):Call<ResponseModel<User>>
    //=================================================
    //  endregion
    //=================================================


    //=================================================
    //  region PRODUCT
    //=================================================

    @GET("api/categories")
    fun getAllCategories():Call<ResponseModel<ArrayList<Category>>>

    @GET("/api/categories/{id}/products")
    fun getProductsByCategory(@Path("id") id:Int, @Query("limit") limit:Int, @Query("offset") offset:Int):Call<ResponseModel<List<Product>>>

    @GET("/api/products/{id}/details")
    fun getProductDetails(@Path("id") id:String):Call<ResponseModel<List<ProductDetails>>>

    @GET("/api/images")
    fun getImagesByModel(@Query("model_name") name:String, @Query("model_id") id:String):Call<ResponseModel<List<Image>>>

    //=================================================
    //  endregion
    //=================================================



    //=================================================
    //  region SHOPPING CART
    //=================================================
    @POST("/api/cart")
     fun addToCart(@Body model:CartRequestModel):Call<ResponseModel<CartModel.CartItem>>

     @GET("/api/carts")
     fun getAllCart():Call<ResponseModel<CartResponseModel>>


    //=================================================
    //  endregion
    //=================================================
}