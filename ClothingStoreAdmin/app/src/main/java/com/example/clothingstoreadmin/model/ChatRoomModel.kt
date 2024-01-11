package com.example.clothingstoreadmin.model

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


 class CustomSender : Serializable{
     var id: String?=null
     var fullName: String?=null
     var avatar: String?=null
     var tokenFCM: String?=null

     constructor()

     constructor(id:String?,name:String?,avt:String?,token:String?){
         this.id = id
         this.fullName = name
         this.avatar = avt
         this.tokenFCM = token
     }
 }