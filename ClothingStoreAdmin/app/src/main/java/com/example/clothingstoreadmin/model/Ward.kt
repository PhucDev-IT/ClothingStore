package com.example.clothingstoreadmin.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Ward: Serializable {
    @SerializedName("WardName")
    private var _wardName: String?=null
    var WardName: String?
        get() = _wardName
        set(value) {
            _wardName = value
        }

    @SerializedName("WardCode")
    private var _wardCode: String?=null
    var WardCode: String?
        get() = _wardCode
        set(value) {
            _wardCode = value
        }

}