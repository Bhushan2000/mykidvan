package com.vihaanshika.mykidsvan.android.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.vihaanshika.mykidsvan.android.MainActivity
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverMob
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.razorpay.PaymentData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.DisposableEffect
import coil.compose.AsyncImage
import com.vihaanshika.mykidsvan.android.utils.APIEndpoints
import com.vihaanshika.mykidsvan.android.utils.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindVehicleScreen(
    assignVehicleId: String?,
    viewModel: AuthViewModel,
    userId: String,
    onPaymentSuccess: (PaymentData) -> Unit,
    onPaymentFailure: (Int, String?) -> Unit,
    requestAssignedStatus: String?,
    trialDate: String?,
    paymentStatus: String?,
    payAmount: String?
) {
    val currentDate = LocalDate.now()
    val expireDate = currentDate.plusYears(1).format(DateTimeFormatter.ISO_DATE)
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val today = dateFormat.parse(dateFormat.format(Date()))
    val trial = dateFormat.parse(trialDate)


    // Razorpay Callbacks Setup
    LaunchedEffect(Unit) {
        viewModel.loadStateOptions()
    }


    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("By School", "By Mobile No.")

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()   // adds padding for status bar
                .padding(
                    top = innerPadding.calculateTopPadding() + 48.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Tab Navigation
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (selectedTabIndex) {
                0 -> FindBySchoolSection(
                    assignVehicleId,
                    payAmount?.toIntOrNull() ?: 0,
                    expireDate,
                    viewModel = viewModel,
                    userId = userId,
                    requestAssignedStatus,
                    today,
                    trial,
                    paymentStatus,
                    onPaymentSuccess,
                    onPaymentFailure
                )

                1 -> FindByMobileSection(
                    assignVehicleId,
                    payAmount?.toIntOrNull() ?: 0,
                    expireDate,
                    viewModel = viewModel,
                    userId = userId,
                    requestAssignedStatus,
                    today,
                    trial,
                    paymentStatus,
                    onPaymentSuccess,
                    onPaymentFailure
                )
            }
        }
    }
}


