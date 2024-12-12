package vn.mobile.clothing.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

open class VoucherModel : Serializable{
    var id:String?=null
    var title:String?=null
    var description:String?=null
    var discount:Double?=null
    var type:String?=null
    @SerializedName("start_at")
    var startAt:Date?=null
    @SerializedName("end_at")
    var endAt:Date?=null
    @SerializedName("is_public")
    var isPublic:Boolean?=null

    constructor()
    constructor(
        title: String?,
        description: String?,
        discount: Double?,
        type: String?,
        startAt: Date?,
        endAt: Date?,
        isPublic: Boolean?
    ) {
        this.title = title
        this.description = description
        this.discount = discount
        this.type = type
        this.startAt = startAt
        this.endAt = endAt
        this.isPublic = isPublic
    }
}