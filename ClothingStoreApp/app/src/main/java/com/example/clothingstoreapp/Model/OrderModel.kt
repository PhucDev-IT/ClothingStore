package com.example.clothingstoreapp.Model

import com.google.type.DateTime
import java.io.Serializable
import java.util.Date



 class UserOrder():Serializable{
     var userId: String?=null
     var tokenFCM:String?=null

     constructor(id:String,token:String?) : this() {
         userId = id
         tokenFCM = token
     }
 }

class OrderModel:Serializable {


    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }


    private var _user: UserOrder? = null
    var user: UserOrder?
        get() = _user
        set(value) {
            _user = value
        }

    private var _deliveryAddress: AddressModel? = null
    var deliveryAddress: AddressModel?
        get() = _deliveryAddress
        set(value) {
            _deliveryAddress = value
        }

    private var _carts: List<ItemCart>? = null
    var carts: List<ItemCart>?
        get() = _carts
        set(value) {
            _carts = value
        }

    private var _orderDate: Date? = null
    var orderDate: Date?
        get() = _orderDate
        set(value) {
            _orderDate = value
        }

    private var _orderStatus: MutableMap<String,Date>? = null
    var orderStatus: MutableMap<String,Date>?
        get() = _orderStatus
        set(value) {
            _orderStatus = value
        }

    private var _voucher: Voucher? = null
    var voucher: Voucher?
        get() = _voucher
        set(value) {
            _voucher = value
        }

    private var _paymentMethod: String? = "Thanh toán khi nhận hàng"
    var paymentMethod: String?
        get() = _paymentMethod
        set(value) {
            _paymentMethod = value
        }

    private var _totalMoney: Double? = null
    var totalMoney: Double?
        get() = _totalMoney
        set(value) {
            _totalMoney = value
        }

    private var _feeShip: Double? = null
    var feeShip: Double?
        get() = _feeShip
        set(value) {
            _feeShip = value
        }

    private var _currentStatus: String? = null
    var currentStatus: String?
        get() = _currentStatus
        set(value) {
            _currentStatus = value
        }

}