package vn.clothing.store.networks.request

import com.google.gson.annotations.SerializedName

data class OrderRequestModel(
    var total: Double,
    @SerializedName("real_total")
    var realTotal: Double,
    var discount: Double,
    @SerializedName("payment_method")
    var paymentMethod:String,
    @SerializedName("delivery_information")
    var shippingAddress: String,
    @SerializedName("voucher_id")
    var voucherId: String?,
    @SerializedName("fee_ship")
    var feeShip:Float,
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
