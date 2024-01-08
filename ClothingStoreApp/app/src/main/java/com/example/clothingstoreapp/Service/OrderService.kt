package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.Model.UserManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class OrderService {

    private val uid: String? = UserManager.getInstance().getUserID()

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


    fun cancelOrder(order: OrderModel, onResult: (Boolean) -> Unit) {
        val orderDocument = db.collection("order").document(order.id!!)
        val canceledOrdersCollection = db.collection("orders").document(uid!!)
            .collection("canceled")

        db.runTransaction { transaction ->
            // Xóa đơn hàng từ collection "order"
            transaction.delete(orderDocument)

            // Thêm đơn hàng vào collection "canceled" trong "orders"
            transaction.set(canceledOrdersCollection.document(order.id!!), order)

            // Trả về kết quả
            true
        }.addOnSuccessListener {
            onResult(true)
        }.addOnFailureListener { e ->
            Log.e(TAG, "Có lỗi khi hủy đơn hàng: ${e.message}")
            onResult(false)
        }
    }


    //Lấy hóa đơn đang vận chuyển
    fun getOrderTransporting(onLoadData:(List<OrderModel>)->Unit){

        db.collection("orders").whereEqualTo("user.userId",uid!!)
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

    fun getOrderDelivery(onLoadData:(List<OrderModel>)->Unit){
        val uid =  UserManager.getInstance().getUserID()
        db.collection("orders").whereEqualTo("user.userId",uid!!)
            .whereEqualTo("currentStatus",ProgressOrder.DeliveredOrder.name)
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

    //Lấy đơn hàng đã hủy
    fun getCancelOrder(onLoadData: (List<OrderModel>) -> Unit){
        db.collection("orders").document(uid!!).collection("items").limit(maxSize)
            .get().addOnSuccessListener { documents->
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

}