@Composable
fun FindByMobileSection(
    assignVehicleId: String?,
    paymentAmountInPaise: Int,
    expireDate: String,
    viewModel: AuthViewModel,
    userId: String,
    requestAssignedStatus: String?,
    today: Date?,
    trial: Date?,
    paymentStatus: String?,
    onPaymentSuccess: (PaymentData) -> Unit,
    onPaymentFailure: (Int, String?) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    var mobileNumber by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }

    val isLoading by viewModel.isAssigningSchool.collectAsState()
    val foundDriver by viewModel.foundDriver.collectAsState()

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
                        context, "Enter valid 10-digit mobile number", Toast.LENGTH_SHORT
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
                assignVehicleId = assignVehicleId,
                paymentAmountInPaise,
                expireDate,
                driver = foundDriver!!,
                viewModel = viewModel,
                userId = userId,
                isLoading = isLoading,
                requestAssignedStatus,
                today = today,
                trial = trial,
                paymentStatus = paymentStatus,
                onPaymentSuccess,
                onPaymentFailure
            )
        }
    }
    val assignResponse by viewModel.assignRequestResponse.collectAsState()
    LaunchedEffect(assignResponse) {
        assignResponse?.let { response ->
            Toast.makeText(
                context, "Assigned Request Sent Successfully", Toast.LENGTH_SHORT
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
fun FindBySchoolSection(
    assignVehicleId: String?,
    paymentAmountInPaise: Int,
    expireDate: String,
    viewModel: AuthViewModel,
    userId: String,
    requestAssignedStatus: String?,
    today: Date?,
    trial: Date?,
    paymentStatus: String?,
    onPaymentSuccess: (PaymentData) -> Unit,
    onPaymentFailure: (Int, String?) -> Unit
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
            })
        DropdownField(
            label = "District",
            selectedValue = selectedDistrict?.district_name.orEmpty(),
            options = districtOptions.map { it.district_name },
            onValueChange = { name ->
                districtOptions.find { it.district_name == name }
                    ?.let { viewModel.onDistrictSelected(it) }
            })

        DropdownField(
            label = "Taluka",
            selectedValue = selectedTaluka?.taluka_name.orEmpty(),
            options = talukaOptions.map { it.taluka_name },
            onValueChange = { name ->
                talukaOptions.find { it.taluka_name == name }
                    ?.let { viewModel.onTalukaSelected(it) }
            })

        DropdownField(
            label = "School",
            selectedValue = selectedSchool?.schoolName.orEmpty(),
            options = schoolOptions.map { it.schoolName },
            onValueChange = { name ->
                schoolOptions.find { it.schoolName == name }?.let { viewModel.onSchoolSelected(it) }
            })

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
                    color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp
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
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No Vehicles Found for This School",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Currently, no drivers have registered for this school. Please try again later or choose a nearby school.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        DriverCard(
                            assignVehicleId,
                            paymentAmountInPaise,
                            expireDate,
                            driver = driver,
                            viewModel = viewModel,
                            userId = userId,
                            requestAssignedStatus = requestAssignedStatus,
                            today = today,
                            trial = trial,
                            paymentStatus = paymentStatus,
                            onPaymentSuccess = onPaymentSuccess,
                            onPaymentFailure = onPaymentFailure
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

                // Vehicle Photo Preview (Max 3)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverCard(
    assignVehicleId: String?,
    paymentAmountInPaise: Int,
    expireDate: String,
    driver: DriverMob,
    viewModel: AuthViewModel,
    userId: String,
    isLoading: Boolean = false,
    requestAssignedStatus: String?,
    today: Date?,
    trial: Date?,
    paymentStatus: String?,
    onPaymentSuccess: (PaymentData) -> Unit,
    onPaymentFailure: (Int, String?) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val openDialog = remember { mutableStateOf(false) }
    val selectedImage = remember { mutableStateOf<String?>(null) }

    val vehiclePhotos =
        driver.photoOfVehicle?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }
            ?: emptyList()

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showSheet = remember { mutableStateOf(false) }

    // Sample payment values
    val fees = paymentAmountInPaise / 100
    val duration = "12 Months"
    val expiryDate = expireDate
    val total = fees

    // coupon code
    var showInitialDialog by remember { mutableStateOf(false) }
    var showCouponInputDialog by remember { mutableStateOf(false) }
    var couponCode by remember { mutableStateOf("") }
    val couponState by viewModel.couponCodeValidation.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val orderIdState by viewModel.getOrderId.collectAsState()
    var razorOrderId by remember { mutableStateOf<String?>(null) }
    var razorAmount by remember { mutableStateOf<Int?>(null) }
    val currentDate = LocalDate.now()
    val paymentDate = currentDate.format(DateTimeFormatter.ISO_DATE)

    LaunchedEffect(orderIdState) {
        if (orderIdState is Resource.Success) {
            val data = (orderIdState as Resource.Success).data
            data.let {
                razorOrderId = it.orderId
                razorAmount = it.amountPaise
            }
        }
    }

    LaunchedEffect(couponState) {
        when (val result = couponState) {
            is Resource.Success -> {
                razorOrderId = result.data.orderId
                razorAmount = result.data.payAmount?.toDoubleOrNull()?.times(100)?.toInt() ?: 0
                showCouponInputDialog = false
                showSheet.value = true
            }

            is Resource.Error -> {
                Toast.makeText(context, result.message ?: "Invalid coupon", Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        activity?.onPaymentSuccessCallback = { paymentData ->
            onPaymentSuccess(paymentData)
            driver.id.let { driverId ->
                viewModel.updatePaymentStatus(
                    id = userId.toInt(),
                    paymentId = paymentData.paymentId ?: "TXN",
                    amount = (razorAmount?.div(100)).toString(),
                    paymentStatus = "Paid",
                    expireDate = expireDate,
                    paymentDate = paymentDate,
                    assignStatus = "Assigned",
                    assignDate = paymentDate,
                    signature = paymentData.signature,
                    orderId = paymentData.orderId
                )

                viewModel.sendAssignRequest(driverId.toString(), userId)
            }
        }

        activity?.onPaymentFailureCallback = { code, message ->
            Toast.makeText(context, "Payment failed", Toast.LENGTH_SHORT).show()
            Log.e("TAG", "FindVehicleScreen: $message")
            onPaymentFailure(code, message)
        }
    }
    // UI Box with Driver Info
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                    )
                ), shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Driver Name
            Text(
                text = driver.driverName ?: "N/A",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            DetailItem("Mobile", driver.number ?: "N/A")
            DetailItem("Vehicle No", driver.vehicleNumber ?: "N/A")

            Spacer(modifier = Modifier.height(16.dp))

            if (vehiclePhotos.isNotEmpty()) {
                Text(
                    text = "Vehicle Photos",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(vehiclePhotos) { photoPath ->
                        AsyncImage(
                            model = "${APIEndpoints.BASE_URL}$photoPath",
                            contentDescription = "Vehicle Photo",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    selectedImage.value = "${APIEndpoints.BASE_URL}$photoPath"
                                    openDialog.value = true
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Call Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${driver.number}")
                        }
                        context.startActivity(intent)
                    }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call", color = Color.White)
                }

                // Send Request Button
                Button(
                    onClick = {
                        Log.d(
                            "TAG", "requestAssignedStatus - $requestAssignedStatus \n "
                                    + "paymentStatus - $paymentStatus \n "
                                    + "assignVehicleId - $assignVehicleId \n "
                                    + "today - $today \n "
                                    + "trial - $trial "
                        )
                        when {
                            // 1. Request is pending and already paid
                            requestAssignedStatus == Constants.REQUEST_PENDING && paymentStatus == "Paid" -> {
                                Toast.makeText(
                                    context,
                                    "Your request is already in Process",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            // 1. Request is pending and not paid(or on trial)
                            requestAssignedStatus == Constants.REQUEST_PENDING && paymentStatus == "Not Paid" -> {
                                Toast.makeText(
                                    context,
                                    "Your request is already in Process for trial period",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            // 2. Vehicle already assigned and accepted
                            assignVehicleId != null && requestAssignedStatus == Constants.REQUEST_ACCEPTED -> {
                                Toast.makeText(
                                    context, "Already Vehicle Owner Assigned", Toast.LENGTH_SHORT
                                ).show()
                            }

                            // 3. In trial period and not paid – send request
                            today != null && trial != null && today.before(trial) && paymentStatus.isNullOrEmpty() -> {
                                showDialog = true
                            }

                            // 4. No status and no payment – show dialog to make payment
                            requestAssignedStatus.isNullOrEmpty() && paymentStatus.isNullOrEmpty() -> {
                                showInitialDialog = true
                            }

                            // 5. Paid but request rejected – resend request
                            assignVehicleId != null && requestAssignedStatus == Constants.REQUEST_REJECTED && paymentStatus == "Paid" -> {
                                viewModel.sendAssignRequest(driver.id.toString(), userId)
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send", color = Color.White)
                    }
                }
            }
        }
    }

    if (showDialog) {
        SubscriptionDialog(onFreeTrialClick = {
            // Handle Free Trial logic
            showDialog = false
            viewModel.sendAssignRequest(driver.id.toString(), userId)
        }, onPayNowClick = {
            // Handle Pay Now logic
            showInitialDialog = true
            showDialog = false
        }, onDismiss = {
            showDialog = false
        })
    }

    // Image Preview Dialog
    if (openDialog.value && selectedImage.value != null) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                selectedImage.value = null
            },
            confirmButton = {},
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = selectedImage.value,
                        contentDescription = "Full Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.background
        )
    }

    if (showInitialDialog) {
        AlertDialog(
            onDismissRequest = { showInitialDialog = false },
            title = { Text("Coupon Code") },
            text = { Text("Do you have a coupon code?") },
            confirmButton = {
                TextButton(onClick = {
                    showInitialDialog = false
                    showCouponInputDialog = true
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showInitialDialog = false
                    showSheet.value = true
                    if (requestAssignedStatus.isNullOrEmpty()) viewModel.getRazorPayOrderId()
                }) {
                    Text("No")
                }
            })
    }

    if (showCouponInputDialog) {
        AlertDialog(
            onDismissRequest = { showCouponInputDialog = false },
            title = { Text("Enter Coupon Code") },
            text = {
                Column {
                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = { couponCode = it },
                        label = { Text("Coupon Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.couponValidation(parentId = userId, couponCode = couponCode)
                    viewModel.clearCouponCodeValidation()
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCouponInputDialog = false
                    showSheet.value = true
                    if (requestAssignedStatus.isNullOrEmpty()) viewModel.getRazorPayOrderId()
                }) {
                    Text("Cancel")
                }
            })
    }

    // Bottom Sheet
    if (showSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showSheet.value = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Text(
                    "\uD83D\uDE90 Send Tracking Request",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))


                val paymentDetailsText =
                    "If you’ve finalized this driver, send a tracking request now.\n" + "Once the driver accepts, you’ll be able to track your child’s school van live through the app.\n" + "\n" + "✅ Yearly Subscription Fee: ₹${
                        razorAmount?.div(
                            100
                        )
                    }/-\n" + "(That’s less than the price of a pizza for peace of mind all year!)"

                Text(
                    paymentDetailsText, style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(8.dp))

//                Text(
//                    "Fees: ₹$fees (Only 28 Paise per day)",
//                    style = MaterialTheme.typography.bodyLarge
//                )
                Text("Duration: $duration", style = MaterialTheme.typography.bodyLarge)
//                Text("Expiry Date: $expiryDate", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))

                Text(
                    "Total: ₹${razorAmount?.div(100)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { showSheet.value = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            showSheet.value = false
                            activity?.startPayment(razorAmount, razorOrderId) ?: Toast.makeText(
                                context, "Unable to start payment.", Toast.LENGTH_SHORT
                            ).show()


                        }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Continue")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
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