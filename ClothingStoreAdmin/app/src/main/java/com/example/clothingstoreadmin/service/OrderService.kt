package com.example.clothingstoreadmin.service

import android.content.ContentValues.TAG

import android.util.Log
import com.example.clothingstoreadmin.model.OrderModel
import com.example.clothingstoreadmin.model.ProgressOrder
import com.example.clothingstoreadmin.model.UserManager

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class OrderService {
    private val uid: String? = UserManager.getInstance().getUserID()
    private val db = Firebase.firestore
    private val maxSize:Long  =  5
    var lastIdOrder: DocumentSnapshot? = null
    fun addOrder(order: OrderModel, onResult: (Boolean) -> Unit) {

        db.collection("orders").document("WaitingShipping").collection("itemOrders").add(order).addOnSuccessListener {

            onResult(true)

        }.addOnFailureListener {
            Log.e(TAG, "Có lỗi: ${it.message}")
            onResult(false)
        }
    }

    fun getOrderByStatus(status:String,onLoadData:(List<OrderModel>)->Unit ){
        lastIdOrder = null
        val query = db.collection("orders")
            .whereEqualTo("currentStatus",status)
            //.orderBy("orderDate", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId())
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }


    fun getNextPage(status:String,onLoadData: (List<OrderModel>) -> Unit) {
        if (lastIdOrder == null) return

        val query =  db.collection("orders")
            .whereEqualTo("currentStatus",status)
            //.orderBy("orderDate", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId())
            .startAfter(lastIdOrder!!.id) // Sử dụng trường currentStatus để phân trang
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }


    //Xác nhận đơn hàng
    fun confirmOrder(order:OrderModel,onResult: (Boolean) -> Unit){
        order.id?.let { it ->
            db.collection("orders")
               .document(it).set(order)
                .addOnSuccessListener {
                    onResult(true)
                }.addOnFailureListener {
                    Log.e(TAG, "Có lỗi: ${it.message}")
                    onResult(false)
                }
        }
    }

    //Hủy đơn hàng

    fun cancelOrder(order: OrderModel, onResult: (Boolean) -> Unit) {
        order.id?.let { it ->
            db.collection("orders")
                .document(it).set(order)
                .addOnSuccessListener {
                    onResult(true)
                }.addOnFailureListener {
                    Log.e(TAG, "Có lỗi: ${it.message}")
                    onResult(false)
                }
        }
    }

    fun getInformationOrderByID(idOrder: String, onResult: (OrderModel?) -> Unit) {
        db.collection("orders")
            .document(idOrder)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    onResult(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val order = snapshot.toObject(OrderModel::class.java)
                    order?.id = snapshot.id

                    onResult(order)
                } else {
                    Log.d(TAG, "Current data: null")
                    onResult(null)
                }
            }
    }


    private fun getDataToService(db: Query, onLoadData: (List<OrderModel>) -> Unit) {
        Log.w(TAG,"before last id: ${lastIdOrder?.id}")
        db.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val list = mutableListOf<OrderModel>()
                    lastIdOrder = documents.documents[documents.size() - 1]
                    Log.w(TAG,"after last id: $lastIdOrder")
                    for (document in documents) {
                        val order: OrderModel = document.toObject(OrderModel::class.java)
                        order.id = document.id
                        list.add(order)
                        Log.w(TAG, "VALUES: ${order.id}")
                    }
                    onLoadData(list)
                }
                else {
                    onLoadData(emptyList())
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Có lỗi: ${it.message}")
                onLoadData(emptyList())
            }
    }

}