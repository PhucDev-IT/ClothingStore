package vn.clothing.store.networks.request

import com.google.gson.annotations.SerializedName

data class RegisterRequestModel(var email: String, var password: String,@SerializedName("full_name") var fullName: String)