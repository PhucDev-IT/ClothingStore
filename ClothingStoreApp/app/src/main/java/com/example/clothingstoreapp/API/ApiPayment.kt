package com.example.clothingstoreapp.API

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiPayment {
    companion object{
        private const val BASE_URL = "https://online-gateway.ghn.vn/shiip/public-api/master-data/"

        fun create(): ApiGHN {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(ApiGHN::class.java)
        }

    }
}