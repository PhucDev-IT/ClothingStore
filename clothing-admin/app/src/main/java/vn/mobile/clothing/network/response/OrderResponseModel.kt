package vn.mobile.clothing.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date


class PurchaseHistoryResponseModel{
    var orders:List<OrderResponseModel>? = null
    var pagination: Pagination?=null
}

class OrderResponseModel (
    @SerializedName("order_id")
    var orderId:String?,
    var total:Double,
    @SerializedName("real_total")
    var realTotal:Double,
    var status:String,
    @SerializedName("order_date")
    var orderDate:Date,
    @SerializedName("item_count")
    var itemCount:Int,
    @SerializedName("first_product_image")
    var iamge:String,
    @SerializedName("first_product_name")
    var productName:String,
    @SerializedName("fee_ship")
    var feeShip:Float?,
    @SerializedName("delivery_information")
    var shippingAddress:String?,
    @SerializedName("user_id")
    var userId:String,
    @SerializedName("user_name")
    var userName:String?
):Serializable

