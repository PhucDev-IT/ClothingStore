package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.Customer
import com.example.clothingstoreapp.Model.UserManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AddressService() {
    private val db:FirebaseFirestore = Firebase.firestore
    private val uid = UserManager.getInstance().getUserID()


    fun addNewAddress(customer: Customer,onResult:(Boolean)->Unit){

        db.collection("users").document(uid!!)
            .set(customer)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                Log.e(TAG,"Fail: ${it.message}")
                onResult(false)
            }

    }

}