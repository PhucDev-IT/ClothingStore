package com.example.clothingstoreadmin.model

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

    private var _role:Int = 0
    var role: Int
        get() = _role
        set(value) {
            _role = value
        }

    constructor()
    constructor(id:String?,name:String?,avt:String?,token:String?){
        this.id = id
        this.fullName = name
        this.avatar = avt
        this.tokenFCM = token
    }

    override fun toString(): String {
        return "Customer(_id=$_id, _fullName=$_fullName, _email=$_email, _listAddress=$_listAddress, _defaultAddress=$_defaultAddress)"
    }


}