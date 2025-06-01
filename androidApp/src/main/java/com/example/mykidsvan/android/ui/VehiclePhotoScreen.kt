package com.example.mykidsvan.android.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.authapp.presentation.viewmodel.AuthViewModel

@Composable
fun VehiclePhotoScreen(viewModel: AuthViewModel, userId: String) {
    val context = LocalContext.current

    var frontImageUri by remember { mutableStateOf<Uri?>(null) }
    var backImageUri by remember { mutableStateOf<Uri?>(null) }
    var insideImageUri by remember { mutableStateOf<Uri?>(null) }
    var outsideImageUri by remember { mutableStateOf<Uri?>(null) }

    val isUploading by viewModel.isUploading.collectAsState()
    var wasUploading by remember { mutableStateOf(false) }

    val vehiclePhotos by viewModel.vehiclePhotos.collectAsState()

    // Fetch photos when screen opens
    LaunchedEffect(Unit) {
        viewModel.getVehiclePhotos()
    }

    // Upload success toast
//    LaunchedEffect(isUploading) {
//        if (wasUploading && !isUploading) {
//            Toast.makeText(context, "✅ Upload successful! Thank you 🎉", Toast.LENGTH_LONG).show()
//        }
//        wasUploading = isUploading
//    }

    // Image pickers
    val frontPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            frontImageUri = uri
        }
    val backPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            backImageUri = uri
        }
    val insidePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            insideImageUri = uri
        }
    val outsidePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            outsideImageUri = uri
        }

    // Server image fallback
    val serverFront = vehiclePhotos.getOrNull(0)?.let { ensureFullUrl(it) }
    val serverBack = vehiclePhotos.getOrNull(1)?.let { ensureFullUrl(it) }
    val serverInside = vehiclePhotos.getOrNull(2)?.let { ensureFullUrl(it) }
    val serverOutside = vehiclePhotos.getOrNull(3)?.let { ensureFullUrl(it) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding( top = 100.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Tap to upload each vehicle photo", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(16.dp))

            UploadPhotoCard("Front View", frontImageUri, serverFront) { frontPickerLauncher.launch("image/*") }
            UploadPhotoCard("Back View", backImageUri, serverBack) { backPickerLauncher.launch("image/*") }
            UploadPhotoCard("Inside View", insideImageUri, serverInside) { insidePickerLauncher.launch("image/*") }
            UploadPhotoCard("Outside View", outsideImageUri, serverOutside) { outsidePickerLauncher.launch("image/*") }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val frontBase64 = frontImageUri?.let { uriToBase64(context, it) } ?: ""
                    val backBase64 = backImageUri?.let { uriToBase64(context, it) } ?: ""
                    val insideBase64 = insideImageUri?.let { uriToBase64(context, it) } ?: ""
                    val outsideBase64 = outsideImageUri?.let { uriToBase64(context, it) } ?: ""

                    viewModel.uploadVehiclePhotos(
                        Integer.valueOf(userId),
                        frontBase64,
                        backBase64,
                        insideBase64,
                        outsideBase64
                    )
                },
                enabled = !isUploading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Submit", fontSize = 16.sp, color = Color.White)
                }
            }

            val uploadMessage by viewModel.uploadMessage.collectAsState()
            uploadMessage?.let { message ->
                LaunchedEffect(message) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    viewModel.clearUploadMessage()
                }
            }
        }
    }
}

fun ensureFullUrl(path: String): String {
    return if (path.startsWith("http")) path
    else "https://avschoolerp.com/$path"
}


@Composable
fun UploadPhotoCard(
    label: String,
    imageUri: Uri?,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        val painter = when {
            imageUri != null -> rememberAsyncImagePainter(imageUri)
            imageUrl != null -> rememberAsyncImagePainter(imageUrl)
            else -> null
        }

        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            painter?.let {
                Image(
                    painter = it,
                    contentDescription = "$label Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } ?: Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Upload $label",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

