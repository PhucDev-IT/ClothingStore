package vn.mobile.clothing.network.response

import com.google.gson.annotations.SerializedName

data class TopProductResponseModel (
    @SerializedName("id")
    val productId:String,
    val imgPreview:String,
    val name:String,
    val price:Float,
    val rate:Float,
    val sold:Int,
    val description:String
)