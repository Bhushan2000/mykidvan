package com.example.mykidsvan.android.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.R
import kotlinx.coroutines.CoroutineStart
import android.util.Base64
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.setValue
import com.example.mykidsvan.android.data.dto.response.Driver
import com.example.mykidsvan.android.data.dto.response.Parent
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(viewModel: AuthViewModel, userId: String, userType: String) {
    val profileData by viewModel.profileData.collectAsState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile(userId, userType)
    }

    LaunchedEffect(profileData) {
        delay(300)
        visible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            profileData?.let { data ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(700)) + scaleIn(initialScale = 0.8f),
                            exit = fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val profileImage =
                                    "https://mykidvan.com/${(data as? Driver)?.profile_picture}"
                                ProfileImage(imageUrl = profileImage)

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = when (data) {
                                        is Parent -> data.parentName?.capitalize() ?: "Unknown"
                                        is Driver -> data.driver_name?.capitalize() ?: "Unknown"
                                        else -> "Unknown"
                                    },
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${userType.capitalize()} Profile",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(1000)),
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    when (data) {
                                        is Parent -> {
                                            ProfileDetailRow("Contact Number", data.contactNumber)
                                            ProfileDetailRow(
                                                "Parent Address",
                                                data.parentAddress?.capitalize()
                                            )
                                            ProfileDetailRow(
                                                "Child Name",
                                                data.childName?.capitalize()
                                            )
                                            ProfileDetailRow(
                                                "School Name",
                                                data.childSchoolName?.capitalize()
                                            )
                                            ProfileDetailRow("School ID", data.schoolId)
                                        }


                                        is Driver -> {
                                            ProfileDetailRow("Mobile Number", data.number)
                                            ProfileDetailRow(
                                                "Vehicle Number",
                                                data.vehicle_number?.capitalize()
                                            )
                                            ProfileDetailRow("State", data.state?.capitalize())
                                            ProfileDetailRow(
                                                "District",
                                                data.district?.capitalize()
                                            )
                                            ProfileDetailRow("City", data.city?.capitalize())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: Text(
                text = "No Data Available",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.error
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?) {
    val transition = rememberInfiniteTransition(label = "profile_image")
    val scale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "image_scale"
    )

    if (imageUrl != null && imageUrl.startsWith("data:image/png;base64,")) {
        val imageBytes = Base64.decode(imageUrl.substringAfter("base64,"), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(140.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    } else {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(140.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

@Composable
fun ProfileDetailRow(title: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = value ?: "N/A",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
