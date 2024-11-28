package vn.mobile.clothing.network.request

import com.google.gson.annotations.SerializedName

data class RegisterRequestModel(var email: String, var password: String,@SerializedName("full_name") var fullName: String)