package com.example.clothingstoreapp.Model

import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.Date

class MessageModel :Serializable {


    private var _createdAt: Timestamp? = null
    var createdAt: Timestamp?
        get() = _createdAt
        set(value) {
            _createdAt = value
        }

    private var _message: String? = null
    var message: String?
        get() = _message
        set(value) {
            _message = value
        }

    private var _senderId: String? = null
    var senderId: String?
        get() = _senderId
        set(value) {
            _senderId = value
        }

    constructor()

    constructor( createAt: Timestamp?, message: String?, senderId: String?) {
        this._createdAt = createAt
        this._message = message
        this._senderId = senderId
    }
}