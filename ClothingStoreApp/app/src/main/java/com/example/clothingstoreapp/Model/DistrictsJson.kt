package com.example.clothingstoreapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//API: https://online-gateway.ghn.vn/shiip/public-api/master-data/district?province_id=***
class DistrictsJson: Serializable {
    val data:List<District>?=null
}

class District: Serializable{
    @SerializedName("ProvinceID")
    private var _provinceID: Int?=null
    var provinceID: Int?
        get() = _provinceID
        set(value) {
            _provinceID = value
        }

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
    @SerializedName("NameExtension")
    private var _nameExtension: List<String>?=null
    var NameExtension:List<String>?
        get() = _nameExtension
        set(value) {
            _nameExtension = value
        }

    private var _wards: List<WardJson>?=null
    var wards:List<WardJson>?
        get() = _wards
        set(value) {
            _wards = value
        }
}
