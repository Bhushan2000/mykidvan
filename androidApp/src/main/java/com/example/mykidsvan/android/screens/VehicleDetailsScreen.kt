package com.example.mykidsvan.android.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsScreen() {
    val context = LocalContext.current
    val vehicleNumber = remember { mutableStateOf("") }
    val ownerName = remember { mutableStateOf("") }
    val vehicleType = remember { mutableStateOf("") }
    val model = remember { mutableStateOf("") }
    val registrationDate = remember { mutableStateOf("") }

    val vehicleTypes = listOf("Car", "Bike", "Truck", "Bus")
    var expanded by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            value = registrationDate.value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Registration Date") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        showDatePickerDialog(context, calendar) {
                            registrationDate.value = it
                        }
                    }
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable {
                    showDatePickerDialog(context, calendar) {
                        registrationDate.value = it
                    }
                }
        )

        OutlinedButton(
            onClick = {
                // TODO: open file/image picker
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Upload RC Book")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                vehicleNumber.value = ""
                ownerName.value = ""
                vehicleType.value = ""
                model.value = ""
                registrationDate.value = ""
            }) {
                Text("Clear", color = Color.White)
            }

            Button(onClick = {
                // TODO: handle submit
            }) {
                Text("Submit", color = Color.White)
            }
        }
    }
}

// 🔁 Reusable Date Picker Logic
fun showDatePickerDialog(
    context: Context,
    calendar: Calendar,
    onDateSelected: (String) -> Unit
) {
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            onDateSelected(formatter.format(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}



