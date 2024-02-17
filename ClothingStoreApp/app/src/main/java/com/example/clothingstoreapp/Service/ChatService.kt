package com.example.clothingstoreapp.Service

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.example.clothingstoreapp.Model.Voucher
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object ChatService {

    //Lấy tất cả người dùng đã chat
    fun allChatroomCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("chatrooms")
    }

    fun getChatroomReference(idRoom:String): DocumentReference {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(idRoom)
    }

    fun getChatroomMessageReference(idRoom: String):CollectionReference{
        return getChatroomReference(idRoom).collection("chats")
    }

    fun addImageAndVoucher(urlImg: Uri, voucher: Voucher, callback: (b: Boolean) -> Unit) {
        var storageRef = FirebaseStorage.getInstance().reference.child("Vouchers")
        storageRef = storageRef.child(System.currentTimeMillis().toString())

        storageRef.putFile(urlImg).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageRef.downloadUrl.addOnSuccessListener { uri ->

                }
            } else {
                Log.e(ContentValues.TAG, "Upload image faild: ${task.exception?.message}")
            }
        }
    }
}