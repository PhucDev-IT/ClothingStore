package com.example.clothingstoreadmin.api

import com.example.clothingstoreadmin.model.DataMessageNotification
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiNotificationService {



    companion object{
        private const val BASE_URL = "https://fcm.googleapis.com/"
        private const val SERVER_KEY:String = "AAAAbjZgOC4:APA91bHTS6nx9yFOrB4OiDR-72eWoQdn4KCINa6vNnx5P8OIgfUG9bf5vnvYoxInwq0I8lozT0MzqIjEPnADWZlvn8e6MWNiN42ZdD0erCipDoYR3tw1yGIhsae_q49bM0rO2h38KT8y"
        private const val CONTENT_TYPE:String = "application/json"

        fun create(): ApiNotificationService {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(ApiNotificationService::class.java)
        }

    }

    @Headers(
        "Authorization: key=$SERVER_KEY",
        "Content-Type: $CONTENT_TYPE"
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification:DataMessageNotification):Call<DataMessageNotification>
}