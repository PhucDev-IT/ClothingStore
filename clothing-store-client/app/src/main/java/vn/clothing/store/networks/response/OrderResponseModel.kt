package vn.clothing.store.networks.response

import com.google.gson.annotations.SerializedName
import vn.mobile.banking.network.response.Pagination
import java.io.Serializable
import java.util.Date



class OrderResponseModel (
    @SerializedName("id")
    var orderId:String?,
    var total:Double,
    @SerializedName("real_total")
    var realTotal:Double,
    var discount:Double,
    @SerializedName("payment_method")
    var paymentMethod:String,
    @SerializedName("order_date")
    var orderDate:Date,
    @SerializedName("fee_ship")
    var feeShip:Float?,
    @SerializedName("delivery_information")
    var shippingAddress:String?,
    @SerializedName("user_id")
    var userId:String,

    ):Serializable

