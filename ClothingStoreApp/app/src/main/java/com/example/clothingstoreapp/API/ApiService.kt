package com.example.clothingstoreapp.API

import com.example.clothingstoreapp.Model.DistrictsJson
import com.example.clothingstoreapp.Model.ProvincesJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    companion object{
        private const val BASE_URL = "https://provinces.open-api.vn/"

        fun create(): ApiService {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(ApiService::class.java)
        }

    }

    //GET
    @GET("api/p/")
    fun callApiProvinces():Call<List<ProvincesJson>>

    @GET("api/p/{code}")
    fun callApiDistrics(@Path("code") number: Int, @Query("depth") depth: Int):Call<ProvincesJson>

    @GET("api/d/{code}")
    fun callApiWard(@Path("code") code:Int,@Query("depth") depth: Int):Call<DistrictsJson>
}