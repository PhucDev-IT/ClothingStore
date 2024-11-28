package vn.mobile.clothing.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

class VoucherModel : Serializable{
    var id:String?=null
    var title:String?=null
    var description:String?=null
    var discount:Float?=null
    var type:String?=null
    @SerializedName("start_at")
    var startAt:Date?=null
    @SerializedName("end_at")
    var endAt:Date?=null
    @SerializedName("is_public")
    var isPublic:String?=null
}