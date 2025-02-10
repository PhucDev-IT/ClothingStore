package vn.clothing.store.models

import com.google.gson.annotations.SerializedName

class Image {
    var id:Int?=null
    @SerializedName("model_name")
    var modelName:String?=null
    @SerializedName("model_id")
    var modelId:String?=null
    var path:String?=null
}