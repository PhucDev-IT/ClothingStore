package com.example.clothingstoreapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ShippingMethodJson{

    var data:List<ShippingMethod>?=null
}

class ShippingMethod{

    @SerializedName("service_id")
    private var _serviceId: Int?=null
    var serviceId: Int?
        get() = _serviceId
        set(value) {
            _serviceId = value
        }
    @SerializedName("short_name")
    private var _shortName: String?=null
    var shortName: String?
        get() = _shortName
        set(value) {
            _shortName = value
        }

    @SerializedName("service_type_id")
    private var _serviceTypeId: Int?=null
    var serviceTypeId: Int?
        get() = _serviceTypeId
        set(value) {
            _serviceTypeId = value
        }


}
