package com.example.clothingstoreapp.Service

import android.content.ContentValues
import android.util.Log
import com.example.clothingstoreadmin.model.Product


import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger


class ProductService(private val db: FirebaseFirestore) {
    private val maxSize: Long = 20
    private lateinit var lastDocument: DocumentSnapshot

    //Lấy tất cả sản phẩm
    fun selectAllFirstPage(onDataLoader: (List<Product>) -> Unit) {
        db.collection("products").limit(maxSize)
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Product>()

                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    product.id = document.id
                    list.add(product)
                }

                // Kiểm tra xem có tài liệu trong kết quả không
                if (!result.isEmpty) {
                    lastDocument = result.documents[result.size() - 1] // Lấy tài liệu cuối cùng

                }
                onDataLoader(list)
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Lỗi: .", exception)
            }
    }

    //Sản phẩm mới thêm
    fun selectNewest(onDataLoader: (List<Product>) -> Unit) {
        db.collection("products").orderBy("createdTime").limit(maxSize)
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Product>()

                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    product.id = document.id
                    list.add(product)
                }

                // Kiểm tra xem có tài liệu trong kết quả không
                if (!result.isEmpty) {
                    lastDocument = result.documents[result.size() - 1] // Lấy tài liệu cuối cùng

                }
                onDataLoader(list)
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Lỗi: .", exception)
            }
    }


    //Lấy sản phẩm theo giới tính
    fun selectByTags(value: String, onDataLoader: (List<Product>) -> Unit) {
        db.collection("products").whereArrayContains("tags", value.toUpperCase(Locale.ROOT)).limit(maxSize)
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Product>()

                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    product.id = document.id
                    list.add(product)
                }

                // Kiểm tra xem có tài liệu trong kết quả không
                if (!result.isEmpty) {
                    lastDocument = result.documents[result.size() - 1] // Lấy tài liệu cuối cùng

                }
                onDataLoader(list)
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Lỗi: .", exception)
            }
    }

    //Sản phẩm phổ biến nhất
    fun selectPopular(onDataLoader: (List<Product>) -> Unit) {
        db.collection("products").orderBy("evaluate").limit(maxSize)
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Product>()

                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    product.id = document.id
                    list.add(product)
                }

                // Kiểm tra xem có tài liệu trong kết quả không
                if (!result.isEmpty) {
                    lastDocument = result.documents[result.size() - 1] // Lấy tài liệu cuối cùng

                }
                onDataLoader(list)
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Lỗi: .", exception)
            }
    }




    //Tìm kiếm sản phẩm
    fun searchProduct(input: String, onDataLoader: (List<Product>) -> Unit) {
        db.collection("products").orderBy("name").startAt(input).endAt(input + '\uf8ff').get()
            .addOnSuccessListener { documents->
                val list = mutableListOf<Product>()

                for(item in documents){
                    val product = item.toObject(Product::class.java)
                    product.id = item.id
                    list.add(product)
                }

                // Kiểm tra xem có tài liệu trong kết quả không
                if (!documents.isEmpty) {
                    lastDocument = documents.documents[documents.size() - 1] // Lấy tài liệu cuối cùng

                }
                onDataLoader(list)
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Lỗi: .", exception)
            }
    }

    //Lấy sản phẩm theo danh mục
    fun selectProductByCategory(id:String,callBack:(List<Product>)->Unit){
        db.collection("products").whereEqualTo("idCategory",id).limit(10).get()
            .addOnSuccessListener { documents->
                val list = mutableListOf<Product>()
                for (item in documents){
                    val pro = item.toObject(Product::class.java)
                    pro.id = item.id

                    list.add(pro)
                }
                callBack(list)
            }.addOnFailureListener {
                Log.e(ContentValues.TAG,"Có lỗi: ${it.message}")
                callBack(emptyList())
            }
    }
}