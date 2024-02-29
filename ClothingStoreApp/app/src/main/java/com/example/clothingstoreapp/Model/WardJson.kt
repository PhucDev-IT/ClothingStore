package com.example.clothingstoreapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//API: https://online-gateway.ghn.vn/shiip/public-api/master-data/ward
data class WardJson(
    val data: List<Ward>,
)

 class Ward: Serializable{
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

     @SerializedName("DistrictID")
    private var _districtID: Int?=null
     var DistrictID: Int?
         get() = _districtID
         set(value) {
             _districtID = value
         }

     @SerializedName("NameExtension")
    private var _nameExtension: List<String>?=null
     var NameExtension: List<String>?
         get() = _nameExtension
         set(value) {
             _nameExtension = value
         }
}