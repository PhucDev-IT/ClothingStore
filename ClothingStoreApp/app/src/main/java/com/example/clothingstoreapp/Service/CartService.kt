package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.CustomProduct
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.Model.ProductDetails
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartService(private val db: FirebaseFirestore) {

    private val maxSize: Long = 10


    fun addToCart(
        idUser: String,
        cart: ItemCart,
        resultData: (b: Boolean) -> Unit
    ) {


        db.collection("carts")
            .document(idUser)
            .collection("cartItems")
            .whereEqualTo("productId", cart.productId)
            .whereEqualTo("classifyId", cart.classifyId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    var isUpdated = false
                    for (document in documents) {
                        val value = document.toObject(ItemCart::class.java)
                        value.id = document.id
                        value.quantity += cart.quantity

                        db.collection("carts")
                            .document(idUser)
                            .collection("cartItems")
                            .document(document.id)
                            .set(value)
                            .addOnSuccessListener {
                                isUpdated = true
                                resultData(true)
                            }
                            .addOnFailureListener {
                                Log.e("Error", "Error adding document", it)
                                resultData(false)
                            }
                        break
                    }
                    if (!isUpdated) {
                        addToCartData(idUser, cart, resultData)
                    }
                } else {
                    addToCartData(idUser, cart, resultData)
                }
            }
            .addOnFailureListener { e ->
                resultData(false)
                Log.e("Error", "Error found document", e)
            }
    }

    private fun addToCartData(
        idUser: String,
        cartData: ItemCart,
        resultData: (b: Boolean) -> Unit
    ) {
        db.collection("carts")
            .document(idUser)
            .collection("cartItems")
            .add(cartData)
            .addOnSuccessListener {
                resultData(true)
            }
            .addOnFailureListener { e ->
                resultData(false)
                Log.e("Error", "Error adding document", e)
            }
    }


    suspend fun selectData(idUser: String): List<ItemCart> = withContext(Dispatchers.IO) {
        try {
            val result = db.collection("carts").document(idUser).collection("cartItems")
                .limit(maxSize)
                .get()
                .await()

            val cartTasks = result.documents.map { document ->
                val idProduct = document.getString("productId") ?: return@map null
                val idClassify = document.getString("classifyId")

                val productTask = async {
                    val productDoc = db.collection("Products").document(idProduct).get().await()
                    if (productDoc.exists()) {
                        val product = productDoc.toObject(Product::class.java)!!
                        val cart = document.toObject(ItemCart::class.java)

                        cart?.name = product.name
                        cart?.image = product.images?.get(0)
                        cart?.price = product.price


                        idClassify?.let { classifyId ->
                            val classifyDoc = db.collection("ProductDetails").document(classifyId).get().await()
                            val item = classifyDoc.toObject(ProductDetails::class.java)
                            cart?.productDetail = item

                        }
                        cart
                    } else {
                        null
                    }
                }
                productTask
            }

            val carts = mutableListOf<ItemCart>()
            cartTasks.mapNotNull { it?.await() }.forEach { cart ->
                carts.add(cart)
            }

            carts
        } catch (e: Exception) {
            Log.e("Error", "Error fetching documents", e)
            emptyList<ItemCart>()
        }
    }
//    fun removeItemCart(list: List<ItemCart>, userID: String, onResult: (Boolean) -> Unit) {
//        val query = db.collection("carts").document(userID).collection("cartItems")
//
//        val tasks = mutableListOf<Task<Void>>()
//
//        for (item in list) {
//            item.idCart?.let {
//                val deleteTask = query.document(it).delete()
//                Log.w(TAG,"Id: ${item.idCart} - $deleteTask")
//                tasks.add(deleteTask)
//            }
//        }
//
//        Tasks.whenAllComplete(tasks)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    onResult(true) // Tất cả xóa thành công
//                } else {
//                    onResult(false) // Có lỗi xảy ra
//                    Log.e(TAG, task.exception.toString())
//                }
//            }
//    }


}