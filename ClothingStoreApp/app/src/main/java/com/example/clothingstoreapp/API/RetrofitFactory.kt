package com.example.clothingstoreapp.API

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {

    companion object {
        fun create(BASE_URL: String): ApiGHN {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(ApiGHN::class.java)
        }
    }
}
