package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.Model.UserManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class OrderService {

    private val uid: String? = UserManager.getInstance().getUserID()
    private var lastIdOrder:DocumentSnapshot? = null
    private val db = Firebase.firestore
    private val maxSize: Long = 5


    fun addOrder(order: OrderModel, onResult: (Boolean) -> Unit) {

        val orderRef = db.collection("orders")
            .add(order)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener{
                onResult(false)
                Log.e(TAG,"Lỗi: ${it.message}")
            }
    }


    fun getInformationOrderByID(status: String, idOrder: String, onResult: (OrderModel?) -> Unit) {
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

    //Lấy đơn hàng đang chờ xác nhận
    fun getOrderWaitingConfirm(onLoadData: (List<OrderModel>) -> Unit) {
        lastIdOrder = null
        val query = db.collection("orders")
            .whereEqualTo("user.userId", uid!!)
            .whereEqualTo("currentStatus",ProgressOrder.WaitConfirmOrder.name)
            //.orderBy("orderDate", Query.Direction.DESCENDING)
             .orderBy(FieldPath.documentId())
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }



    //Lấy hóa đơn đang vận chuyển
    fun getOrderShipping(onLoadData: (List<OrderModel>) -> Unit) {
        lastIdOrder = null
        val query = db.collection("orders")
            .whereEqualTo("user.userId", uid!!)
            .whereIn("currentStatus", listOf(ProgressOrder.PackagingOrder.name, ProgressOrder.Shipping.name))
           // .orderBy("orderDate", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId())
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }


    //Đơn hàng đã  mua
    fun getOrderDelivered(onLoadData: (List<OrderModel>) -> Unit) {
        lastIdOrder = null
        val query = db.collection("orders")
            .whereEqualTo("user.userId", uid!!)
            .whereArrayContains("statusOrder","")
           // .orderBy("orderDate", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId())
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }

    //Lấy đơn hàng đã hủy
    fun getCancelOrder(onLoadData: (List<OrderModel>) -> Unit) {
        val query = db.collection("orders")
            .whereEqualTo("user.userId", uid!!)
            .whereEqualTo("currentStatus",ProgressOrder.OrderCanceled.name)
           // .orderBy("orderDate", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId())
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }

    fun getNextPage(status:String,onLoadData: (List<OrderModel>) -> Unit) {
        if (lastIdOrder == null) return

        val query =  db.collection("orders")
            .whereEqualTo("user.userId", uid!!)
            .whereEqualTo("currentStatus",status)
            //.orderBy("orderDate", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId())
            .startAfter(lastIdOrder!!.id) // Sử dụng trường currentStatus để phân trang
            .limit(maxSize)
        getDataToService(query, onLoadData)
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