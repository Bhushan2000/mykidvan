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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.setValue
import com.example.mykidsvan.android.data.dto.response.Driver
import com.example.mykidsvan.android.data.dto.response.Parent
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(viewModel: AuthViewModel, userId: String, userType: String) {
    val profileData by viewModel.profileData.collectAsState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(profileData) {
        delay(300)
        visible = true
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile(userId, userType)
    }

    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            profileData?.let { data ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        val profileImage = "https://mykidvan.com/${(data as? Driver)?.profile_picture}"
                        ProfileImage(imageUrl = profileImage)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (data) {
                                is Parent -> data.parentName?.capitalize() ?: "Unknown"
                                is Driver -> data.driver_name?.capitalize()?: "Unknown"
                                else -> "Unknown"
                            },
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "${userType.capitalize()} Profile",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        when (data) {
                            is Parent -> {
                                ProfileDetailRow("Contact Number", data.contactNumber)
                                ProfileDetailRow("Parent Address", data.parentAddress?.capitalize())
                                ProfileDetailRow("Child Name", data.childName?.capitalize())
                                ProfileDetailRow("School Name", data.childSchoolName?.capitalize())
                                ProfileDetailRow("School ID", data.schoolId)
                            }

                            is Driver -> {
                                ProfileDetailRow("Mobile Number", data.number)
                                ProfileDetailRow("Vehicle Number", data.vehicle_number?.capitalize())
                                ProfileDetailRow("State", data.state?.capitalize())
                                ProfileDetailRow("District", data.district?.capitalize())
                                ProfileDetailRow("City", data.city?.capitalize())
                            }
                        }
                    }

                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Data Available")
            }
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?) {
    if (imageUrl != null && imageUrl.startsWith("data:image/png;base64,")) {
        val imageBytes = Base64.decode(imageUrl.substringAfter("base64,"), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    } else {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$label:",
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = value ?: "N/A", fontSize = 16.sp)
        }
    }
}
