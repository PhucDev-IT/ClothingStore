package com.example.clothingstoreadmin.model



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

    private var _products: List<ItemCart>? = null
    var products: List<ItemCart>?
        get() = _products
        set(value) {
            _products = value
        }

    private var _orderDate: Long = Date().time
    var orderDate: Long
        get() = _orderDate
        set(value) {
            _orderDate = value
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

    private var _feeShip: Double = 0.0
    var feeShip: Double
        get() = _feeShip
        set(value) {
            _feeShip = value
        }

    private var _currentStatus: String = ProgressOrder.WaitConfirmOrder.name
    var currentStatus: String
        get() = _currentStatus
        set(value) {
            _currentStatus = value
        }


}