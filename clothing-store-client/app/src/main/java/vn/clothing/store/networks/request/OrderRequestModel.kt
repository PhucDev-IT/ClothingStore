package vn.clothing.store.networks.request

import com.google.gson.annotations.SerializedName

data class OrderRequestModel(
    var total: Double,
    @SerializedName("real_total")
    var realTotal: Double,
    @SerializedName("delivery_information")
    var shippingAddress: String,
    @SerializedName("voucher_id")
    var voucherId: String?,
    var status:String = "PENDING",
    @SerializedName("list_item")
    var listItem: List<OrderItemRequestModel>
)

data class OrderItemRequestModel(
    var quantity: Int,
    var price:Double,
    var color: String,
    var size: String,
    @SerializedName("product_id")
    var productId: String,
)
//User_id get from token
// order_date auto add in code nodejs, not add at client
