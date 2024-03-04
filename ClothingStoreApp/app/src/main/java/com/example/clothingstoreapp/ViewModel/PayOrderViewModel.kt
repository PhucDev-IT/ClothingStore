package com.example.clothingstoreapp.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.CustomProduct
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ShippingMethod
import com.example.clothingstoreapp.Model.Voucher
import kotlin.math.log

class PayOrderViewModel : ViewModel() {

    private val _mCarts = MutableLiveData<List<ItemCart>>()
    val mCarts:LiveData<List<ItemCart>> = _mCarts

    private val _voucher = MutableLiveData<Voucher?>(null)
    val voucher: LiveData<Voucher?> = _voucher

    private val _deliveryAddress  = MutableLiveData<AddressModel?>()
    val deliveryAddress: LiveData<AddressModel?> = _deliveryAddress

    private val _transportUnit  = MutableLiveData<Map<ShippingMethod,Int>?>()
    val transportUnit: LiveData<Map<ShippingMethod,Int>?> = _transportUnit

    private val _selectTransportUnit  = MutableLiveData<ShippingMethod?>()
    val selectTransportUnit: LiveData<ShippingMethod?> = _selectTransportUnit

    private val _paymentMethod  = MutableLiveData("Thanh toán khi nhận hàng")
    val paymentMethod: LiveData<String> = _paymentMethod

    private val _order  = MutableLiveData<OrderModel>()
    val order: LiveData<OrderModel> = _order

    private val _feeShip  = MutableLiveData<Int?>(15000)
    val feeShip: LiveData<Int?> = _feeShip

    fun setDeliveryAddress(addressModel: AddressModel?){
        _deliveryAddress.value = addressModel
    }

    fun setListCart(list:List<ItemCart>){
        _mCarts.value = list
    }

    fun setOrder(o:OrderModel)
    {
        _order.value = o
    }

    fun setFeeShip(o:Int?)
    {
        _feeShip.value = o
    }

    fun setTransportUnit(value:Map<ShippingMethod,Int>)
    {
        _transportUnit.value = value
    }

    fun setSelectTransportUnit(value:ShippingMethod)
    {
        _selectTransportUnit.value = value
    }

    fun setVoucher(coupon:Voucher?){
        _voucher.value = coupon
    }

    fun getListCart(): List<ItemCart>? {
        return _mCarts.value
    }

    fun getDeliveryAddress():AddressModel?{
        return deliveryAddress.value
    }



}