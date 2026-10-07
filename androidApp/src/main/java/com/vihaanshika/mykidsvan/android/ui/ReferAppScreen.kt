package com.vihaanshika.mykidsvan.android.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.utils.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferAppScreen(viewModel: AuthViewModel, userRole: String, userId: String) {
    val context = LocalContext.current
    val referCode by viewModel.referCode.collectAsState()
    val referralCode = referCode.toString()
    // State to manage loading
    val isLoading = remember { mutableStateOf(true) }
    // Fetch commission based on user role once
    LaunchedEffect(Unit) {
        val driverId = if (viewModel.userRole.value == Constants.USER_PARENT) {
            viewModel.assignedVehicleId.value.toString()
        } else {
            viewModel.userId.value.toString()
        }
        viewModel.getCommission(driverId, userRole = userRole)
    }

    val webUrl = if (userRole == Constants.USER_PARENT) {
        Constants.REFERRAL_PARENT
    } else {
        Constants.REFERRAL_DRIVER
    }
    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()   // adds padding for status bar
                .padding(
                    top = innerPadding.calculateTopPadding() + 48.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            // 🔥 Changed from hardcoded 80.dp to top=72.dp so it matches your TopAppBar height
        ) {
            var scale by remember { mutableStateOf(1f) }
            LaunchedEffect(Unit) {
                scale = 1.1f
                delay(500L)
                scale = 1f
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Your Referral Code",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1565C0)
                        )
                        Text(
                            text = referralCode,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 2.dp),
                            color = Color(0xFF1565C0)
                        )
                    }

                    IconButton(
                        onClick = {
                            val message = if (userRole == Constants.USER_DRIVER)
                                Constants.REFERRAL_DRIVER_CONTENT
                            else
                                Constants.REFERRAL_PARENT_CONTENT

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, message)
                                `package` = "com.whatsapp"
                            }
                            try {
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "WhatsApp not installed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    "WhatsApp not installed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF2E7D32)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // WebView below, full height and scrollable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // takes remaining vertical space
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.setSupportZoom(true)
                            isVerticalScrollBarEnabled = true

                            // Dark mode (if applicable)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                settings.forceDark = WebSettings.FORCE_DARK_ON
                            }

                            setOnTouchListener { view, event ->
                                view.parent?.requestDisallowInterceptTouchEvent(true)
                                false
                            }

                            // Handle loading state
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(
                                    view: WebView?,
                                    url: String?,
                                    favicon: android.graphics.Bitmap?
                                ) {
                                    isLoading.value = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading.value = false
                                }
                            }

                            loadUrl(webUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Loading indicator
                if (isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}
