package vn.mobile.clothing.network.rest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

abstract class BaseCallback<T>: Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        // Response code not in range 200-300
        if (!response.isSuccessful) {
            handleErrorResponse(response)
            return
        }

        // Check response body
        val body = response.body()
        if (body == null) {
           onError("The response is empty.")
            return
        }
        onSuccess(body)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        onError(t.message?:"An unknown error")

    }


    private fun handleErrorResponse(response: Response<*>){
        if(response.errorBody() == null){
            when(response.code()){
                401 -> onError("401: Unauthorized Request. Please check API Key and try again.")
                400 -> onError("400: Invalid Request. Please check request body and try again.")
                else -> onError("Error code " + response.code())
            }
            return
        }
        try {
            onError(response.errorBody()!!.string())
        } catch (e: IOException) {
            onError(e.message?:"Failure to server")
        }
    }

    abstract fun onSuccess(model:T)
    abstract fun onError(message:String)
}