package com.example.clothingstoreadmin.model

import java.io.Serializable
import java.util.Date

class Voucher :Serializable{
    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    private var _title: String? = null
    var title: String?
        get() = _title
        set(value) {
            _title = value
        }

    private var _content: String? = null
    var content: String?
        get() = _content
        set(value) {
            _content = value
        }

    private var _discount: Double? = null
    var discount: Double?
        get() = _discount
        set(value) {
            _discount = value
        }

    private var _typeVoucher:String?=null
    var typeVoucher: String?
        get() = _typeVoucher
        set(value) {
            _typeVoucher = value
        }

    private var _quantity:Int?=null
    var quantity: Int?
        get() = _quantity
        set(value) {
            _quantity = value
        }

    private var _timeStart:Date?=null
    var timeStart: Date?
        get() = _timeStart
        set(value) {
            _timeStart = value
        }

    private var _timeEnd:Date?=null
    var timeEnd: Date?
        get() = _timeEnd
        set(value) {
            _timeEnd = value
        }
}