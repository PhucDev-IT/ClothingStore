package com.example.clothingstoreapp.Model

import java.io.Serializable

class Customer:Serializable {
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

    private var _email: String? = null
    var email: String?
        get() = _email
        set(value) {
            _email = value
        }

    private var _listAddress: List<AddressModel>? = null
    var listAddress: List<AddressModel>?
        get() = _listAddress
        set(value) {
            _listAddress = value
        }

    private var _defaultAddress: AddressModel? = null
    var defaultAddress: AddressModel?
        get() = _defaultAddress
        set(value) {
            _defaultAddress = value
        }

    override fun toString(): String {
        return "Customer(_id=$_id, _fullName=$_fullName, _email=$_email, _listAddress=$_listAddress, _defaultAddress=$_defaultAddress)"
    }


}