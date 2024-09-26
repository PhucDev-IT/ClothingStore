package vn.clothing.store.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity("delivery_information")
class DeliveryInformation {
    @PrimaryKey
    var id:String = ""
    @SerializedName("province_id")
    var provinceId:String?=null
    @SerializedName("district_id")
    var districtId:String?=null
    @SerializedName("ward_id")
    var wardId:String?=null
    @SerializedName("number_phone")
    var numberPhone:String?=null
    var details:String = ""
    @SerializedName("full_name")
    var fullName:String?=null
    @SerializedName("user_id")
    var userId:String?=null
    @SerializedName("is_default")
    var isDefault:Boolean =false

}