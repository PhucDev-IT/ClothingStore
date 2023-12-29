package com.example.clothingstoreadmin.service

import android.content.ContentValues.TAG

import android.util.Log
import com.example.clothingstoreadmin.model.OrderModel
import com.example.clothingstoreadmin.model.ProgressOrder

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
    fun getOrderWaitShipping(onLoadData:(List<OrderModel>)->Unit){

        db.collection("orders")

            .whereNotEqualTo("currentStatus",ProgressOrder.TransportingOrder.name)
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

    //Xác nhận đơn hàng
    fun confirmOrder(order:OrderModel,onResult: (Boolean) -> Unit){
        order.id?.let {
            db.collection("orders").document(it).set(order)
                .addOnSuccessListener {
                    onResult(true)
                }.addOnFailureListener {
                    Log.e(TAG, "Có lỗi: ${it.message}")
                    onResult(false)
                }
        }
    }

}