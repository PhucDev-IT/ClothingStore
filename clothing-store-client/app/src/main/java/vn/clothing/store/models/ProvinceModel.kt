package vn.clothing.store.models

import com.google.gson.annotations.SerializedName

class ProvinceModel {
    var code:String?=null
    var name:String?=null
    var slug:String?=null
    var type:String?=null
    @SerializedName("name_with_type")
    var nameWithType:String?=null
}