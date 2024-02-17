package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Model.Voucher
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicInteger

class VoucherService(private val db:FirebaseFirestore) {

    fun selectAllVoucherNotUse(uid: String, onDataLoader: (List<Voucher>) -> Unit) = runBlocking {

        val documents = db.collection("vouchers").get().await()

        val listVouchers = documents.documents.map { document ->
            val idVoucher = document.id
            val voucher = async(Dispatchers.IO) {
                val result = db.collection("orders")
                    .whereEqualTo("voucher.id", idVoucher)
                    .whereEqualTo("user.userId", uid).get().await()

                if (result.isEmpty) {
                    document.toObject(Voucher::class.java)
                } else {
                    null
                }
            }
            voucher
        }

        val resultVouchers = listVouchers.awaitAll().filterNotNull()
        onDataLoader(resultVouchers)
    }


    fun selectMyVouchers(uid:String,onDataLoader:(List<Voucher>)->Unit)
    {
        db.collection("myVouchers").document(uid).collection("items").get()
            .addOnSuccessListener { documents->
                val listVouchers = mutableListOf<Voucher>()
                val tasksCount = AtomicInteger(documents.size())

                for (document in documents){
                    val idVoucher = document.id

                    db.collection("orders").whereEqualTo("voucher.id",idVoucher)
                        .whereEqualTo("idUser",uid).get().addOnSuccessListener { result->
                            if(result.isEmpty){
                                val voucher = document.toObject(Voucher::class.java)
                                listVouchers.add(voucher)
                            }

                            if (tasksCount.decrementAndGet() == 0) {
                                onDataLoader(listVouchers)
                            }
                        }
                }
            }.addOnFailureListener {
                Log.e(TAG,"lỗi: $it")
            }
    }

    //Nhập mã voucher
    fun findVoucher(idVoucher:String,onDataLoader:(Voucher?,String?)->Unit){
        val tasksCount = AtomicInteger(1)
        val userCurrent = UserManager.getInstance().getUserCurrent()

        db.collection("vouchers").document(idVoucher).get().addOnSuccessListener { result->
            if(result.exists()){

                val voucher = result.toObject(Voucher::class.java)

                if(voucher!=null){

                    db.collection("orders").whereEqualTo("voucher.id",idVoucher)
                        .whereEqualTo("idUser",userCurrent?.id).get().addOnSuccessListener { value->
                            if(value.isEmpty){
                                if (tasksCount.decrementAndGet() == 0) {
                                    onDataLoader(voucher,null)
                                }

                            }else{
                                onDataLoader(null,"Bạn không đã sử dụng voucher này")
                            }
                        }

                }else{
                    onDataLoader(null,"Bạn không thể dùng voucher này")
                }

            }else{
                onDataLoader(null,"Mã voucher không tồn tại")
            }
        }.addOnFailureListener {
            Log.e(TAG,"Fail: $it")
            onDataLoader(null,"Mã voucher không tồn tại")
        }
    }
}