package com.example.clothingstoreapp.Model

import java.io.Serializable


class AddressModel : Serializable {

    private var _id: String? = null
    var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    private var _fullName: String? = null
    var fullName: String?
        get() = _fullName
        set(value) {
            _fullName = value
        }

    private var _numberPhone: String? = null
    var numberPhone: String?
        get() = _numberPhone
        set(value) {
            _numberPhone = value
        }

    private var _province: Province? = null
    var province: Province?
        get() = _province
        set(value) {
            _province = value
        }

    private var _district: District? = null
    var district: District?
        get() = _district
        set(value) {
            _district = value
        }

    private var _ward: Ward? = null
    var ward: Ward?
        get() = _ward
        set(value) {
            _ward = value
        }

    private var _addressDetails: String? = null
    var addressDetails: String?
        get() = _addressDetails
        set(value) {
            _addressDetails = value
        }

    private var _typeAddress: String? = null
    var typeAddress: String?
        get() = _typeAddress
        set(value) {
            _typeAddress = value
        }

    private var _isDefault: Boolean = false
    var isDefault: Boolean
        get() = _isDefault
        set(value) {
            _isDefault = value
        }

    constructor()

    constructor(
        fullName: String?,
        numberPhone: String?,
        tinhThanhPho: Province?,
        quanHuyen: District?,
        phuongXa: Ward?,
        addressDetails: String?,
        type:String
    ) {
        this._fullName = fullName
        this._numberPhone = numberPhone
        this._province = tinhThanhPho
        this._district = quanHuyen
        this._ward = phuongXa
        this._addressDetails = addressDetails
        this._typeAddress = type
    }

    override fun toString(): String {
        return "AddressModel(_id=$_id, _fullName=$_fullName, _numberPhone=$_numberPhone, _province=$_province, _district=$_district, _ward=$_ward, _addressDetails=$_addressDetails, _typeAddress=$_typeAddress, _isDefault=$_isDefault)"
    }


}