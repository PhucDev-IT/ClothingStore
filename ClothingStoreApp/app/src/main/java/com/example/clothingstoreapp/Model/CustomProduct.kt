package com.example.clothingstoreapp.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable



open class CustomProduct() :Serializable {


    private var _name: String? = null
    var name: String?
        get() = _name
        set(value) {
            _name = value
        }


    private var _image: String? = null
    var image: String?
        get() = _image
        set(value) {
            _image = value
        }

    private var _price: Double? = null
    var price: Double?
        get() = _price
        set(value) {
            _price = value
        }

    private var _productDetail : ProductDetails?=null
    var productDetail : ProductDetails?
        get() = _productDetail
        set(value) {
            _productDetail = value
        }

    constructor(name: String?, image: String?, price: Double) : this() {

        this._name = name
        this._image = image
        this._price = price
    }

    override fun toString(): String {
        return "CustomProduct( _name=$_name, _image=$_image, _price=$_price, _productDetails=$_productDetail)"
    }


}