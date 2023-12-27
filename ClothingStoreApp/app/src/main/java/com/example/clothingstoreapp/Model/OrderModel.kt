package com.example.clothingstoreapp.Model

import com.google.type.DateTime
import java.io.Serializable
import java.util.Date

class OrderModel:Serializable {

    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    private var _userID: String? = null
    var userID: String?
        get() = _userID
        set(value) {
            _userID = value
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

    private var _orderStatus: Map<String,Date>? = null
    var orderStatus: Map<String,Date>?
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

}