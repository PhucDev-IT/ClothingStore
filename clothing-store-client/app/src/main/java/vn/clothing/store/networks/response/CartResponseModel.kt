package vn.clothing.store.networks.response

import com.google.gson.annotations.SerializedName
import vn.clothing.store.models.ProductDetails
import java.io.Serializable

class CartResponseModel {
    @SerializedName("cart_id")
    var cartId:String?=null
    @SerializedName("user_id")
    var userId:String?=null

    @SerializedName("list_item")
    var listItem:ArrayList<CartItemResponseModel>?=null


     class CartItemResponseModel : Serializable{
        var id:Int?=null
         @SerializedName("product_id")
        var productId:String?=null
        var quantity:Int?=null
        var color:String?=null
        var size:String?=null
        var image:String?=null
        var name:String?=null
        var price:Double?=null
    }
}