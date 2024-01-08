package com.example.clothingstoreadmin.service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreadmin.model.NotificationModel
import com.google.firebase.firestore.FirebaseFirestore

class NotificationService(private val db:FirebaseFirestore) {


    //Thông báo đến 1 user
    fun addNewNotificationForUser(userId:String,notification: NotificationModel,callBack: (Boolean) -> Unit){
        db.collection("MyNotifications").document(userId).collection("items").add(notification)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    callBack(true)
                }else callBack(false)
            }.addOnFailureListener {
                Log.e(TAG,"Có lỗi xảy ra: ${it.message}")
                callBack(false)
            }
    }

    fun addNewNotification(notification: NotificationModel,callBack:(Boolean)->Unit){
        db.collection("notifications").add(notification) .addOnCompleteListener {task->
            if(task.isSuccessful){
                callBack(true)
            }else callBack(false)
        }.addOnFailureListener {
            Log.e(TAG,"Có lỗi xảy ra: ${it.message}")
            callBack(false)
        }
    }
}