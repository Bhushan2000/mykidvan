package com.vihaanshika.mykidsvan.android.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.vihaanshika.mykidsvan.R
import com.vihaanshika.mykidsvan.android.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportHelpScreen() {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val openDialog = remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()   // ✅ adds padding for status bar
                .padding(
                    top = innerPadding.calculateTopPadding() + 48.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Logo + Text
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(com.vihaanshika.mykidsvan.android.R.drawable.mkv),
                            contentDescription = "Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = " MyKidVan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Website
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(Constants.SUPPORT_WEBSITE))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(com.vihaanshika.mykidsvan.android.R.drawable.website),
                        contentDescription = "Website",
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Constants.SUPPORT_WEBSITE_PLACEHOLDER,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = colors.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Phone
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        openDialog.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Constants.SUPPORT_PHONE_PLACEHOLDER,
                        style = MaterialTheme.typography.bodyLarge.copy(color = colors.primary)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Email
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${Constants.SUPPORT_EMAIL}")
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Constants.SUPPORT_EMAIL_PLACEHOLDER,
                        style = MaterialTheme.typography.bodyLarge.copy(color = colors.primary)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // YouTube
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(Constants.SUPPORT_YOUTUBE))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(com.vihaanshika.mykidsvan.android.R.drawable.youtube),
                        contentDescription = "Website",
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Constants.SUPPORT_YOUTUBE_PLACEHOLDER,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = colors.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Instagram
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(Constants.SUPPORT_INSTAGRAM))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(com.vihaanshika.mykidsvan.android.R.drawable.instagram),
                        contentDescription = "Website",
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Constants.SUPPORT_INSTAGRAM_PLACEHOLDER,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = colors.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Facebook
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(Constants.SUPPORT_FACEBOOK))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(com.vihaanshika.mykidsvan.android.R.drawable.facebook),
                        contentDescription = "Website",
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Constants.SUPPORT_FACEBOOK_PLACEHOLDER,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = colors.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // FAQ Driver
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FAQ_DRIVER))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(com.vihaanshika.mykidsvan.android.R.drawable.faq),
                        contentDescription = "Website",
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Constants.FAQ_DRIVER_PLACEHOLDER,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = colors.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // FAQ Parent
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FAQ_PARENT))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(com.vihaanshika.mykidsvan.android.R.drawable.conversation),
                        contentDescription = "Website",
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Constants.FAQ_PARENT_PLACEHOLDER,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = colors.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
        }
    }

    // 📞 Dialog to choose Call or WhatsApp
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text("Contact Support") },
            text = { Text("How would you like to contact us?") },
            confirmButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse("tel:${Constants.SUPPORT_PHONE_NO}")
                    context.startActivity(callIntent)
                }) {
                    Text("Call")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(
                            "https://wa.me/${
                                Constants.SUPPORT_PHONE_NO.replace("+", "").replace(" ", "")
                            }"
                        )
                    }
                    context.startActivity(whatsappIntent)
                }) {
                    Text("WhatsApp")
                }
            }
        )
    }
}

@Composable
fun ExpandableSupportCard(
    title: String,
    description: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colors.surface), // Surface color for dark mode
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface // Text color adapts to the theme
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.onSurface // Icon color adapts to the theme
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    color = colors.onSurface // Text color adapts to the theme
                )
            }
        }
    }
}

@Composable
fun SupportCategoryCard(title: String, description: String) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.surface), // Surface color for dark mode
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface // Text color adapts to the theme
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface.copy(alpha = 0.7f) // Subtle text color for description
            )
        }
    }
}


