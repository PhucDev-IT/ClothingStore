package vn.clothing.store.models

import com.google.gson.annotations.SerializedName

class ProductDetails {
    var id:Int?=null
    var quantity:Int?=null
    var color:String?=null
    var size:String?=null
    var price:Float = 0f
    @SerializedName("product_id")
    var productId:String?=null
}