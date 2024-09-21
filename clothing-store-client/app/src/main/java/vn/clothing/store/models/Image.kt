package vn.clothing.store.models

import com.google.gson.annotations.SerializedName

class Image {
    var id:Int?=null
    @SerializedName("model_name")
    var modelName:Int?=null
    @SerializedName("model_id")
    var modelId:Int?=null
    var path:Int?=null
}