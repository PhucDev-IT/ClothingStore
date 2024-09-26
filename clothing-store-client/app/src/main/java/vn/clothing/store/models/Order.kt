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
//    @SerializedName("list_item")
//    var listItem: List<OrderItem>?=null


}