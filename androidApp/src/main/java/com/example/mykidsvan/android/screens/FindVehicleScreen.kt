package com.example.mykidsvan.android.screens

import com.example.mykidsvan.android.R // ✅ Important: ensure R is correctly imported from your app module
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
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

import androidx.compose.material3.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mykidsvan.android.MainActivity
import com.example.mykidsvan.android.data.dto.response.DriverMob
import com.example.mykidsvan.android.utils.Constants
import com.razorpay.PaymentData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.DisposableEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindVehicleScreen(
    viewModel: AuthViewModel,
    userId: String,
    onPaymentSuccess: (PaymentData) -> Unit,
    onPaymentFailure: (Int, String?) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    val selectedDriverId = remember { mutableStateOf<String?>(null) }
    val paymentAmountInPaise = 10000 // ₹100

    // Razorpay Callbacks Setup
    LaunchedEffect(Unit) {
        activity?.onPaymentSuccessCallback = { paymentData ->
            onPaymentSuccess(paymentData)
            selectedDriverId.value?.let { driverId ->
                val currentDate = LocalDate.now()
                val paymentDate = currentDate.format(DateTimeFormatter.ISO_DATE)
                val expireDate = currentDate.plusYears(1).format(DateTimeFormatter.ISO_DATE)

                viewModel.updatePaymentStatus(
                    id = userId.toInt(),
                    transactionId = paymentData.paymentId ?: "TXN",
                    amount = paymentAmountInPaise.toString(),
                    paymentStatus = "Paid",
                    expireDate = expireDate,
                    paymentDate = paymentDate,
                    assignStatus = "Assigned",
                    assignDate = paymentDate
                )

                viewModel.sendAssignRequest(driverId, userId)
            }
        }

        activity?.onPaymentFailureCallback = { code, message ->
            Toast.makeText(context, "Payment failed: $message", Toast.LENGTH_SHORT).show()
            onPaymentFailure(code, message)
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("By School", "By Mobile No.")

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Tab Navigation
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

            // Tab Content
            when (selectedTabIndex) {
                0 -> FindBySchoolSection(
                    viewModel = viewModel,
                    userId = userId,
                    onStartPayment = { driverId ->
                        selectedDriverId.value = driverId
                        activity?.startPayment(paymentAmountInPaise)
                            ?: Toast.makeText(
                                context,
                                "Unable to start payment.",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                )

                1 -> FindByMobileSection(
                    viewModel = viewModel,
                    userId = userId,
                    onStartPayment = { driverId ->
                        selectedDriverId.value = driverId
                        activity?.startPayment(paymentAmountInPaise)
                            ?: Toast.makeText(
                                context,
                                "Unable to start payment.",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                )
            }
        }
    }
}


@Composable
fun FindByMobileSection(
    viewModel: AuthViewModel,
    userId: String,
    onStartPayment: (driverId: String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    var mobileNumber by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }

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

    ) {
        OutlinedTextField(
            value = mobileNumber,
            onValueChange = { if (it.length <= 10) mobileNumber = it },
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
                    hasSearched = true // Mark that user has searched

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
            Text("Search", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        // Driver Details Card
        if (hasSearched && foundDriver?.id == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No data found for above Mobile number",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else if (foundDriver?.id != null) {
            DriverCard(
                driver = foundDriver!!,
                viewModel = viewModel,
                userId = userId,
                onStartPayment = onStartPayment,
                isLoading = isLoading
            )
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
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearFindDriverByMobile()
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
    onStartPayment: (driverId: String) -> Unit
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
        Spacer(modifier = Modifier.height(16.dp))
        DropdownField(
            label = "District",
            selectedValue = selectedDistrict?.district_name.orEmpty(),
            options = districtOptions.map { it.district_name },
            onValueChange = { name ->
                districtOptions.find { it.district_name == name }
                    ?.let { viewModel.onDistrictSelected(it) }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "Taluka",
            selectedValue = selectedTaluka?.taluka_name.orEmpty(),
            options = talukaOptions.map { it.taluka_name },
            onValueChange = { name ->
                talukaOptions.find { it.taluka_name == name }
                    ?.let { viewModel.onTalukaSelected(it) }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

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
                    if (driver.driverName.equals("No data found", ignoreCase = true)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No data found for above selection",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        DriverCard(
                            driver = driver,
                            viewModel = viewModel,
                            userId = userId,
                            onStartPayment = onStartPayment
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }
        }
    }
}

/*@Composable
fun DriverCard(
    driver: Driver,
    viewModel: AuthViewModel,
    userId: String,
    onStartPayment: (driverId: String) -> Unit,
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
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = "${Constants.BASE_URL}${driver.profile_picture}",
                contentDescription = "Driver Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = driver.driver_name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = driver.number, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                driver.vehicle_number?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }

                // ✅ Vehicle Photo Preview (Max 3)
                val vehiclePhotos = driver.photo_of_vehicle
                    ?.split(",") // Split by comma
                    ?.filter { it.isNotBlank() } // Remove empty entries
                    ?.take(3) // Only take first 3 photos
                    ?: emptyList()

                if (vehiclePhotos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Vehicle Photos:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        vehiclePhotos.forEach { photoPath ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("${Constants.BASE_URL}$photoPath")
                                    .crossfade(true)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.placeholder_image)
                                    .build(),
                                contentDescription = "Vehicle Photo",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            //    startPayment(driver.id) // Just pass driver ID
                            onStartPayment(driver.id)
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
}*/

@Composable
fun DriverCard(
    driver: DriverMob,
    viewModel: AuthViewModel,
    userId: String,
    onStartPayment: (String) -> Unit,
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val selectedImage = remember { mutableStateOf<String?>(null) }

    val vehiclePhotos = driver.photoOfVehicle
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Driver Name (Bold and Large)
            Text(
                text = driver.driverName ?: "N/A",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            DetailItem("Mobile", driver.number ?: "N/A")
            DetailItem("Vehicle No", driver.vehicleNumber ?: "N/A")

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle Photos
            if (vehiclePhotos.isNotEmpty()) {
                Text(
                    text = "Vehicle Photos",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(vehiclePhotos) { photoPath ->
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("${Constants.BASE_URL}$photoPath")
                                .crossfade(true)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.placeholder_image)
                                .build(),
                            contentDescription = "Vehicle Photo",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    selectedImage.value = "${Constants.BASE_URL}$photoPath"
                                    openDialog.value = true
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Buttons Row (Send + Call aligned)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // Call Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${driver.number}")
                        }
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Driver",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Call",
                        color = Color.White
                    )
                }

                // Send Request Button
                Button(
                    onClick = {
                        if (!isLoading) onStartPayment(driver.id.toString())
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Request",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Send",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Full Image Preview Dialog
    if (openDialog.value && selectedImage.value != null) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                selectedImage.value = null
            },
            confirmButton = {},
            text = {
                AsyncImage(
                    model = selectedImage.value,
                    contentDescription = "Full Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


