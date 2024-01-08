package com.example.clothingstoreadmin.service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreadmin.model.Voucher

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.concurrent.atomic.AtomicInteger

class VoucherService(private val db: FirebaseFirestore) {

    fun selectAllVouchers(callBack: (List<Voucher>) -> Unit) {
        db.collection("vouchers").get().addOnSuccessListener { documents ->
            val list = mutableListOf<Voucher>()

            for (item in documents) {
                val voucher = item.toObject(Voucher::class.java)
                list.add(voucher)
            }

            callBack(list)
        }.addOnFailureListener {
            Log.e(TAG, "Có lỗi xảy ra: ${it.message}")
            callBack(emptyList())
        }
    }


    fun checkIdAvailability(id: String, callBack: (Boolean) -> Unit) {
        db.collection("vouchers").document(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    // Nếu ID đã tồn tại, thử tạo ID mới và kiểm tra lại
                    callBack(true)
                } else {
                   callBack(false)
                }
            }
        }
    }

    fun addVoucher(id: String,voucher:Voucher, callBack: (Boolean,String?) -> Unit){
        db.collection("vouchers").document(id).set(voucher)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    callBack(true,null)
                }else{
                    callBack(false,"Có lỗi xảy ra")
                }
            }.addOnFailureListener {
                Log.e(TAG, "Có lỗi xảy ra: ${it.message}")
                callBack(false,it.message)
            }
    }


}