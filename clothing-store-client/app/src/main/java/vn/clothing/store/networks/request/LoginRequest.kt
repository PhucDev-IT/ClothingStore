package vn.clothing.store.networks.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    var email: String,
    var password: String,
    @SerializedName("fcm_token")
    val fcmToken: String,
    @SerializedName("device_id")
    var deviceId: String
)