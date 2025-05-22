package com.example.mykidsvan.android.screens

import android.graphics.BitmapFactory
import android.net.Uri
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
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.mykidsvan.android.data.dto.response.Driver
import com.example.mykidsvan.android.data.dto.response.Parent
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(viewModel: AuthViewModel, userId: String, userType: String) {
    val context = LocalContext.current
    val profileData by viewModel.profileData.collectAsState()
    var visible by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // Editable fields
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var childName by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }

    var mobileNumber by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var taluka by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    val isUpdating by viewModel.isProfileUpdating.collectAsState()
    val updateMessage by viewModel.updateMessage.collectAsState()

    // Gallery launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile(userId, userType)
    }

    LaunchedEffect(profileData) {
        delay(300)
        visible = true

        profileData?.let { data ->
            when (data) {
                is Parent -> {
                    name = data.parentName.orEmpty()
                    contact = data.contactNumber.orEmpty()
                    address = data.parentAddress.orEmpty()
                    childName = data.childName.orEmpty()
                    schoolName = data.childSchoolName.orEmpty()
                }

                is Driver -> {
                    name = data.driver_name.orEmpty()
                    mobileNumber = data.number.orEmpty()
                    vehicleNumber = data.vehicle_number.orEmpty()
                    state = data.state.orEmpty()
                    district = data.district.orEmpty()
                    taluka = data.taluka.orEmpty()
                    city = data.city.orEmpty()
                    schoolName = data.school_serviced.orEmpty()
                }
            }
        }
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
                            enter = fadeIn(tween(700)) + scaleIn(initialScale = 0.8f),
                            exit = fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.TopEnd) {
                                    ProfileImage(
                                        imageUrl = profileImageUri?.toString()
                                            ?: "https://mykidvan.com/${(data as? Driver)?.profile_picture}"
                                    )

                                    if (isEditing) {
                                        IconButton(
                                            onClick = {
                                                imagePickerLauncher.launch("image/*")
                                            },
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .size(32.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Image",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = name.capitalize(),
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

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (isEditing) {
                                            // Save logic - you can pass all fields and imageUri to ViewModel
                                            viewModel.updateProfile(
                                                context = context,
                                                userId = userId,
                                                userType = userType,
                                                name = name,
                                                contact = contact,
                                                address = address,
                                                childName = childName,
                                                schoolName = schoolName,
                                                mobile = mobileNumber,
                                                vehicle = vehicleNumber,
                                                state = state,
                                                district = district,
                                                taluka = taluka,
                                                city = city,
                                                imageUri = profileImageUri
                                            )
                                        }
                                        isEditing = !isEditing
                                    }
                                ) {
                                    Text(if (isEditing) "Save" else "Edit", color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(1000)),
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    when (data) {
                                        is Parent -> {
                                            if (isEditing) {
                                                EditableField("Parent Name", name) { name = it }
                                                EditableField("Contact Number", contact) { contact = it }
                                                EditableField("Address", address) { address = it }
                                                EditableField("Child Name", childName) { childName = it }
                                                EditableField("School Name", schoolName) { schoolName = it }
                                            } else {
                                                ProfileDetailRow("Parent Name", name)
                                                ProfileDetailRow("Contact Number", contact)
                                                ProfileDetailRow("Address", address)
                                                ProfileDetailRow("Child Name", childName)
                                                ProfileDetailRow("School Name", schoolName)
                                            }
                                        }

                                        is Driver -> {
                                            if (isEditing) {
                                                EditableField("Driver Name", name) { name = it }
                                                EditableField("Mobile Number", mobileNumber) { mobileNumber = it }
                                                EditableField("Vehicle Number", vehicleNumber) { vehicleNumber = it }
                                                EditableField("State", state) { state = it }
                                                EditableField("District", district) { district = it }
                                                EditableField("Taluka", taluka) { taluka = it }
                                                EditableField("City", city) { city = it }
                                                EditableField("School", schoolName) { schoolName = it }
                                            } else {
                                                ProfileDetailRow("Driver Name", name)
                                                ProfileDetailRow("Mobile Number", mobileNumber)
                                                ProfileDetailRow("Vehicle Number", vehicleNumber)
                                                ProfileDetailRow("State", state)
                                                ProfileDetailRow("District", district)
                                                ProfileDetailRow("Taluka", taluka)
                                                ProfileDetailRow("City", city)
                                                ProfileDetailRow("School", schoolName)
                                            }
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
        // Show toast if update message is available
        LaunchedEffect(updateMessage) {
            updateMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
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
