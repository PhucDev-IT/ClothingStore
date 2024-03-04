package com.example.clothingstoreadmin.model

import java.io.Serializable


class ItemCart : Serializable{

    private var _idCart: String? = null
    var idCart: String?
        get() = _idCart
        set(value) {
            _idCart = value
        }

    private var _product:CustomProduct?=null
    var product:CustomProduct?
        get() = _product
        set(value) {
            _product = value
        }


}