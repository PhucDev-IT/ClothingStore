package vn.mobile.clothing.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ErrorResponseModel {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    var details:List<ValidationField> ? = null
}

class ValidationField{
    @SerializedName("field_name")
    var field:String?=null

    @SerializedName("error_message")
    var errorMessage:String?=null
}