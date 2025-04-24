package com.example.mykidsvan.android.screens

import android.app.Activity
import android.content.Context
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.dto.response.Driver
import kotlin.math.log

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mykidsvan.android.MainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindVehicleScreen(viewModel: AuthViewModel,userId:String,
                      onPaymentSuccess: () -> Unit,
                      onPaymentFailure: () -> Unit) {

    // Observe the list of states from the ViewModel
    val stateOptions by viewModel.stateOptions.collectAsState()
    val districtOptions by viewModel.districtOptions.collectAsState()
    val talukaOptions by viewModel.talukaOptions.collectAsState()
    val schoolOptions by viewModel.schoolOptions.collectAsState()

    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()
    val selectedSchool by viewModel.selectedSchool.collectAsState()

    val driverOptions by viewModel.driverOptions.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? MainActivity

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            DropdownField(
                label = "State",
                selectedValue = selectedState?.state_name ?: "",
                options = stateOptions.map { it.state_name },
                onValueChange = { selectedName ->
                    val state = stateOptions.find { it.state_name == selectedName }
                    state?.let { viewModel.onStateSelected(it) }
                }
            )

            DropdownField(
                label = "District",
                selectedValue = selectedDistrict?.district_name ?: "",
                options = districtOptions.map { it.district_name },
                onValueChange = { selectedName ->
                    val district = districtOptions.find { it.district_name == selectedName }
                    district?.let { viewModel.onDistrictSelected(it) }
                }
            )

            DropdownField(
                label = "Taluka",
                selectedValue = selectedTaluka?.taluka_name ?: "",
                options = talukaOptions.map { it.taluka_name },
                onValueChange = { selectedName ->
                    val taluka = talukaOptions.find { it.taluka_name == selectedName }
                    taluka?.let { viewModel.onTalukaSelected(it) }
                }
            )

            DropdownField(
                label = "School",
                selectedValue = selectedSchool?.schoolName ?: "",
                options = schoolOptions.map { it.schoolName },
                onValueChange = { selectedName ->
                    val school = schoolOptions.find { it.schoolName == selectedName }
                    school?.let { viewModel.onSchoolSelected(it) }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Button with Loading Indicator
            Button(
                onClick = {
                    if (selectedState == null || selectedDistrict == null ||
                        selectedTaluka == null || selectedSchool == null
                    ) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        selectedSchool!!.id?.let { viewModel.loadDriverList(it) }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Search", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Driver List with Animation
            if (driverOptions.isNotEmpty()) {
                AnimatedVisibility(visible = driverOptions.isNotEmpty()) {
                    Column(
                        modifier = Modifier.animateContentSize()
                    ) {
                        driverOptions.forEach { driver ->
                            DriverCard(
                                driver = driver,
                                viewModel = viewModel,
                                userId = userId,
                                startPayment = { onSuccess, onFailure ->
                                    activity?.initiateDriverAssignPayment(
                                        amountInPaise = 1000, // ₹10
                                        onSuccess = onSuccess,
                                        onFailure = onFailure
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverCard(
    driver: Driver,
    viewModel: AuthViewModel,
    userId: String,
    startPayment: (onSuccess: () -> Unit, onFailure: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val isLoading = viewModel.isLoading
    val assignResponse by viewModel.assignRequestResponse.collectAsState()

    LaunchedEffect(assignResponse) {
        assignResponse?.let { response ->
            Toast.makeText(context, response.message ?: "Request Sent", Toast.LENGTH_SHORT).show()
            viewModel.clearResponses()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Driver Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.LightGray, shape = CircleShape)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = driver.driver_name, fontWeight = FontWeight.Bold)
                Text(text = driver.number, style = MaterialTheme.typography.bodyMedium)
            }

            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${driver.number}")
                }
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Call, contentDescription = "Call Driver")
            }

            IconButton(
                onClick = {
                    if (!isLoading) {
                        startPayment(
                            {
                                viewModel.sendAssignRequest(driver.id, userId)
                            },
                            {
//                                Toast.makeText(context, "Payment Failed", Toast.LENGTH_SHORT).show()
                                // payment failed
                            }
                        )
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

        }
    }
}


