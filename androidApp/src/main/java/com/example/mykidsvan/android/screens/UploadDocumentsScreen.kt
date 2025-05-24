package com.example.mykidsvan.android.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import com.example.authapp.presentation.viewmodel.AuthViewModel

@Composable
fun UploadDocumentsScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    uid: String,
    onSubmit: (Map<String, String>) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var aadharPhoto by remember { mutableStateOf("") }
    var licensePhoto by remember { mutableStateOf("") }
    var insurancePhoto by remember { mutableStateOf("") }
    var fitnessCertificate by remember { mutableStateOf("") }
    var vehiclePhoto by remember { mutableStateOf("") }
    var profilePicture by remember { mutableStateOf("") }
    val uploadSuccess by viewModel.uploadDocSuccess.collectAsState()

    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess != null) {
            navController.navigate("login") {
                popUpTo("schoolOnRegistration") { inclusive = true }
            }
        }
    }
    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FileUploadFieldDoc("Upload Aadhar Photo", aadharPhoto) { aadharPhoto = it }
            FileUploadFieldDoc("Upload Driver's License", licensePhoto) { licensePhoto = it }
            FileUploadFieldDoc("Upload Insurance Photo", insurancePhoto) { insurancePhoto = it }
            FileUploadFieldDoc(
                "Upload Fitness Certificate",
                fitnessCertificate
            ) { fitnessCertificate = it }
            FileUploadFieldDoc("Upload Vehicle Photo", vehiclePhoto) { vehiclePhoto = it }
            FileUploadFieldDoc("Upload Profile Picture", profilePicture) { profilePicture = it }

            Button(
                onClick = {
                    val data = mapOf(
                        "id" to uid,
                        "profile_picture" to profilePicture,
                        "adhar_number" to aadharPhoto,
                        "driver_license" to licensePhoto,
                        "insurance_details" to insurancePhoto,
                        "fitness_certificate" to fitnessCertificate,
                        "photo_of_vehicle" to vehiclePhoto
                    )

                    if (data.values.all { it.isNotBlank() }) {
                        onSubmit(data)
                    } else {
                        Toast.makeText(context, "Please upload all documents", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FileUploadFieldDoc(
    label: String,
    imageBase64: String,
    onImageUploaded: (String) -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                val base64 = uriToBase64FileUpload(context, it)
                onImageUploaded(base64)
            }
        }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Upload", tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Choose Image", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

fun uriToBase64FileUpload(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else ""
    } catch (e: Exception) {
        ""
    }
}
