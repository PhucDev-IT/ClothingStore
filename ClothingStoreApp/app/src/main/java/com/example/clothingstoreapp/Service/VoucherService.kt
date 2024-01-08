package com.example.clothingstoreapp.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Model.Voucher
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.concurrent.atomic.AtomicInteger

class VoucherService(private val db:FirebaseFirestore) {

    fun selectAllVoucherNotUse(uid:String,onDataLoader:(List<Voucher>)->Unit){

        db.collection("vouchers").get().addOnSuccessListener { documents->

            val listVouchers = mutableListOf<Voucher>()
            val tasksCount = AtomicInteger(documents.size())

            for (document in documents){
                val idVoucher = document.id

                db.collection("orders").whereEqualTo("voucher.id",idVoucher)
                    .whereEqualTo("user.userID",uid).get().addOnSuccessListener { result->
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