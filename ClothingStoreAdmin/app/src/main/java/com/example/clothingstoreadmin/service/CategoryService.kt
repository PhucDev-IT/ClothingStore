package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreadmin.model.Category
import com.example.clothingstoreadmin.model.FirebaseManager

import com.google.firebase.firestore.FirebaseFirestore

class CategoryService() {

    private val db:FirebaseFirestore = FirebaseManager.getFirestoreInstance()

    fun getAllCategory(callBack:(List<Category>)->Unit){
        db.collection("categories").get().addOnSuccessListener { documents->
            val list = mutableListOf<Category>()
            for (item in documents){
                val cate = item.toObject(Category::class.java)
                cate.id = item.id
                list.add(cate)
            }

            callBack(list)
        }.addOnFailureListener {
            Log.e(TAG,"Có lỗi: ${it.message}")
            callBack(emptyList())
        }
    }
}