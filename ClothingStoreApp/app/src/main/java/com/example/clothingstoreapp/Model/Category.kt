package com.example.clothingstoreapp.Model

import java.io.Serializable

class Category:Serializable {

    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }
    private var _nameCategory:String? = null
    var nameCategory:String?
        get() = _nameCategory
        set(value) {
            _nameCategory = value;
        }

    private var _url:String? = null
    var url:String?
        get() = _url
        set(value) {
            _url = value;
        }
}