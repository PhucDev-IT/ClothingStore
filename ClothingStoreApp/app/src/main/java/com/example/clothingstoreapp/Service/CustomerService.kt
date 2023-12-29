package com.example.clothingstoreapp.Service

import android.util.Log
import com.example.clothingstoreapp.Model.Customer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CustomerService(private val db:FirebaseFirestore) {

    fun getInformationUser(uid: String,callback:(Customer?)->Unit){
        db.collection("users").document(uid).get().addOnSuccessListener { value->
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
        db.collection("users").document(uid).set(data, SetOptions.merge())
            .addOnSuccessListener {

            }.addOnFailureListener{
                Log.e("Lỗi: ",it.toString())

            }
    }



}