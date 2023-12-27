package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.UserManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class OrderService {
    private val db = Firebase.firestore
    private val maxSize:Long  =  10
    fun addOrder(order: OrderModel, onResult: (Boolean) -> Unit) {

        db.collection("orders").add(order).addOnSuccessListener {

            onResult(true)

        }.addOnFailureListener {
            Log.e(TAG, "Có lỗi: ${it.message}")
            onResult(false)
        }
    }

    //Lấy hóa đơn đang vận chuyển
    fun getOrderTransporting(onLoadData:(List<OrderModel>)->Unit){
        val uid =  UserManager.getInstance().getUserID()
        db.collection("orders").whereEqualTo("userID",uid)
            .orderBy("orderDate",Query.Direction.ASCENDING)
            .limit(maxSize).get()
            .addOnSuccessListener {  documents->
                val list = mutableListOf<OrderModel>()
                for(document in documents){
                    val order:OrderModel = document.toObject(OrderModel::class.java)
                    order.id = document.id
                    list.add(order)
                    Log.w(TAG,"VALUES: ${order.id}")
                }
                onLoadData(list)

            }.addOnFailureListener {
                Log.e(TAG, "Có lỗi: ${it.message}")
                onLoadData(emptyList())
            }

    }

    //Lịch sử mua hàng
    fun getPurchaseHistory(onLoadData:(List<OrderModel>)->Unit){
        val uid =  UserManager.getInstance().getUserID()
        if (uid != null) {
            db.collection("purchaseHistory").document(uid).collection("itemOrders").orderBy("orderDate",Query.Direction.ASCENDING)
                .limit(maxSize).get()
                .addOnSuccessListener {  documents->
                    val list = mutableListOf<OrderModel>()
                    for(document in documents){
                        val order:OrderModel = document.toObject(OrderModel::class.java)
                        order.id = document.id
                        list.add(order)
                    }
                    onLoadData(list)

                }.addOnFailureListener {
                    Log.e(TAG, "Có lỗi: ${it.message}")
                    onLoadData(emptyList())
                }
        }
    }
}