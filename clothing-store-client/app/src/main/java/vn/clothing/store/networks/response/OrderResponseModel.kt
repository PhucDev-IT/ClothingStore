package vn.clothing.store.networks.response

import com.google.gson.annotations.SerializedName
import java.util.Date


class PurchaseHistoryResponseModel{
    var orders:List<OrderResponseModel>? = null
    var pagination:Pagination?=null
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
    var productName:String

)

class Pagination(var totalItems:Int?,var totalPages:Int?,var currentPage:Int?,var itemsPerPage:Int?)