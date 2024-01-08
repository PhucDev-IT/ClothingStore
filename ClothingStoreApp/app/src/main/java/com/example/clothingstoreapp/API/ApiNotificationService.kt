package com.example.clothingstoreapp.API

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiNotificationService {

    companion object{
        private const val BASE_URL = "https://fcm.googleapis.com/"

        fun create(): ApiNotificationService {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(ApiNotificationService::class.java)
        }

    }

}