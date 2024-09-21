package vn.clothing.store.networks.response

import com.google.gson.annotations.SerializedName
import vn.clothing.store.models.ProductDetails

class CartResponseModel {
    @SerializedName("id")
    var cartId:String?=null
    @SerializedName("user_id")
    var userId:String?=null

    var listItem:ArrayList<CartItemResponseModel>?=null

     class CartItemResponseModel {
        var id:String?=null
        var productId:String?=null
        var quantity:Int?=null
        var color:String?=null
        var size:String?=null
        var image:String?=null
        var name:String?=null
        var price:Double?=null
    }
}