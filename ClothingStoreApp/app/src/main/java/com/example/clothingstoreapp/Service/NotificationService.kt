package com.example.clothingstoreapp.Service

import android.util.Log
import com.example.clothingstoreapp.Model.Notification
import com.example.clothingstoreapp.Model.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationService(private val db:FirebaseFirestore) {
    private val TAG:String = NotificationService::class.java.name
    private val userID: String? = UserManager.getInstance().getUserID()

    fun getNotificationByID(callBack:(List<Notification>)->Unit){

        if (userID != null) {
            db.collection("MyNotifications").document(userID).collection("items")
                .orderBy("timeSend",Query.Direction.DESCENDING).limit(10).get()
                .addOnSuccessListener { task->
                    val list = mutableListOf<Notification>()
                    for (item in task){
                        val notification = item.toObject(Notification::class.java)
                        notification.id = item.id

                        list.add(notification)
                    }
                    callBack(list)
                }.addOnFailureListener {
                    Log.e(TAG,"Có lỗi xảy ra: ${it.message}")
                    callBack(emptyList())
                }
        }

    }


    fun getAllNotification(callBack: (List<Notification>) -> Unit){
        db.collection("notifications")
            .orderBy("timeSend",Query.Direction.DESCENDING).limit(15).get()
            .addOnSuccessListener { task->
                val list = mutableListOf<Notification>()
                for (item in task){
                    val notification = item.toObject(Notification::class.java)
                    notification.id = item.id

                    list.add(notification)
                }
                callBack(list)
            }.addOnFailureListener {
                Log.e(TAG,"Có lỗi xảy ra: ${it.message}")
                callBack(emptyList())
            }
    }
}