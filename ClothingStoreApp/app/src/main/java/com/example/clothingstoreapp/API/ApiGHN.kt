package com.example.clothingstoreapp.API

import com.example.clothingstoreapp.Model.DistrictsJson
import com.example.clothingstoreapp.Model.ProvincesJson
import com.example.clothingstoreapp.Model.ShippingFeeResponse
import com.example.clothingstoreapp.Model.ShippingMethodJson
import com.example.clothingstoreapp.Model.WardJson
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiGHN {


    companion object {
        private const val BASE_URL = "https://online-gateway.ghn.vn/shiip/public-api/"

        private val interceptor: Interceptor = Interceptor { chain ->
            // Lấy ra yêu cầu gốc
            val originalRequest: Request = chain.request()

            // Thêm header vào yêu cầu gốc
            val modifiedRequest: Request = originalRequest.newBuilder()
                .header("token", "52a73b2a-d2d4-11ee-893f-b6ed573185af")
                .header("ShopID", "4914423")
                .build()

            // Tiếp tục xử lý yêu cầu mới với các interceptor khác hoặc thực hiện yêu cầu
            chain.proceed(modifiedRequest)
        }

        private val loggingInterceptor: HttpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        private val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)

        fun create(): ApiGHN {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient.build())
                .build()

            return retrofit.create(ApiGHN::class.java)
        }

    }

    //GET
    @GET("master-data/province")
    fun callApiProvinces(): Observable<ProvincesJson>


    @GET("master-data/district")
    fun callApiDistricts(@Query("province_id") provinceId: Int): Observable<DistrictsJson>


    @GET("master-data/ward")
    fun callApiWard(@Query("district_id") districtId: Int): Observable<WardJson>

    //Lấy các phương thức giao hàng khả dụng: https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services
    /*
    header: token
    paramas:
    shop_id: int - ID của shop
    from_district: int - ID của quận/huyện người gửi
    to_district: int - ID của quận/huyện người nhận
     */


    @GET("v2/shipping-order/available-services?shop_id=4914423&to_district=1616&from_district=1615")
    fun getTransportUnit(
//        @Query("from_district") from_district: Int,
//        @Query("to_district") to_district: Int,
//        @Query("shop_id") shop_id: Int
    ): Call<ResponseBody>

    //Tính giá cước vận chuyên: https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee
    /*
    Header: token, shop_id
    service_id: int - ID của gói dịch vụ mà bạn chọn
    service_type_id: Nếu không điền service_id, có thể thay thế bằng 1 trong 3 lựa chọn sau: 1: Express, 2: Standard, 3: Saving
    insurance_value: int - giá trị của sản phẩn. GHN căn cứ vào giá trị này để tính tiền bảo hiểm cho hàng hóa.
    coupon: String - Mã giảm giá của GHN. Nếu không có, để rỗng: "" hoặc null
    to_ward_code: String - ID Phường/ Xã người nhận
    to_district_id: int - ID Quận/Huyện người nhận
    from_district_id: int - ID Quận/Huyện người gửi
    weight: int - trọng lượng hàng hóa (gram)
    length: int - Chiều dài (cm)
    width: int - Chiều rộng (cm)
    height: int - Chiều cao (cm)
     */


    @GET("v2/shipping-order/fee")
    fun calculateFeeShip(
        @Query("service_id") serviceId: Int,
        @Query("insurance_value") insuranceValue: Int,
        @Query("to_ward_code") toWardCode: String,
        @Query("to_district_id") toDistrictId: Int,
        @Query("from_district_id") fromDistrictId: Int,
        @Query("weight") weight: Int,
        @Query("length") length: Int,
        @Query("width") width: Int,
        @Query("height") height: Int,
        @Query("service_type_id") serviceTypeId: Int
    ): Call<ShippingFeeResponse>
}