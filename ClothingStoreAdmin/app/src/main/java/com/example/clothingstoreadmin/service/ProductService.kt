package com.example.clothingstoreapp.Service

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.example.clothingstoreadmin.model.Product
import com.example.clothingstoreadmin.model.ProductDetails


import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger


class ProductService(private val db: FirebaseFirestore) {
    private val maxSize: Long = 20
    private lateinit var lastDocument: DocumentSnapshot

    //Lấy tất cả sản phẩm
    fun selectAllFirstPage(onDataLoader: (List<Product>) -> Unit) {
        db.collection("Products").limit(maxSize)
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
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()

                for (item in documents) {
                    val product = item.toObject(Product::class.java)
                    product.id = item.id
                    list.add(product)
                }

                // Kiểm tra xem có tài liệu trong kết quả không
                if (!documents.isEmpty) {
                    lastDocument =
                        documents.documents[documents.size() - 1] // Lấy tài liệu cuối cùng

                }
                onDataLoader(list)
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Lỗi: .", exception)
            }
    }

    //Lấy sản phẩm theo danh mục
    fun selectProductByCategory(id: String, callBack: (List<Product>) -> Unit) {
        db.collection("products").whereEqualTo("idCategory", id).limit(10).get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (item in documents) {
                    val pro = item.toObject(Product::class.java)
                    pro.id = item.id

                    list.add(pro)
                }
                callBack(list)
            }.addOnFailureListener {
                Log.e(ContentValues.TAG, "Có lỗi: ${it.message}")
                callBack(emptyList())
            }
    }


    suspend fun addImages(uris: List<Uri>, onDataLoaded: (List<String>) -> Unit) {
        try {
            val storageRef = FirebaseStorage.getInstance().reference.child("products")

            val uri: List<String> = withContext(Dispatchers.IO) {
                uris.map { image ->
                    async {
                        val imageName = "${System.currentTimeMillis()}_${image.lastPathSegment}"
                        val imageRef = storageRef.child(imageName)
                        val uploadTask = imageRef.putFile(image)
                        val downloadUrl = uploadTask.await().storage.downloadUrl.await()
                        downloadUrl.toString()
                    }
                }.awaitAll()
            }
            onDataLoaded(uri)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            onDataLoaded(emptyList())
        }
    }

    fun addProduct(product: Product,productDetails: List<ProductDetails>, onResult: (Boolean) -> Unit) {

        val classifyCollection = db.collection("ProductDetails")

        try{
            db.collection("Products").add(product).addOnSuccessListener { productDocRef->
                val productId = productDocRef.id
                productDetails.forEach { productDetail ->
                    productDetail.productId = productId
                    classifyCollection.add(productDetail)
                }
                onResult(true)
            }.addOnFailureListener {e->
                Log.e(TAG, "Transaction failed: ${e.message}")
                onResult(false)
            }
        }catch (e:Exception){
            Log.e(TAG, "Transaction failed: ${e.message}")
            onResult(false)
        }

    }

    fun getProductDetails(idProduct:String,onDataLoader: (List<ProductDetails>) -> Unit){
        db.collection("ProductDetails").whereEqualTo("productId",idProduct).get()
            .addOnSuccessListener {documents->
                val list = mutableListOf<ProductDetails>()
                for(item in documents){
                    val product = item.toObject(ProductDetails::class.java)
                    product.id = item.id
                    list.add(product)
                }
                onDataLoader(list)
            }
            .addOnFailureListener {
                Log.e(ContentValues.TAG, "Lỗi: .", it)
                onDataLoader(emptyList())
            }
    }

}