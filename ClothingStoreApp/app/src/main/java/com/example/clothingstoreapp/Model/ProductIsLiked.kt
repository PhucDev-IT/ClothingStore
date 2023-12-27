package com.example.clothingstoreapp.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ProductIsLiked {

    @PrimaryKey
    lateinit var idProduct:String
    var idCategory:String?=null
}