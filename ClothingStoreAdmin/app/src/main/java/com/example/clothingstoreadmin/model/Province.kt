package com.example.clothingstoreadmin.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Province: Serializable {
    @SerializedName("ProvinceName")
    private var _provinceName: String?=null
    var ProvinceName:String?
        get() = _provinceName
        set(value) {
            _provinceName = value
        }

    @SerializedName("ProvinceID")
    private var _provinceID: Int?=null
    var ProvinceID:Int?
        get() = _provinceID
        set(value) {
            _provinceID = value
        }
}