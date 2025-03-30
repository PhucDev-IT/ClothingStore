package vn.clothing.store.networks.response

import com.google.gson.annotations.SerializedName

data class EphemeralKey(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("associated_objects") val associatedObjects: List<AssociatedObject>,
    @SerializedName("created") val created: Long,
    @SerializedName("expires") val expires: Long,
    @SerializedName("livemode") val livemode: Boolean,
    @SerializedName("secret") val secret: String
)

data class AssociatedObject(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String
)