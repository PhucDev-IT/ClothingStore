package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.Customer
import com.example.clothingstoreapp.Model.UserManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class AddressService() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val uid = UserManager.getInstance().getUserID()
    private val TAG = AddressService::class.java.name

    fun addNewAddress(address: AddressModel, onResult: (Boolean) -> Unit) {
        uid?.let { userId ->
            val addressCollectionRef = db.collection("address").document(userId).collection("items")
            addressCollectionRef.add(address)
                .addOnSuccessListener { documentReference ->
                    if (address.isDefault) {
                        val newAddressId = documentReference.id
                        db.collection("address").document(userId)
                            .set(mapOf("default_id" to newAddressId), SetOptions.merge())
                            .addOnSuccessListener {
                                // Gọi hàm callback với kết quả thành công
                                onResult(true)
                            }
                            .addOnFailureListener { exception ->
                                // Ghi log và gọi hàm callback với kết quả thất bại
                                Log.e(TAG, "Fail: ${exception.message}")
                                onResult(false)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // Ghi log và gọi hàm callback với kết quả thất bại
                    Log.e(TAG, "Fail: ${exception.message}")
                    onResult(false)
                }
        } ?: run {
            // Gọi hàm callback với kết quả thất bại nếu uid không được xác thực hoặc null
            onResult(false)
        }
    }


    fun fetchAddress(onResult: (List<AddressModel>?) -> Unit) {
        uid?.let { userId ->
            db.collection("address").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val defaultID = documentSnapshot.get("default_id")
                    db.collection("address").document(userId).collection("items")
                        .get().addOnSuccessListener { documentRef ->
                            val list = mutableListOf<AddressModel>()
                            for (item in documentRef) {
                                val address = item.toObject(AddressModel::class.java)
                                address.id = item.id
                                address.isDefault = item.id == defaultID
                                list.add(address)

                                Log.w(TAG, "Value: $address")
                            }

                            onResult(list)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to fetch address: ${exception.message}")
                    onResult(null)
                }
        } ?: run {
            Log.e(TAG, "User ID is null")
            onResult(null)
        }
    }


    fun getAddressDefault(onResult: (AddressModel?) -> Unit) {
        uid?.let { userId ->
            db.collection("address").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val defaultID = documentSnapshot.getString("default_id")
                    if (defaultID != null) {
                        db.collection("address").document(userId).collection("items")
                            .document(defaultID).get().addOnSuccessListener { documentRef ->
                                val address = documentRef.toObject(AddressModel::class.java)
                                address?.id = documentRef.id
                                address?.isDefault = true

                                Log.w(TAG, "Value: $address")
                                onResult(address)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to fetch address: ${exception.message}")
                    onResult(null)
                }
        } ?: run {
            Log.e(TAG, "User ID is null")
            onResult(null)
        }
    }


}