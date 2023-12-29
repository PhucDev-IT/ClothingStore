package com.example.clothingstoreadmin.model

import java.io.Serializable

 class CustomProduct(): Serializable {
     var id:String?=null
     var name:String?=null
     var imgPreview:String?=null
     var price:Double?=null


     constructor(id:String,name:String,img:String,price:Double) : this() {
         this.id = id
         this.name = name
         this.imgPreview = img
         this.price = price

     }


 }