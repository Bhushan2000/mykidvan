package com.vihaanshika.mykidsvan.android.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.example.authapp.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
            Toast.makeText(context, "Registration successful.", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo("schoolOnRegistration") { inclusive = true }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearUploadDocToDatabase()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Upload Documents") })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            FileUploadFieldDoc("Aadhar Card Photo", aadharPhoto) { aadharPhoto = it }
            FileUploadFieldDoc("Driver's License", licensePhoto) { licensePhoto = it }
            FileUploadFieldDoc("Insurance Photo", insurancePhoto) { insurancePhoto = it }
            FileUploadFieldDoc("Fitness Certificate", fitnessCertificate) {
                fitnessCertificate = it
            }
            FileUploadFieldDoc("Vehicle Photo", vehiclePhoto) { vehiclePhoto = it }
            FileUploadFieldDoc("Profile Picture", profilePicture) { profilePicture = it }

            Spacer(modifier = Modifier.height(24.dp))

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
                    .height(52.dp),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FileUploadFieldDoc(
    label: String, imageBase64: String, onImageUploaded: (String) -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), onResult = { uri ->
            uri?.let {
                imageUri = it
                val base64 = uriToBase64FileUpload(context, it)
                onImageUploaded(base64)

                // ✅ Show success toast
                Toast.makeText(context, "$label uploaded successfully!", Toast.LENGTH_SHORT).show()
            }
        })

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Upload", tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Choose Image", color = Color.White)
                }

                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
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
