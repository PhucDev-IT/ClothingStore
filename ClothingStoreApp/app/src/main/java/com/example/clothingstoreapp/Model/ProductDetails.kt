package com.example.clothingstoreapp.Model

import java.io.Serializable
import java.util.Date

class ProductDetails :Serializable{
    var id: String? = null
    var nameClassify: String? = null
    var size:String?=null
    var quantity:Int? = null


    constructor()
    constructor(classify: String, size:String,quantity:Int) {
        this.id = Date().time.toString()
        this.nameClassify = classify
        this.size = size
        this.quantity = quantity

    }
}