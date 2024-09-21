package vn.clothing.store.models

import com.google.gson.annotations.SerializedName

class Product {
    var id: String? = null
    var name: String? = null
    @SerializedName("img_preview")
    var imgPreview: String? = null
    var price: Float? = null
    var description: String? = null
    var rate: Float? = null
    @SerializedName("is_public")
    var isPublic: Boolean? = null

    var images:ArrayList<Image>?=null
}