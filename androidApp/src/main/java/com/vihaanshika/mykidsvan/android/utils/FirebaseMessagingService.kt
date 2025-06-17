package com.vihaanshika.mykidsvan.android.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
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
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseMessagingService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        // Send token to your server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived: $remoteMessage")
        val title = remoteMessage.notification?.title ?: "New Message"
        val body = remoteMessage.notification?.body ?: "You've received a new message"

        val senderName = remoteMessage.data["name"] ?: title
        val profileImageUrl = remoteMessage.data["profile_picture"]

        Log.d(TAG, "onMessageReceived: name --> $senderName  profileUrl---> $profileImageUrl")

        serviceScope.launch {
//            showNotificationWithImage(
//                this@FirebaseMessagingService,
//                senderName,
//                body,
//                profileImageUrl
//            )
            showNotification(senderName,body,profileImageUrl)
        }
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
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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

        val remoteInput = RemoteInput.Builder("key_text_reply").setLabel("Reply").build()

        val replyIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ACTION_REPLY"
            putExtra("message_id", notificationId)
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            this, notificationId + 1, replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.ic_reply, "Reply", replyPendingIntent
        ).addRemoteInput(remoteInput).setAllowGeneratedReplies(true).build()

        CoroutineScope(Dispatchers.IO).launch {
            var bitmap: Bitmap? = null

            try {
                profileUrl?.let {
                    val input = URL(it).openStream()
                    bitmap = BitmapFactory.decodeStream(input)
                }
            } catch (e: Exception) {
                Log.e("Notification", "Failed to load image", e)
            }

            val style = NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null as Bitmap?)
                // Hide large icon in expanded view
                .setSummaryText(message)

            val builder = NotificationCompat.Builder(this@FirebaseMessagingService, channelId)
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.drawable.menu)
                .setStyle(style)
                .setLargeIcon(bitmap)
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

            manager.notify(notificationId, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Clean up when service is destroyed
    }
}

