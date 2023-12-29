package com.example.clothingstoreadmin.model

import java.io.Serializable

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

}