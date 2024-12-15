package vn.clothing.store.networks.response

import com.google.gson.annotations.SerializedName
import vn.mobile.banking.network.response.Pagination
import java.util.Date

class PreviewOrderResponseModel {
    @SerializedName("order_id")
    var orderId: String? = null
    @SerializedName("real_total")
    var realTotal: Double? = null
    @SerializedName("order_date")
    var orderDate: Date? = null
    @SerializedName("user_id")
    var userId: String? = null
    @SerializedName("item_count")
    var itemCount: Int? = null
    @SerializedName("first_product_image")
    var image: String? = null
    @SerializedName("first_product_name")
    var productName: String? = null
}
class PurchaseHistoryResponseModel{
    var orders:List<PreviewOrderResponseModel>? = null
    var pagination: Pagination?=null
}
