package com.example.clothingstoreapp.Model


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


    private var _description: String? = null
    var description: String?
        get() = _description
        set(value) {
            _description = value
        }

    private var _quantity: Int? = null
    var quantity: Int?
        get() = _quantity
        set(value) {
            _quantity = value
        }


    private var _classifies: List<String>? = null
    var classifies: List<String>?
        get() = _classifies
        set(value) {
            _classifies = value
        }

    private var _img_preview: List<String>? = null
    var img_preview: List<String>?
        get() = _img_preview
        set(value) {
            _img_preview = value
        }

    private var _colors: Map<String,String>? = null
    var colors: Map<String,String>?
        get() = _colors
        set(value) {
            _colors = value
        }

    private var _tags: List<String>? = null
    var tags: List<String>?
        get() = _tags
        set(value) {
            _tags = value
        }

    private var _createdTime: Date? = null
    var createdTime: Date?
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

    private var _idCategory: String? = null
    var idCategory: String?
        get() = _idCategory
        set(value) {
            _idCategory = value
        }
}