package com.example.clothingstoreapp.Model

import java.io.Serializable

class ItemCart : Serializable{

    private var _idCart: String? = null
    var idCart: String?
        get() = _idCart
        set(value) {
            _idCart = value
        }

    private var _idProduct: String? = null
    var idProduct: String?
        get() = _idProduct
        set(value) {
            _idProduct = value
        }

    private var _quantity: Int? = null
    var quantity: Int?
        get() = _quantity
        set(value) {
            _quantity = value
        }

    private var _classify: String? = null
    var classify: String?
        get() = _classify
        set(value) {
            _classify = value
        }

    private var _color: String? = null
    var color: String?
        get() = _color
        set(value) {
            _color = value
        }

    private var _product:Product?=null
    var product:Product?
        get() = _product
        set(value) {
            _product = value
        }
    constructor()
    constructor(idPro:String,quantity:Int){
        _idProduct = idPro
        _quantity = quantity
    }

    constructor(p:Product,number:Int){
        _product = p
        _quantity = number
    }
}