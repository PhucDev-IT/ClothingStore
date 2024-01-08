package com.example.clothingstoreapp.Data_Local


import android.content.Context

class DataLocalManager {


    private lateinit var mySharedPreferences: MySharedPreferences

    companion object{
        private const val PREF_FIRST_APP:String = "PREF_FIRST_APP"

        private var instance: DataLocalManager? = null

        fun init(context: Context) {
            instance = DataLocalManager()
            instance!!.mySharedPreferences = MySharedPreferences(context)
        }

        private fun getInstance(): DataLocalManager {
            if (instance == null) {
                instance = DataLocalManager()
            }
            return instance as DataLocalManager
        }

        fun setUsedApplication(){
            getInstance().mySharedPreferences.putBooleanValue(PREF_FIRST_APP,false)
        }

        fun checkIsFirstUseApp(): Boolean {
            return getInstance().mySharedPreferences.getBooleanValues(PREF_FIRST_APP)
        }
    }
}