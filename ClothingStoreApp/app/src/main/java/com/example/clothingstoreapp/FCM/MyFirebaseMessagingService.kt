
package com.example.clothingstoreapp.FCM



import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Model.PushNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//        val notification: RemoteMessage.Notification = message.notification ?: return
//
//        val strTitle:String = notification.title?:""
//        val strMessenge = notification.body?:""

        val valueMap : Map<String,String> =  message.data
        val strTitle: String = valueMap["title"]!!
        val strMessage: String = valueMap["message"]!!

        PushNotification().sendNotification(this,strTitle,strMessage)


    }

        override fun onNewToken(token: String) {
            super.onNewToken(token)
          Log.w(TAG,"new  Token:  $token")
        }


}