package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.CustomProduct
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.Product
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class CartService(private val db: FirebaseFirestore) {

    private val maxSize: Long = 10


    fun addToCart(
        idUser: String,
        cart: ItemCart,
        resultData: (b: Boolean) -> Unit
    ) {
        val cartData = hashMapOf<String, Any>(
            "idProduct" to cart.idProduct!!,
            "quantity" to cart.quantity,
            "classify" to cart.classify!!,
            "color" to cart.color!!
        )


        db.collection("carts")
            .document(idUser)
            .collection("cartItems")
            .whereEqualTo("idProduct", cart.idProduct)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    var isUpdated = false
                    for (document in documents) {
                        val value = document.toObject(ItemCart::class.java)
                        if (value.color == cart.color && value.classify == cart.classify) {
                            val quantity = value.quantity + cart.quantity
                            db.collection("carts")
                                .document(idUser)
                                .collection("cartItems")
                                .document(document.id)
                                .update("quantity", quantity)
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
                    }
                    if (!isUpdated) {
                        addToCartData(idUser, cartData, resultData)
                    }
                } else {
                    addToCartData(idUser, cartData, resultData)
                }
            }
            .addOnFailureListener { e ->
                resultData(false)
                Log.e("Error", "Error found document", e)
            }
    }

    private fun addToCartData(
        idUser: String,
        cartData: HashMap<String, Any>,
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


    fun selectData(idUser: String, onDataLoader: (List<ItemCart>) -> Unit) {
        db.collection("carts").document(idUser).collection("cartItems")
            .limit(maxSize)
            .get()
            .addOnSuccessListener { result ->
                val cartTasks = mutableListOf<Task<DocumentSnapshot>>()
                val list = mutableListOf<ItemCart>()

                for (document in result) {
                    val idProduct = document.getString("idProduct")
                    val cartTask = db.collection("products").document(idProduct!!).get()
                    cartTasks.add(cartTask)

                    cartTask.addOnSuccessListener { documentRef ->
                        if (documentRef.exists()) {
                            val cart = document.toObject(ItemCart::class.java)
                            cart.idCart = document.id
                            val product = documentRef.toObject(Product::class.java)
                            product?.id = documentRef.id

                            cart.product = CustomProduct(
                                product?.id!!, product?.name!!, product.img_preview!![0],
                                product.price!!
                            )

                            list.add(cart)
                        }
                    }
                }

                Tasks.whenAllSuccess<DocumentSnapshot>(cartTasks)
                    .addOnSuccessListener {
                        onDataLoader(list)
                    }
                    .addOnFailureListener { e ->
                        onDataLoader(emptyList())
                        Log.e("Error", "Error fetching documents", e)
                    }
            }
            .addOnFailureListener { e ->
                onDataLoader(emptyList())
                Log.e("Error", "Error fetching documents", e)
            }
    }

    fun removeItemCart(list: List<ItemCart>, userID: String, onResult: (Boolean) -> Unit) {
        val query = db.collection("carts").document(userID).collection("cartItems")

        val tasks = mutableListOf<Task<Void>>()

        for (item in list) {
            item.idCart?.let {
                val deleteTask = query.document(it).delete()
                Log.w(TAG,"Id: ${item.idCart} - $deleteTask")
                tasks.add(deleteTask)
            }
        }

        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true) // Tất cả xóa thành công
                } else {
                    onResult(false) // Có lỗi xảy ra
                    Log.e(TAG, task.exception.toString())
                }
            }
    }


}