package vn.clothing.store.networks.response

import com.google.gson.annotations.SerializedName

data class PaymentIntentResponseModel (
    val id:String,
    @SerializedName("client_secret")
    val clientSecret:String
)