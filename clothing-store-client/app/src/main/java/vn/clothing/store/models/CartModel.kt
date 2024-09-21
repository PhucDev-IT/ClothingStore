package vn.clothing.store.models

import com.google.gson.annotations.SerializedName

class CartModel {
    var id:Int?=null
    @SerializedName("user_id")
    var userId:String?=null

    internal class CartItem{
        var id:Int?=null
        @SerializedName("product_details_id")
        var productDetailsId:Int?=null
        @SerializedName("cart_id")
        var cartId:Int?=null
        var quantity:Int?=null
    }
}