package com.example.clothingstoreapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ShippingMethod
import com.example.clothingstoreapp.Model.Voucher

class PayOrderViewModel : ViewModel() {

    private val _mCarts = MutableLiveData<List<ItemCart>>()
    val mCarts:LiveData<List<ItemCart>> = _mCarts

    private val _voucher = MutableLiveData<Voucher?>(null)
    val voucher: LiveData<Voucher?> = _voucher

    private val _deliveryAddress  = MutableLiveData<AddressModel?>()
    val deliveryAddress: LiveData<AddressModel?> = _deliveryAddress

    private val _selectTransportUnit  = MutableLiveData<ShippingMethod?>()
    val selectTransportUnit: LiveData<ShippingMethod?> = _selectTransportUnit

    private val _paymentMethod  = MutableLiveData("Thanh toán khi nhận hàng")
    val paymentMethod: LiveData<String> = _paymentMethod

    private val _order  = MutableLiveData<OrderModel>()
    val order: LiveData<OrderModel> = _order


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