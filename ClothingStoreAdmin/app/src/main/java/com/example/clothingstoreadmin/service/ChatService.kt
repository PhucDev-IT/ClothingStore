package com.example.clothingstoreadmin.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

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


}