package com.example.spotixe.services

import Components.HomePage
import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.spotixe.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.URL
import java.util.concurrent.Executors

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message received: $remoteMessage")

        // Chạy trên background thread (network, decode ảnh...)
        Executors.newSingleThreadExecutor().execute {
            try {
                Log.d("FCM", "Data Payload: ${remoteMessage.data}")

                // Lấy dữ liệu từ data message
                val data = remoteMessage.data
                val title = data["title"]
                    ?: remoteMessage.notification?.title
                    ?: "SpotiXe"

                val body = data["body"]
                    ?: remoteMessage.notification?.body
                    ?: "Bạn có một thông báo mới"

                val imageUrl = data["imageUrl"] // <-- String? (nullable)

                Log.d("FCM", "Parsed Title: $title")
                Log.d("FCM", "Parsed Body: $body")
                Log.d("FCM", "Parsed Image URL: $imageUrl")

                // Gửi vào notification, imageUrl cho phép null
                showNotification(title, body, imageUrl)
            } catch (e: Exception) {
                Log.e("FCM", "Error processing notification: ${e.message}", e)
            }
        }
    }

    private fun showNotification(
        title: String,
        message: String,
        imageUrl: String?
    ) {
        val context = applicationContext

        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            action = "ACTION_FCM_OPEN_NOTIFICATION"
            putExtra("screen", "NotificationScreen")
            putExtra("messageId", message)
            putExtra("title", title)
            putExtra("body", message)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "spotixe_general"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Thông báo chung",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT
                )
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.example.spotixe.R.drawable.spotixe_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(0xFF58BA47.toInt())
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setContentIntent(pendingIntent)

        // BigTextStyle
        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(message)
        )

        // Nếu có ảnh thì chuyển sang BigPictureStyle

        if (!imageUrl.isNullOrBlank()) {
            val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null as Icon?)

            )
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

}
