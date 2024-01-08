package com.example.clothingstoreadmin.service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreadmin.model.CustomProduct
import com.example.clothingstoreadmin.model.ItemCart
import com.example.clothingstoreadmin.model.Product
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject

class CartService(private val db: FirebaseFirestore) {

    private val maxSize: Long = 10


    fun addToCart(idUser: String, cart: ItemCart, resultData: (b: Boolean) -> Unit) {
        val cartData = hashMapOf(
            "idProduct" to cart.idProduct,
            "quantity" to cart.quantity,
            "classify" to cart.classify,
            "color" to cart.color
        )



        db.collection("carts")
            .document(idUser)
            .collection("cartItems").whereEqualTo("idProduct", cart.idProduct)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val value = document.toObject(ItemCart::class.java)
                        if (value.color == cart.color && value.classify == cart.classify) {
                            val quantity = value.quantity?.plus(cart.quantity!!)
                            db.collection("carts")
                                .document(idUser)
                                .collection("cartItems").document(document.id)
                                .update("quantity", quantity)
                                .addOnSuccessListener {
                                    resultData(true)
                                }
                                .addOnFailureListener {
                                    Log.e("Error", "Error adding document", it)
                                    resultData(false)
                                }
                            break
                        }
                    }
                } else {
                    db.collection("carts")
                        .document(idUser)
                        .collection("cartItems").add(cartData)
                        .addOnSuccessListener { _ ->
                            resultData(true)
                        }
                        .addOnFailureListener { e ->
                            resultData(false)
                            Log.e("Error", "Error adding document", e)
                        }
                }
            }.addOnFailureListener { e ->
                resultData(false)
                Log.e("Error", "Error found document", e)
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

}