package com.vihaanshika.mykidsvan.android.utils

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
import com.vihaanshika.mykidsvan.android.MainActivity
import com.vihaanshika.mykidsvan.android.R
import java.net.URL
import kotlin.random.Random
import androidx.core.app.RemoteInput  // ✅ CORRECT

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        // Send token to your server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "New Message"
        val body = remoteMessage.notification?.body ?: "You've received a new message"

        val senderName = remoteMessage.data["name"] ?: title
        val profileImageUrl = remoteMessage.data["profileUrl"]

        showNotification(senderName, body, profileImageUrl)
    }

    private fun showNotification(name: String, message: String, profileUrl: String?) {
        val channelId = "message_channel"
        val notificationId = Random.nextInt()
        val groupKey = "group_chat_messages"

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("openMessageScreen", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Large icon from profile URL
        val largeIcon = profileUrl?.let {
            try {
                val input = URL(it).openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                null
            }
        }

        // --- LIKE ACTION ---
        val likeIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ACTION_LIKE"
            putExtra("message_id", notificationId)
        }
        val likePendingIntent = PendingIntent.getBroadcast(
            this, notificationId, likeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val likeAction = NotificationCompat.Action.Builder(
            R.drawable.ic_like, "Like", likePendingIntent
        ).build()

        // --- REPLY ACTION ---
        val remoteInput = RemoteInput.Builder("key_text_reply")
            .setLabel("Reply")
            .build()

        val replyIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ACTION_REPLY"
            putExtra("message_id", notificationId)
        }
        val replyPendingIntent = PendingIntent.getBroadcast(
            this, notificationId + 1, replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.ic_reply, "Reply", replyPendingIntent
        ).addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(name)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSmallIcon(R.drawable.menu)
            .setLargeIcon(largeIcon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(groupKey)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(likeAction)
            .addAction(replyAction)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Messages", NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, notificationBuilder.build())
     }

}

