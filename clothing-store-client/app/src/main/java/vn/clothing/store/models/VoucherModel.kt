package vn.clothing.store.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

class VoucherModel : Serializable{
    var id:String?=null
    var title:String?=null
    var description:String?=null
    var discount:Double?=null
    var type:String?=null
    @SerializedName("start_at")
    var startAt:Date?=null
    @SerializedName("end_at")
    var endAt:Date?=null
    var quantity:Int = 0
    var used:Int = 0
    @SerializedName("user_id")
    var userId:String?=null
}

