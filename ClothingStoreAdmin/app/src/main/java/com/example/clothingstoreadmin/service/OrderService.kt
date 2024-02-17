package com.example.clothingstoreadmin.service

import android.content.ContentValues.TAG

import android.util.Log
import com.example.clothingstoreadmin.model.OrderModel
import com.example.clothingstoreadmin.model.ProgressOrder

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class OrderService {
    private val db = Firebase.firestore
    private val maxSize:Long  =  5
    private var lastIdOrder: DocumentSnapshot? = null
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
            .orderBy("currentStatus")
            .limit(maxSize).get()
            .addOnSuccessListener {  documents->
                lastIdOrder = documents.documents[documents.size() - 1]
                Log.w(TAG,"Last id: $lastIdOrder")
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

    fun getNextPage(status: String = ProgressOrder.TransportingOrder.name, onLoadData: (List<OrderModel>) -> Unit) {
        if (lastIdOrder == null) return
        Log.w(TAG, "Last id pre: ${lastIdOrder!!.id}") // Chú ý sử dụng lastIdOrder.id để lấy ID của DocumentSnapshot
        db.collection("orders")
            .whereNotEqualTo("currentStatus", status)
            .orderBy("currentStatus")
            .startAfter(lastIdOrder!!["currentStatus"]) // Sử dụng trường currentStatus để phân trang
            .limit(maxSize.toLong())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    lastIdOrder = documents.documents[documents.size() - 1]
                    Log.w(TAG, "Last id next: ${lastIdOrder!!.id}")
                    val list = mutableListOf<OrderModel>()
                    for (document in documents) {
                        val order: OrderModel = document.toObject(OrderModel::class.java)
                        order.id = document.id
                        list.add(order)
                        Log.w(TAG, "VALUES Next: ${order.id}")
                    }
                    onLoadData(list)
                } else {
                    onLoadData(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Có lỗi next page: ${exception.message}")
                onLoadData(emptyList())
            }
    }



    //Xác nhận đơn hàng
    fun confirmOrder(order:OrderModel,onResult: (Boolean) -> Unit){
        order.id?.let { it ->
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