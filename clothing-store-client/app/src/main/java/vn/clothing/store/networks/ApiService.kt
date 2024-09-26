package vn.clothing.store.networks

import com.google.gson.GsonBuilder
import vn.clothing.store.BuildConfig
import vn.mobile.banking.network.rest.RetrofitClient


class ApiService private constructor(){
    var BASE_URL = BuildConfig.API_BASE_URL
        private set
    private var TOKEN:String = ""

    companion object {
        private val GSON = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

        @Volatile
        private var instance:ApiService?=null
        val APISERVICE =ApiService.getInstance()

        private fun getInstance():ApiService{
            if(instance == null){
                synchronized(ApiService::class){
                    instance = ApiService()
                }
            }
            return instance!!
        }
    }


    fun getService(token:String?=null):IRequestService{
        if(token!=null) TOKEN = token
        return RetrofitClient.buildService(GSON, BASE_URL,TOKEN).create(IRequestService::class.java)
    }


    fun setToken(token:String){
        this.TOKEN = token
    }

    fun setBaseUrl(url:String){
        this.BASE_URL = url

    }

    //========================================
    //  region CALL API
    //========================================



    //========================================
    //  endregion
    //========================================



}