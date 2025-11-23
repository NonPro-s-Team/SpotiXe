package com.example.spotixe.services

import Components.HomePage
import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.URL
import java.util.concurrent.Executors

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New token: $token")
        // chỗ này em có thể gửi token lên server luôn nếu muốn
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
        imageUrl: String? // <-- cho phép null
    ) {
        val context = applicationContext

        val intent = Intent(context, HomePage::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("screen", title)
            putExtra("messageId", message)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo channel cho Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel",
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "default_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Nếu có imageUrl thì mới cố load ảnh
        if (!imageUrl.isNullOrBlank()) {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
                builder.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                )
            } catch (e: Exception) {
                Log.e("FCM", "Error loading image: ${e.message}", e)
            }
        }

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            builder.build()
        )
    }
}
