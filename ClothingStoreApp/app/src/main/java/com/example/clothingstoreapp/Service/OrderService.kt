package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.Model.UserManager
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class OrderService {

    private val uid: String? = UserManager.getInstance().getUserID()
    var lastIdOrder:DocumentSnapshot? = null
    private val db = Firebase.firestore
    private val maxSize: Long = 10


    fun addOrder(order: OrderModel, onResult: (Boolean) -> Unit) {

        db.collection("orders").add(order).addOnSuccessListener {

                onResult(true)

            }.addOnFailureListener {
                Log.e(TAG, "Có lỗi: ${it.message}")
                onResult(false)
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
                    Log.w(TAG, "Result = $order")
                    onResult(order)
                } else {
                    Log.d(TAG, "Current data: null")
                    onResult(null)
                }
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

    //Lấy đơn hàng đang chờ xác nhận
    fun getOrderWaitingConfirm(onLoadData: (List<OrderModel>) -> Unit) {
        lastIdOrder = null
        val query = db.collection("orders")
            .whereEqualTo("user.userId", uid!!)
            .whereEqualTo("currentStatus",ProgressOrder.WaitConfirmOrder.name)
            .orderBy("orderDate", Query.Direction.ASCENDING)
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }



    //Lấy hóa đơn đang vận chuyển
    fun getOrderShipping(onLoadData: (List<OrderModel>) -> Unit) {
        lastIdOrder = null
        val query = db.collection("orders")
            .whereEqualTo("user.userId", uid!!)
            .whereIn("currentStatus", listOf(ProgressOrder.PackagingOrder.name, ProgressOrder.Shipping.name))
            .orderBy("orderDate", Query.Direction.ASCENDING)
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }


    //Đơn hàng đã  mua
    fun getOrderDelivered(onLoadData: (List<OrderModel>) -> Unit) {
        lastIdOrder = null
        val query = db.collection("orders")
            .whereEqualTo("user.userId", uid!!)
            .whereArrayContains("statusOrder","")
            .orderBy("orderDate", Query.Direction.ASCENDING)
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }

    //Lấy đơn hàng đã hủy
    fun getCancelOrder(onLoadData: (List<OrderModel>) -> Unit) {
        val query = db.collection("orders")
            .document(ProgressOrder.DeliveredOrder.name).collection(uid!!)
            .limit(maxSize)
        getDataToService(query, onLoadData)
    }

    fun getNextPage(status:String,onLoadData: (List<OrderModel>) -> Unit) {
        if (lastIdOrder == null) return

        db.collection("orders")
            .document(status).collection("itemOrders")
            .whereEqualTo("currentStatus",ProgressOrder.WaitConfirmOrder.name)
            .orderBy(FieldPath.documentId())
            .startAfter(lastIdOrder!!.id) // Sử dụng trường currentStatus để phân trang
            .limit(maxSize)
            .get()

    }

    private fun getDataToService(db: Query, onLoadData: (List<OrderModel>) -> Unit) {
        db.get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<OrderModel>()
                lastIdOrder = documents.documents[documents.size() - 1]
                for (document in documents) {
                    val order: OrderModel = document.toObject(OrderModel::class.java)
                    order.id = document.id
                    list.add(order)
                    Log.w(TAG, "VALUES: ${order.id}")
                }
                onLoadData(list)
            }
            .addOnFailureListener {
                Log.e(TAG, "Có lỗi: ${it.message}")
                onLoadData(emptyList())
            }
    }


}