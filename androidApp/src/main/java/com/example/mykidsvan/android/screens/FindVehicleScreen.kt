package com.example.mykidsvan.android.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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

import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardType
import com.example.mykidsvan.android.MainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindVehicleScreen(
    viewModel: AuthViewModel,
    userId: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailure: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("By School", "By Mobile No.")

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> FindBySchoolSection(viewModel, userId, onPaymentSuccess, onPaymentFailure)
                1 -> FindByMobileSection(viewModel, userId, onPaymentSuccess, onPaymentFailure)
            }
        }
    }
}

@Composable
fun FindByMobileSection(
    viewModel: AuthViewModel,
    userId: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailure: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    var mobileNumber by remember { mutableStateOf("") }

    val isLoading by viewModel.isAssigningSchool.collectAsState()
    val assignMessage by viewModel.assignSchoolMessage.collectAsState()
    val foundDriver by viewModel.foundDriver.collectAsState()

    LaunchedEffect(assignMessage) {
        assignMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearAssignSchoolMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            label = { Text("Enter Driver Mobile No.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp), // Use shape here instead of clip
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E88E5),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (mobileNumber.length == 10) {
                    viewModel.findDriverByMobile(mobileNumber)
                } else {
                    Toast.makeText(
                        context,
                        "Enter valid 10-digit mobile number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Driver Details Card
        foundDriver?.let { driver ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Driver Found",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileDetailRow1("Name", driver.vehicle?.driverName ?: "N/A")
                        ProfileDetailRow1("Mobile", driver.vehicle?.number ?: "N/A")
                        ProfileDetailRow1("Vehicle No", driver.vehicle?.vehicleNumber ?: "N/A")

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${driver.vehicle?.number}")
                                }
                                context.startActivity(intent)
                            }) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = "Call Driver",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (!isLoading) {
                                        driver.vehicle?.id?.let {
                                            if (userId != null) {
                                                viewModel.sendAssignRequest(it, userId)
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
                                    Icon(
                                        Icons.Default.Send,
                                        contentDescription = "Send Request",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    val assignResponse by viewModel.assignRequestResponse.collectAsState()
    LaunchedEffect(assignResponse) {
        assignResponse?.let { response ->
            Toast.makeText(
                context,
                response.message ?: "Request Sent",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.clearResponses()
        }
    }
}

@Composable
private fun ProfileDetailRow1(label: String, value: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value ?: "N/A",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun FindBySchoolSection(
    viewModel: AuthViewModel,
    userId: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailure: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    val stateOptions by viewModel.stateOptions.collectAsState()
    val districtOptions by viewModel.districtOptions.collectAsState()
    val talukaOptions by viewModel.talukaOptions.collectAsState()
    val schoolOptions by viewModel.schoolOptions.collectAsState()
    val driverOptions by viewModel.driverOptions.collectAsState()

    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()
    val selectedSchool by viewModel.selectedSchool.collectAsState()

    var isSearching by remember { mutableStateOf(false) }

    Column {
        DropdownField(
            label = "State",
            selectedValue = selectedState?.state_name.orEmpty(),
            options = stateOptions.map { it.state_name },
            onValueChange = { name ->
                stateOptions.find { it.state_name == name }?.let { viewModel.onStateSelected(it) }
            }
        )

        DropdownField(
            label = "District",
            selectedValue = selectedDistrict?.district_name.orEmpty(),
            options = districtOptions.map { it.district_name },
            onValueChange = { name ->
                districtOptions.find { it.district_name == name }
                    ?.let { viewModel.onDistrictSelected(it) }
            }
        )

        DropdownField(
            label = "Taluka",
            selectedValue = selectedTaluka?.taluka_name.orEmpty(),
            options = talukaOptions.map { it.taluka_name },
            onValueChange = { name ->
                talukaOptions.find { it.taluka_name == name }
                    ?.let { viewModel.onTalukaSelected(it) }
            }
        )

        DropdownField(
            label = "School",
            selectedValue = selectedSchool?.schoolName.orEmpty(),
            options = schoolOptions.map { it.schoolName },
            onValueChange = { name ->
                schoolOptions.find { it.schoolName == name }?.let { viewModel.onSchoolSelected(it) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedState == null || selectedDistrict == null || selectedTaluka == null || selectedSchool == null) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    selectedSchool!!.id?.let {
                        isSearching = true
                        viewModel.loadDriverList(it)
                        isSearching = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            if (isSearching) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Search", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = driverOptions.isNotEmpty()) {
            Column(modifier = Modifier.animateContentSize()) {
                driverOptions.forEach { driver ->
                    DriverCard(
                        driver = driver,
                        viewModel = viewModel,
                        userId = userId,
                        startPayment = { onSuccess, onFailure ->
                            activity?.initiateDriverAssignPayment(
                                amountInPaise = 1000,
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
        assignResponse?.let {
            Toast.makeText(context, it.message ?: "Request Sent", Toast.LENGTH_SHORT).show()
            viewModel.clearResponses()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
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
                    .background(Color.LightGray, CircleShape)
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
                            { viewModel.sendAssignRequest(driver.id, userId) },
                            { /* You can show a Toast here if needed */ }
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

