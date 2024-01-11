package com.example.clothingstoreadmin.service

import android.util.Log
import com.example.clothingstoreadmin.model.Customer

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CustomerService() {

    fun getInformationUser(uid: String,callback:(Customer?)->Unit){
        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener { value->
            if(value.exists()){
                val customer = value.toObject(Customer::class.java)
                if (customer != null) {
                    callback(customer)
                }
            }else{
                callback(null)
            }
        }.addOnFailureListener{
            Log.e("Lỗi: ",it.toString())
            callback(null)
        }

    }

    fun updateTokenFcm(uid:String,token:String){
        val data = hashMapOf<String,String>("tokenFCM" to token)
        FirebaseFirestore.getInstance().collection("users").document(uid).set(data, SetOptions.merge())
            .addOnSuccessListener {

            }.addOnFailureListener{
                Log.e("Lỗi: ",it.toString())

            }
    }



}