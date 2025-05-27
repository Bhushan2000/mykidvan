package com.example.mykidsvan.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.dto.response.Driver
import com.example.mykidsvan.android.data.dto.response.DriverMob
import com.example.mykidsvan.android.utils.Constants
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsScreen(viewModel: AuthViewModel, assignVehicleId: String?, userRole: String?) {
    val context = LocalContext.current
    val vehicleNumber = remember { mutableStateOf("") }
    val ownerName = remember { mutableStateOf("") }
    val vehicleType = remember { mutableStateOf("") }
    val model = remember { mutableStateOf("") }
    val vehicleRegistration = remember { mutableStateOf("") }
    val vehiclePhotosState = remember { mutableStateOf("") }

    val profileData by viewModel.profileData.collectAsState()
    val isLoading = profileData == null && assignVehicleId != null

    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (assignVehicleId != null) {
            viewModel.loadProfile(assignVehicleId, "driver")
        }
    }

    LaunchedEffect(profileData) {
        delay(300)
        profileData?.let { data ->
            if (data is DriverMob) {
                ownerName.value = data.driverName.orEmpty()
                vehicleNumber.value = data.vehicleNumber.orEmpty()
                vehicleType.value = data.driverType.orEmpty()
                model.value = data.vehicleModel.orEmpty()
                vehicleRegistration.value = data.vehicleRegistration.orEmpty()
                vehiclePhotosState.value = data.photoOfVehicle.orEmpty()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            assignVehicleId == null -> {
                Text(
                    text = "No vehicle found and No student found.",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            profileData == null -> {
                Text(
                    text = "Vehicle data not found",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Vehicle Details",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    OutlinedTextField(
                        value = vehicleNumber.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Number") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )

                    OutlinedTextField(
                        value = ownerName.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Owner Name") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )

                    OutlinedTextField(
                        value = vehicleType.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )

                    OutlinedTextField(
                        value = model.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Model") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )

                    OutlinedTextField(
                        value = vehicleRegistration.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Registration Number") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )

                    val photoList = vehiclePhotosState.value
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .take(5)

                    if (photoList.isNotEmpty()) {
                        Text(
                            text = "Vehicle Photos:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(photoList) { photoPath ->
                                val url = "${Constants.BASE_URL}$photoPath"
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Vehicle Photo",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                                        .clickable { selectedImageUrl = url },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                }

                selectedImageUrl?.let { url ->
                    ZoomableImageViewer(photoUrl = url) {
                        selectedImageUrl = null
                    }
                }
            }
        }
    }
}
@Composable
fun ZoomableImageViewer(photoUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        ZoomableImage(
            imageUrl = photoUrl,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }
}

@Composable
fun ZoomableImage(imageUrl: String, modifier: Modifier = Modifier) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += offsetChange
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = { /* Optional tap logic */ })
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
