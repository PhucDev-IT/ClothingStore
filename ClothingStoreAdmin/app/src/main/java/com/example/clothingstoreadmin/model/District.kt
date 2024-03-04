package com.example.clothingstoreadmin.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class District: Serializable {


    @SerializedName("DistrictID")
    private var _districtID: Int?=null
    var districtID: Int?
        get() = _districtID
        set(value) {
            _districtID = value
        }


    @SerializedName("DistrictName")
    private var _districtName: String?=null
    var districtName: String?
        get() = _districtName
        set(value) {
            _districtName = value
        }
    @SerializedName("Code")
    private var _code: String?=null
    var code: String?
        get() = _code
        set(value) {
            _code = value
        }

}