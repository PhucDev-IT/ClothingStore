package com.example.clothingstoreapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Service.OrderService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class PurchaseHistoryViewModel:ViewModel() {

    private val orderService = OrderService()
    private val _mListOrder = MutableLiveData<List<OrderModel>>()
    val mListOrder: LiveData<List<OrderModel>> = _mListOrder

    private val _mListDelivery = MutableLiveData<List<OrderModel>>()
    val mListDelivery: LiveData<List<OrderModel>> = _mListDelivery

    private val _mListCancel = MutableLiveData<List<OrderModel>>()
    val mListCancel: LiveData<List<OrderModel>> = _mListCancel


    //Lấy đơn hàng đang chờ xác nhận
    fun getOrder(callBack:(List<OrderModel>)->Unit){
        orderService.getOrderTransporting {list->
            _mListOrder.value = list
            callBack(list)
        }
    }

    fun getOrderDelivery(callBack:(List<OrderModel>)->Unit){
        orderService.getOrderDelivery{list->
            _mListDelivery.value = list
            callBack(list)
        }
    }

    fun getOrderCanceled(callBack:(List<OrderModel>)->Unit){
        orderService.getCancelOrder{list->
            _mListCancel.value = list
            callBack(list)
        }
    }


}