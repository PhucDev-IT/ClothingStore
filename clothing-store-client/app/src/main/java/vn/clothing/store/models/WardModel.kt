package vn.clothing.store.models

import com.google.gson.annotations.SerializedName

class WardModel {
    var code:String?=null
    var name:String?=null
    var slug:String?=null
    var type:String?=null
    @SerializedName("name_with_type")
    var nameWithType:String?=null
    var path:String?=null
    @SerializedName("path_with_type")
    var pathWithType:String?=null
    @SerializedName("parent_code")
    var parentCode:String?=null
}