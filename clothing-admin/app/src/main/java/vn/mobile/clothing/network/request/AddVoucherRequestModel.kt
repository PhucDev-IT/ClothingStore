package vn.mobile.clothing.network.request

import com.google.gson.annotations.SerializedName
import vn.mobile.clothing.models.VoucherModel

class AddVoucherRequestModel: VoucherModel() {
    var condition:String?= "all"
    @SerializedName("user_id")
    var userId:String?=null
    var quantity:Int = 1
}