package com.example.clothingstoreadmin.model


import java.io.Serializable

class CustomProduct :Serializable{
    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    private var _name: String? = null
    var name: String?
        get() = _name
        set(value) {
            _name = value
        }


    private var _price: Double? = null
    var price: Double?
        get() = _price
        set(value) {
            _price = value
        }

    private var _imgPreview: String? = null
    var imgPreview: String?
        get() = _imgPreview
        set(value) {
            _imgPreview = value
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

    private var _quantity: Int? = null
    var quantity: Int?
        get() = _quantity
        set(value) {
            _quantity = value
        }
    constructor()

    constructor(id:String,quantity:Int,name:String,img:String,price: Double){
        this._id = id
        this._quantity = quantity
        this._name = name
        this._imgPreview = img
        this._price = price
    }

    constructor(id:String,name:String,img:String,price:Double,classify:String,color:String,quantity:Int){
        this._id = id
        this._name = name
        this._imgPreview = img
        this._price = price
        this._classify = classify
        this._color = color
        this._quantity = quantity
    }

    override fun toString(): String {
        return "CustomProduct(_id=$_id, _name=$_name, _price=$_price, _imgPreview=$_imgPreview, _classify=$_classify, _color=$_color, _quantity=$_quantity)"
    }


}