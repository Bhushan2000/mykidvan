package com.vihaanshika.mykidsvan.android.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SubscriptionDialog(
    onFreeTrialClick: () -> Unit,
    onPayNowClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Choose a Subscription Option",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                "Start your journey with a Free Trial or upgrade instantly by choosing Pay Now."
            )
        },
        confirmButton = {
            TextButton(onClick = { onFreeTrialClick() }) {
                Text("Free Trial")
            }
        },
        dismissButton = {
            TextButton(onClick = { onPayNowClick() }) {
                Text("Pay Now")
            }
        }
    )
}
