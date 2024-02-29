package com.example.clothingstoreapp.Model

import java.io.Serializable

class OrderDetails:Serializable {
    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    private var _products:List<CustomProduct>?=null
    var products:List<CustomProduct>?
        get() = _products
        set(value) {
            _products = value
        }
}

