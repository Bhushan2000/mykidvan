package com.vihaanshika.mykidsvan.android.ui

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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverMob
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.PlaceHolders
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsScreen(
    viewModel: AuthViewModel,
    assignVehicleId: String?,
    vehicleTrackingStatus: String?,
    userRole: String?
) {
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
            .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        when {
            assignVehicleId == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(), // Fills the whole screen
                    contentAlignment = Alignment.Center // Centers the inner content
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp) // This adds margin around the whole message
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    Color.White.copy(alpha = 0.8f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = PlaceHolders.MSG_NO_VEHICLE_ASSIGNED,
                                color = Color.Red,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = PlaceHolders.MSG_REQUEST_DRIVER_ASSIGNMENT,
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

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

                    OutlinedTextField(
                        value = vehicleNumber.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Number") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )

                    OutlinedTextField(
                        value = ownerName.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Owner Name") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )

                    OutlinedTextField(
                        value = vehicleType.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Type") },
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )

                    OutlinedTextField(
                        value = model.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Model") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )

                    OutlinedTextField(
                        value = vehicleRegistration.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Registration Number") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
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
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            ZoomableImage(
                imageUrl = photoUrl,
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize()
                    .sizeIn(maxWidth = 360.dp, maxHeight = 480.dp) // adjust as needed
            )
        }
    }
}


@Composable
fun ZoomableImage(imageUrl: String, modifier: Modifier = Modifier) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 5f)
        scale = newScale

        val maxOffsetX = (scale - 1f) * 300f
        val maxOffsetY = (scale - 1f) * 500f

        val newOffset = offset + offsetChange
        offset = Offset(
            x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
            y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
        )
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { /* Optional tap handler */ })
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(transformableState)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(16.dp))
        )
    }
}
