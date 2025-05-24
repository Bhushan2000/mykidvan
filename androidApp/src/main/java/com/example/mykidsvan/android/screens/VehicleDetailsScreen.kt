package com.example.mykidsvan.android.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.dto.response.Driver
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

    val vehicleTypes = listOf("Van", "Auto Rickshaw", "Car", "Tempo", "Mini School Bus")
    var expanded by remember { mutableStateOf(false) }

    val profileData by viewModel.profileData.collectAsState()
    val isLoading = profileData == null

    LaunchedEffect(Unit) {
        if (assignVehicleId != null) {
            viewModel.loadProfile(assignVehicleId, "driver")
        } else {
            Toast.makeText(context, "Vehicle Owner not assigned yet", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(profileData) {
        delay(300)
        profileData?.let { data ->
            if (data is Driver) {
                ownerName.value = data.driver_name.orEmpty()
                vehicleNumber.value = data.vehicle_number.orEmpty()
                vehicleType.value = data.driver_type.orEmpty()
                model.value = data.vehicle_model.orEmpty()
                vehicleRegistration.value = data.vehicle_registration.orEmpty()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        } else {
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
                    onValueChange = { vehicleNumber.value = it },
                    label = { Text("Vehicle Number") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                )

                OutlinedTextField(
                    value = ownerName.value,
                    onValueChange = { ownerName.value = it },
                    label = { Text("Owner Name") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = vehicleType.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        vehicleTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    vehicleType.value = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = model.value,
                    onValueChange = { model.value = it },
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
                    label = { Text("Registration Date") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                )
            }
        }
    }
}


