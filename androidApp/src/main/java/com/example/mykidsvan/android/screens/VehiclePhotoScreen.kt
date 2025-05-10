package com.example.mykidsvan.android.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import coil.compose.AsyncImage
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

    LaunchedEffect(isUploading) {
        if (wasUploading && !isUploading) {
            Toast.makeText(context, "✅ Upload successful! Thank you 🎉", Toast.LENGTH_LONG).show()
        }
        wasUploading = isUploading
    }

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

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Tap to upload each vehicle photo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(16.dp))

            UploadPhotoCard("Front View", frontImageUri) { frontPickerLauncher.launch("image/*") }
            UploadPhotoCard("Back View", backImageUri) { backPickerLauncher.launch("image/*") }
            UploadPhotoCard(
                "Inside View",
                insideImageUri
            ) { insidePickerLauncher.launch("image/*") }
            UploadPhotoCard(
                "Outside View",
                outsideImageUri
            ) { outsidePickerLauncher.launch("image/*") }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (frontImageUri == null || backImageUri == null || insideImageUri == null || outsideImageUri == null) {
                        Toast.makeText(context, "Please upload all photos", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val frontBase64 = uriToBase64(context, frontImageUri!!)
                        val backBase64 = uriToBase64(context, backImageUri!!)
                        val insideBase64 = uriToBase64(context, insideImageUri!!)
                        val outsideBase64 = uriToBase64(context, outsideImageUri!!)

                        viewModel.uploadVehiclePhotos(
                            Integer.valueOf(userId),
                            frontBase64,
                            backBase64,
                            insideBase64,
                            outsideBase64
                        )
                    }
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


@Composable
fun UploadPhotoCard(
    label: String,
    imageUri: Uri?,
    onClick: () -> Unit
) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "$label image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Pick $label image",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
