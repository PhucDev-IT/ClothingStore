package com.example.clothingstoreapp.Service

import android.util.Log
import com.example.clothingstoreapp.Model.Customer
import com.google.firebase.firestore.FirebaseFirestore

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

}