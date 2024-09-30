package vn.clothing.store.models

import com.google.gson.annotations.SerializedName
import java.util.Date

class Order {
    var id:String?=null
    var total: Double?=null
    @SerializedName("real_total")
    var realTotal: Double?=null
    @SerializedName("delivery_information")
    var shippingAddress: String?=null
    @SerializedName("voucher_id")
    var voucherId: String?=null
    @SerializedName("product_id")
    var productId: String?=null
    @SerializedName("order_date")
    var orderDate: Date?=null
    @SerializedName("user_id")
    var userId: String?=null
    var status: String?=null
    @SerializedName("fee_ship")
    var feeShip: Float?=null
    @SerializedName("list_item")
    var listItem: List<OrderItem>?=null

    var product:Product?=null
}

class OrderItem{
    var id:String?=null
    @SerializedName("order_id")
    var orderId:String?=null
    var color:String?=null
    var size:String?=null
    var quantity:Int?=null
    var price:Float?=null
}