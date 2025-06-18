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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vihaanshika.mykidsvan.android.MainActivity
import com.vihaanshika.mykidsvan.android.R
import java.net.URL
import kotlin.random.Random
import androidx.core.app.RemoteInput  // ✅ CORRECT
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation
import com.vihaanshika.mykidsvan.android.data.AuthRepository
import com.vihaanshika.mykidsvan.android.data.dto.response.UpdateTokenResponse
import com.vihaanshika.mykidsvan.android.ui.tracking.LatLngRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.inject

class FirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseMessagingService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val userMessageCountMap = mutableMapOf<String, Int>()
    private val repository: AuthRepository by inject()
    private val userPreferences: UserPreferences by inject()
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        // Send token to your server if needed
        try {
            serviceScope.launch {
                combine(userPreferences.userRole, userPreferences.userIdFlow) { role, id ->
                    role to id // or return a custom data class if you want
                }.collect { (role, id) ->
                    Log.d("CombinedFlow", "Role: $role, ID: $id")
                    // Do whatever you want with role and id
                    val response = repository.updateFCMToken(id?.toInt(), role.toString(), token)
                    if (response.status == true) {
                        Log.d(TAG, "onNewToken if block : ${response.message}")
                    } else {
                        Log.d(TAG, "onNewToken else block : ${response.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onNewToken: Fail to update token", e)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title ?: "New Message"
        val body = remoteMessage.notification?.body ?: "You've received a new message"
        val type = remoteMessage.data["type"] ?: "unknown"
        when (type) {
            "chat" -> {
                val senderName = remoteMessage.data["name"] ?: title
                // val message = remoteMessage.data["message"] ?: "You've got a new message"
                val profileUrl = remoteMessage.data["profile_picture"]
                Log.d(TAG, "message received :name --> $title profileUrl---> $profileUrl")

                serviceScope.launch {
                    showNotification(senderName, body, profileUrl)
                }
            }

            "payment" -> {
                // Optionally trigger a simple notification (without actions)
                showSimpleNotification(title, body, "payment_channel")
            }

            "tracking" -> {
                showSimpleNotification(title, body, "tracking_channel")
            }

            else -> {
                Log.w(TAG, "Unknown notification type: $type")
            }
        }
    }

    private fun showSimpleNotification(title: String, message: String, channelId: String) {

        val notificationId = Random.nextInt()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.menu)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelId.replace("_", " ").capitalize(),
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, builder.build())
    }

    private fun showNotification(name: String, message: String, profileUrl: String?) {
        val channelId = Constants.MESSAGE_CHANNEL
        val notificationId = Random.nextInt()
        val groupKey = "group_chat_messages_$name" // 👈 Unique group for each sender

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("openMessageScreen", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
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
            val imageLoader = ImageLoader(this@FirebaseMessagingService)
            var bitmap: Bitmap? = null
            try {
                val request = ImageRequest.Builder(this@FirebaseMessagingService)
                    .data(profileUrl)
                    .placeholder(R.drawable.menu)
                    .error(R.drawable.menu)
                    .transformations(CircleCropTransformation())
                    .allowHardware(false)
                    .build()

                val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
                if (result is BitmapDrawable) {
                    bitmap = result.bitmap
                }
            } catch (e: Exception) {
                Log.e("Notification", "Image load failed", e)
            }

            val builder = NotificationCompat.Builder(this@FirebaseMessagingService, channelId)
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.drawable.menu)
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

            // ⬇️ Send the individual message notification
            manager.notify(notificationId, builder.build())

            // ⬇️ Maintain a group summary for this user
            val existingCount = userMessageCountMap.getOrDefault(name, 0) + 1
            userMessageCountMap[name] = existingCount

            if (existingCount > 1) {
                val summaryText = "$existingCount messages from $name"

                val summaryBuilder =
                    NotificationCompat.Builder(this@FirebaseMessagingService, channelId)
                        .setContentTitle(name)
                        .setContentText(summaryText)
                        .setSmallIcon(R.drawable.menu)
                        .setLargeIcon(bitmap)
                        .setStyle(
                            NotificationCompat.InboxStyle().addLine(message)
                                .setSummaryText(summaryText)
                        )
                        .setGroup(groupKey)
                        .setGroupSummary(true)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)

                manager.notify(name.hashCode(), summaryBuilder.build()) // Unique ID per user
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Clean up when service is destroyed
    }
}

