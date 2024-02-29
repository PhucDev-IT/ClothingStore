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

    private var _avatar: String? = null
    var avatar: String?
        get() = _avatar
        set(value) {
            _avatar = value
        }

    private var _tokenFCM: String? = null
    var tokenFCM: String?
        get() = _tokenFCM
        set(value) {
            _tokenFCM = value
        }


    override fun toString(): String {
        return "Customer(_id=$_id, _fullName=$_fullName, _email=$_email, _listAddress=$_listAddress"
    }


}