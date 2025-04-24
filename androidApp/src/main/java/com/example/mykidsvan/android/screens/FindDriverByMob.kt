package com.example.mykidsvan.android.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.dto.response.Driver

@Composable
fun FindDriverByMob(viewModel: AuthViewModel, userId: String?) {

    val context = LocalContext.current

    var mobileNumber by remember { mutableStateOf("") }
    val isLoading by viewModel.isAssigningSchool.collectAsState()
    val assignMessage by viewModel.assignSchoolMessage.collectAsState()
    val foundDriver by viewModel.foundDriver.collectAsState() // Assuming this holds driver data after fetch

    LaunchedEffect(assignMessage) {
        assignMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearAssignSchoolMessage()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // Mobile Number Input
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = {
                    if (it.length <= 10) mobileNumber = it.filter { char -> char.isDigit() }
                },
                label = { Text("Enter Driver's Mobile Number") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (mobileNumber.length != 10) {
                        Toast.makeText(
                            context,
                            "Please enter valid 10-digit mobile number",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.findDriverByMobile(mobileNumber) // You call your ViewModel function here
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Submit", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Show Driver Details if found
            foundDriver?.let { driver ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Driver Name: ${driver.vehicle?.driverName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Mobile: ${driver.vehicle?.number}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Vehicle No: ${driver.vehicle?.vehicleNumber}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        // Add more details as needed


                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data =
                                        Uri.parse("tel:${driver.vehicle?.number ?: driver.vehicle?.number}")
                                }
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Default.Call, contentDescription = "Call Driver")
                            }

                            IconButton(
                                onClick = {
                                    if (!isLoading) {
                                        driver.vehicle?.id?.let {
                                            if (userId != null) {
                                                viewModel.sendAssignRequest(
                                                    it,
                                                    userId
                                                )
                                            }
                                        }
                                    }
                                },
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = "Send Request")
                                }
                            }

                            val assignResponse by viewModel.assignRequestResponse.collectAsState()
                            // Show Toast message when response changes
                            LaunchedEffect(assignResponse) {
                                assignResponse?.let { response ->
                                    Toast.makeText(
                                        context,
                                        response.message ?: "Request Sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Optional: reset state after showing
                                    viewModel.clearResponses()
                                }
                            }

                        }

                    } //
                }
            }
        }
    }
}




