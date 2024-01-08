package com.example.clothingstoreapp.Data_Local


import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class MySharedPreferences(private val mContext:Context) {
    companion object{
        private const val MY_SHARED_PREFERENCES:String = "MY_SHARED_PREFERENCES"
    }

    fun putStringValue(key:String,values:String){
        val sharedPreferences:SharedPreferences = mContext.getSharedPreferences(
            MY_SHARED_PREFERENCES,Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key,values)
        editor.apply()
    }

    fun getStringValues(key:String): String? {

        val sharedPreferences:SharedPreferences = mContext.getSharedPreferences(
            MY_SHARED_PREFERENCES,Context.MODE_PRIVATE)
        return sharedPreferences.getString(key,null)
    }

    fun putBooleanValue(key:String,values:Boolean){
        val sharedPreferences:SharedPreferences = mContext.getSharedPreferences(
            MY_SHARED_PREFERENCES,Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key,values)
        editor.apply()
    }

    fun getBooleanValues(key:String): Boolean {

        val sharedPreferences:SharedPreferences = mContext.getSharedPreferences(
            MY_SHARED_PREFERENCES,Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, true)
    }
}