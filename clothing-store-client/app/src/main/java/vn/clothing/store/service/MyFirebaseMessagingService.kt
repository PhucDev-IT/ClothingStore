package vn.clothing.store.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import vn.clothing.store.MyApplication
import vn.clothing.store.R
import vn.clothing.store.models.NotificationModel

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notification: RemoteMessage.Notification = remoteMessage.notification ?: return
        val strTitle:String = notification.title?:""
        val strMessage = notification.body?:""

//        val valueMap : Map<String,String> =  message.data
//        val strTitle: String? = valueMap["title"]
//        val strMessage: String? = valueMap["message"]

        val notificationModel = NotificationModel(System.currentTimeMillis().toString(),strTitle,strMessage,null,null,null,null,null)

        Log.w(TAG,"valueMap = $notification")
        PushNotification.sendNotification(this,notificationModel)
    }


    private fun sendTokenToServer(token: String) {
        // Gửi token tới server qua API
    }


    companion object{
        private val TAG = MyFirebaseMessagingService::class.java.name
    }
}
