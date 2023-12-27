package com.example.clothingstoreapp.Model

import java.io.Serializable


class AddressModel : Serializable {

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

    private var _tinhThanhPho: String? = null
    var tinhThanhPho: String?
        get() = _tinhThanhPho
        set(value) {
            _tinhThanhPho = value
        }

    private var _quanHuyen: String? = null
    var quanHuyen: String?
        get() = _quanHuyen
        set(value) {
            _quanHuyen = value
        }

    private var _phuongXa: String? = null
    var phuongXa: String?
        get() = _phuongXa
        set(value) {
            _phuongXa = value
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

    constructor()

    constructor(
        fullName: String?,
        numberPhone: String?,
        tinhThanhPho: String?,
        quanHuyen: String?,
        phuongXa: String?,
        addressDetails: String?,
        type:String
    ) {
        this._fullName = fullName
        this._numberPhone = numberPhone
        this._tinhThanhPho = tinhThanhPho
        this._quanHuyen = quanHuyen
        this._phuongXa = phuongXa
        this._addressDetails = addressDetails
        this._typeAddress = type
    }

    override fun toString(): String {
        return "Address(_fullName=$_fullName, _numberPhone=$_numberPhone, _tinhThanhPho=$_tinhThanhPho, _quanHuyen=$_quanHuyen, _phuongXa=$_phuongXa, _addressDetails=$_addressDetails)"
    }


}