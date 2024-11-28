package vn.mobile.clothing.utils

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging

import vn.mobile.clothing.MyApplication
import vn.mobile.clothing.R
import kotlin.random.Random

object Utils {

    const val TAG = "Utils"

    @SuppressLint("DefaultLocale")
    fun generateOTP():String{
        val otp = Random.nextInt(0,1000000)
        return String.format("%06d", otp)
    }

    fun sendNotification(context: Context, title:String, text:String){
        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(bitmap)
            .setColor(context.resources.getColor(R.color.colorPrimary))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }


    //Lấy FCM Token của ứng dụng
    fun getTokenFCM(callback:(String)->Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.w(TAG,"My Token: $token")
                callback(token)
            }
        }
    }
}