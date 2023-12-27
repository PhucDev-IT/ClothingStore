package com.example.clothingstoreapp.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.Voucher
import kotlin.math.log

class PayOrderViewModel : ViewModel() {

    private val _mListCart = MutableLiveData<List<ItemCart>>()
    val mListCart:LiveData<List<ItemCart>> = _mListCart

    private val _voucher = MutableLiveData<Voucher?>(null)
    val voucher: LiveData<Voucher?> = _voucher

    private val _deliveryAddress  = MutableLiveData<AddressModel?>()
    val deliveryAddress: LiveData<AddressModel?> = _deliveryAddress

    private val _paymentMethod  = MutableLiveData("Thanh toán khi nhận hàng")
    val paymentMethod: LiveData<String> = _paymentMethod

    fun setDeliveryAddress(addressModel: AddressModel?){
        _deliveryAddress.value = addressModel
    }

    fun setListCart(list:List<ItemCart>){
        _mListCart.value = list
    }

    fun setVoucher(coupon:Voucher?){
        _voucher.value = coupon
    }

    fun getListCart(): List<ItemCart>? {
        return mListCart.value
    }

    fun getDeliveryAddress():AddressModel?{
        return deliveryAddress.value
    }

}