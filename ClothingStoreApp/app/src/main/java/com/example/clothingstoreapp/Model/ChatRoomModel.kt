package com.example.clothingstoreapp.Model

import com.google.firebase.Timestamp
import java.io.Serializable

class ChatRoomModel : Serializable {
    private var _idRoom: String? = null
    var idRoom: String?
        get() = _idRoom
        set(value) {
            _idRoom = value
        }

    private var _lastMessageTimestamp: Timestamp? = null
    var lastMessageTimestamp: Timestamp?
        get() = _lastMessageTimestamp
        set(value) {
            _lastMessageTimestamp = value
        }

    private var _lastMessage: String? = null
    var lastMessage: String?
        get() = _lastMessage
        set(value) {
            _lastMessage = value
        }

    private var _lastMessageSenderId: String? = null
    var lastMessageSenderId: String?
        get() = _lastMessageSenderId
        set(value) {
            _lastMessageSenderId = value
        }


    private var _senderBy: CustomSender? = null
    var senderBy: CustomSender?
        get() = _senderBy
        set(value) {
            _senderBy = value
        }
}


data class CustomSender(
    val id: String?,
    val fullName: String?,
    val avatar: String?,
    val tokenFCM: String?
)