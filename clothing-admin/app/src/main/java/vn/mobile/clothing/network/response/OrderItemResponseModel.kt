package vn.mobile.clothing.network.response

import com.google.gson.annotations.SerializedName
import vn.mobile.clothing.models.VoucherModel

import java.util.Date

class OrderDetailsResponseModel {
    @SerializedName("order_items")
    var orderItems: List<OrderItemResponseModel>? = null
    var voucher: VoucherModel? = null
    @SerializedName("order_status")
    var orderStatus: List<OrderStatus>? = null
}

class OrderItemResponseModel {
    @SerializedName("product_id")
    var productId: String? = null

    @SerializedName("img_preview")
    var image: String? = null

    @SerializedName("name")
    var productName: String? = null
    var id: String? = null

    @SerializedName("order_id")
    var orderId: String? = null
    var color: String? = null
    var size: String? = null
    var quantity: Int? = null
    var price: Float? = null
}

class OrderStatus {
    @SerializedName("order_id")
    var orderId:String?=null
    @SerializedName("user_id")
    var userId:String?=null
    var status: String? = null
    var note: String? = null
    var updatedAt: Date? = null
}