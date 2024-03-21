package com.example.clothingstoreadmin.model

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

    constructor()

    constructor(id:String,name:String){
        this.id = id
        this._nameCategory = name
    }
}