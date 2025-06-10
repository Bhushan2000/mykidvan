package com.vihaanshika.mykidsvan.android.utils

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messageId = intent.getIntExtra("message_id", -1)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        when (intent.action) {
            "ACTION_LIKE" -> {
                Toast.makeText(context, "Message $messageId liked!", Toast.LENGTH_SHORT).show()
                // Update backend or database here
                notificationManager.cancel(messageId) // ✅ Dismiss notification
            }

            "ACTION_REPLY" -> {
                val replyText = RemoteInput.getResultsFromIntent(intent)
                    ?.getCharSequence("key_text_reply")?.toString()
                Toast.makeText(context, "Replied: $replyText", Toast.LENGTH_SHORT).show()
                // Send reply to server here
                notificationManager.cancel(messageId) // ✅ Dismiss notification
            }
        }
    }
}
