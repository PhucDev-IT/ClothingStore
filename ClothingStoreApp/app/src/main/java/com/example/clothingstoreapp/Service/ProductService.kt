package com.example.clothingstoreapp.Service

import android.content.ContentValues
import android.util.Log
import androidx.compose.ui.text.toUpperCase
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.Model.ProductIsLiked
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

    //Lấy sản phâẩm đã yêu thích
    fun getProductsIsLiked(list: List<ProductIsLiked>, onDataLoader: (List<Product>) -> Unit) {
        val listProduct: MutableList<Product> = ArrayList()
        val tasksCount = AtomicInteger(list.size)
        for (item in list) {
            db.collection("products").document(item.idProduct).get()
                .addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            val product =
                                document.toObject(Product::class.java)
                            if (product != null) {
                                listProduct.add(product)
                            }
                        }
                    }

                    // Giảm số lượng task chưa hoàn thành
                    if (tasksCount.decrementAndGet() == 0) {
                        // Nếu không còn task nào chưa hoàn thành, gọi onDataLoader với listProduct
                        onDataLoader(listProduct)
                    }
                }
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
}