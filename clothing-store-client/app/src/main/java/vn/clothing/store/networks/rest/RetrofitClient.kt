package vn.mobile.banking.network.rest

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit
class RetrofitClient {
    companion object {
        fun buildService(gson: Gson, baseUrl: String, token: String?): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient(token).build())  // Pass token to okHttpClient
                .build()
        }

        private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        // Modified okHttpClient to take token as parameter
        private fun okHttpClient(token: String?): OkHttpClient.Builder {
            return OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(interceptor(token))  // Pass token to interceptor
        }

        private fun interceptor(token: String?): Interceptor {
            return Interceptor { chain ->
                val originalRequest: Request = chain.request()
                val modifiedRequest: Request = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")  // Add token to the header
                    .build()
                chain.proceed(modifiedRequest)
            }
        }
    }
}
