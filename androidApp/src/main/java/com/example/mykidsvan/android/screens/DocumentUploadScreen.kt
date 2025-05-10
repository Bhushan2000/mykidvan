package com.example.mykidsvan.android.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DocumentUploadScreen(
    navController: NavController
) {
    val context = LocalContext.current

    // Define variables for document uploads
    var aadharPhoto by remember { mutableStateOf("") }
    var licensePhoto by remember { mutableStateOf("") }
    var insurancePhoto by remember { mutableStateOf("") }
    var fitnessCertificate by remember { mutableStateOf("") }
    var vehiclePhoto by remember { mutableStateOf("") }
    var profilePicture by remember { mutableStateOf("") }

    // Use your existing UI components to create the document upload fields
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionTitle("Documents Upload")

        FileUploadField("Upload Aadhar Photo", aadharPhoto) { aadharPhoto = it }
        FileUploadField("Upload Driver's License Photo", licensePhoto) { licensePhoto = it }
        FileUploadField("Upload Insurance Details Photo", insurancePhoto) { insurancePhoto = it }
        FileUploadField("Upload Fitness Certificate", fitnessCertificate) { fitnessCertificate = it }
        FileUploadField("Upload Vehicle Photo", vehiclePhoto) { vehiclePhoto = it }
        FileUploadField("Upload Profile Picture", profilePicture) { profilePicture = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Handle any logic or validation here, then navigate to the next screen if needed
                navController.navigate("nextScreen")  // Navigate to the next screen
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Continue", color = Color.White)
        }
    }
}
