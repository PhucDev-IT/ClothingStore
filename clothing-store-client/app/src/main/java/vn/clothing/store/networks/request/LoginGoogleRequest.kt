package vn.clothing.store.networks.request

import com.google.gson.annotations.SerializedName

data class LoginGoogleRequest(
    var email: String,
    var password: String?,
    @SerializedName("fcm_token")
    val fcmToken: String,
    @SerializedName("device_id")
    var deviceId: String,
    @SerializedName("full_name")
    var fullName:String?,
    @SerializedName("last_name")
    var lastName:String?,
    @SerializedName("first_name")
    var firstName:String?,
    @SerializedName("number_phone")
    var numberPhone:String?,
    var avatar:String?
)