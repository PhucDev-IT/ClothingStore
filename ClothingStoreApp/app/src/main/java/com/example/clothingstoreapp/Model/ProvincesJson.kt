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
 }