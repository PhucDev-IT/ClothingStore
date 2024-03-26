package com.example.clothingstoreadmin.model

import java.io.Serializable


class ItemCart() : Serializable,CustomProduct() {
    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    private var _productId: String? = null
    var productId: String?
        get() = _productId
        set(value) {
            _productId = value
        }


    private var _classifyId: String? = null
    var classifyId: String?
        get() = _classifyId
        set(value) {
            _classifyId = value
        }


    private var _quantity: Int = 1
    var quantity: Int
        get() = _quantity
        set(value) {
            _quantity = value
        }




    constructor(productId: String, quantity: Int) : this() {
        this._productId = productId
        this._quantity = quantity
    }

    constructor(productId: String, classify: String, quantity: Int) : this() {
        this._productId = productId
        this._classifyId = classify
        this._quantity = quantity
    }

    override fun toString(): String {
        return "ItemCart(_id=$_id, _productId=$_productId, _classifyId=$_classifyId, _quantity=$_quantity)"
    }


}