package com.example.clothingstoreapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//api: https://online-gateway.ghn.vn/shiip/public-api/master-data/province
class ProvincesJson() : Serializable {
   val code:Int? = null
    val message:String? = null
    val data:List<Province>?=null
}
 class Province:Serializable{
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

     @SerializedName("CountryID")
     private var _countryID: Int?=null
     var CountryID:Int?
         get() = _countryID
         set(value) {
             _countryID = value
         }

     @SerializedName("Code")
     private var _code: String?=null
     var Code:String?
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

     @SerializedName("districts")
     private var _districts: List<DistrictsJson>?=null
     var districts:List<DistrictsJson>?
         get() = _districts
         set(value) {
             _districts = value
         }

     override fun toString(): String {
         return "Province(_provinceName=$_provinceName, _provinceID=$_provinceID, _countryID=$_countryID, _code=$_code, _nameExtension=$_nameExtension, _districts=$_districts)"
     }

 }