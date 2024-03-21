package com.example.clothingstoreapp.Model


import android.net.Uri
import java.io.Serializable
import java.util.Date


class Product : Serializable{


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

    private var _sale: Double = 0.0
    var sale: Double
        get() = _sale
        set(value) {
            _sale = value
        }

    private var _description: String? = null
    var description: String?
        get() = _description
        set(value) {
            _description = value
        }


    private var _images: List<String>? = null
    var images: List<String>?
        get() = _images
        set(value) {
            _images = value
        }


    private var _createdTime: Date = Date()
    var createdTime: Date
        get() = _createdTime
        set(value) {
            _createdTime = value
        }

    private var _rateEvaluate:Float = 5.0f
    var rateEvaluate: Float
        get() = _rateEvaluate
        set(value) {
            _rateEvaluate = value
        }

    private var _idCategory: List<String>? = null
    var idCategory: List<String>?
        get() = _idCategory
        set(value) {
            _idCategory = value
        }
}