package vn.clothing.store

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate


class MyApplication : Application() {
    val context: Context get() = instance.applicationContext

    init { instance = this }
    companion object{
        const val CHANNEL_ID = "CHANNEL_1"
        @SuppressLint("StaticFieldLeak")
        @get:Synchronized
        var instance:MyApplication = MyApplication()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }


    private fun createNotificationChannel(){
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.sound_notify)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()


            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
                setSound(soundUri,audioAttributes)
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}