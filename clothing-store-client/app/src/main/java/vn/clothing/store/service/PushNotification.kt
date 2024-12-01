package vn.clothing.store.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import vn.clothing.store.MyApplication
import vn.clothing.store.R
import vn.clothing.store.activities.NotificationActivity
import vn.clothing.store.models.NotificationModel

object PushNotification {

     fun sendNotification(context: Context,notification: NotificationModel) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


         val intent = Intent(context, NotificationActivity::class.java).apply {
             flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
         }

         // PendingIntent để xử lý sự kiện nhấn
         val pendingIntent = PendingIntent.getActivity(
             context,
             0,
             intent,
             PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
         )

        val notificationBuilder = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Thay bằng icon của bạn
            .setContentIntent(pendingIntent) // Gắn PendingIntent
            .setAutoCancel(true) // Tự động xóa thông báo khi nhấn
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder)
    }
}