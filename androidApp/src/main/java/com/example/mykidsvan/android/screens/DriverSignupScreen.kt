package com.example.mykidsvan.android.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverSignupScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Form field values
    var ownerName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var aadharPhoto by remember { mutableStateOf("") }
    var licensePhoto by remember { mutableStateOf("") }
    var vehicleRegNumber by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var seatingCapacity by remember { mutableStateOf("") }
    var insurancePhoto by remember { mutableStateOf("") }
    var fitnessCertificate by remember { mutableStateOf("") }
    var vehiclePhoto by remember { mutableStateOf("") }
    var areasCovered by remember { mutableStateOf("") }
    var schoolServiced by remember { mutableStateOf("") }
    var profilePicture by remember { mutableStateOf("") }
    var aboutMe by remember { mutableStateOf("") }
    var verificationState by remember { mutableStateOf("") }
    var availabilityStatus by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Dropdown state
    val stateOptions by viewModel.stateOptions.collectAsState()
    val districtOptions by viewModel.districtOptions.collectAsState()
    val talukaOptions by viewModel.talukaOptions.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()

    // Referral Code States
    val referralCode = remember { mutableStateOf(generateReferralCodeDriver()) }
    val enteredReferralCode = remember { mutableStateOf("") }
    val isReferralCodeApplied = remember { mutableStateOf(false) }

    val vehicleTypes = listOf("Van", "Auto Rickshaw", "Car", "Tempo", "Mini School Bus")


    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        disabledBorderColor = Color.LightGray,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.Gray
    )

    Scaffold(topBar = {
        TopAppBar(title = { Text("Driver Registration Form") })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionTitle("Personal Details")

            OutlinedTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = { Text("Vehicle Owner Name") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            OutlinedTextField(
                value = contactNumber,
                onValueChange = { contactNumber = it },
                label = { Text("Contact Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.visibility else R.drawable.visibility_off
                            ),
                            contentDescription = null
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Location Details")

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


            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Documents Upload")

            FileUploadField("Upload Aadhar Photo", aadharPhoto) { aadharPhoto = it }
            FileUploadField("Upload Driver's License Photo", licensePhoto) { licensePhoto = it }
            FileUploadField("Upload Insurance Details Photo", insurancePhoto) {
                insurancePhoto = it
            }
            FileUploadField("Upload Fitness Certificate", fitnessCertificate) {
                fitnessCertificate = it
            }
            FileUploadField("Upload Vehicle Photo", vehiclePhoto) { vehiclePhoto = it }
            FileUploadField("Upload Profile Picture", profilePicture) { profilePicture = it }

            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Vehicle Details")

            OutlinedTextField(
                value = vehicleRegNumber,
                onValueChange = { vehicleRegNumber = it },
                label = { Text("Vehicle Registration Number") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            OutlinedTextField(
                value = vehicleModel,
                onValueChange = { vehicleModel = it },
                label = { Text("Vehicle Model") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            OutlinedTextField(
                value = seatingCapacity,
                onValueChange = { seatingCapacity = it },
                label = { Text("Seating Capacity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            DropdownField("Vehicle Type", vehicleType, vehicleTypes) { vehicleType = it }

            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Additional Information")

            OutlinedTextField(
                value = areasCovered,
                onValueChange = { areasCovered = it },
                label = { Text("Areas/Routes Covered") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            OutlinedTextField(
                value = schoolServiced,
                onValueChange = { schoolServiced = it },
                label = { Text("Schools Serviced/Interested In") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            OutlinedTextField(
                value = aboutMe,
                onValueChange = { aboutMe = it },
                label = { Text("About Me") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            // Referral Code UI
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Your Referral Code: ${referralCode.value}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Row for Referral Code input and Apply button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Referral Code Text Field
                OutlinedTextField(
                    value = enteredReferralCode.value,
                    onValueChange = { enteredReferralCode.value = it },
                    label = { Text("Referral Code (Optional)") },
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors,
                    modifier = Modifier
                        .weight(1f) // Makes the text field take the available space
                        .height(64.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Apply Button
                Button(
                    onClick = {
                        if (enteredReferralCode.value.isNotBlank()) {
                            // Apply the entered referral code (you can integrate this with your ViewModel or API)
                            isReferralCodeApplied.value = true
                            Toast.makeText(context, "Referral Code Applied", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .padding(start = 8.dp) // Optional, to add some spacing between elements
                ) {
                    Text(text = "Apply", color = Color.White)
                }
            }





            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
                Text("Accept Terms and Conditions", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (ownerName.isBlank() || contactNumber.isBlank() || !termsAccepted) {
                        Toast.makeText(
                            context,
                            "Please fill mandatory fields and accept the terms.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        isLoading = true
                        viewModel.registerDriver(
                            ownerName,
                            contactNumber,
                            password,
                            selectedState?.id ?: "",
                            selectedDistrict?.id ?: "",
                            selectedTaluka?.id ?: "",
                            city,
                            address,
                            aadharPhoto,
                            licensePhoto,
                            vehicleRegNumber,
                            vehicleModel,
                            seatingCapacity,
                            vehicleType,
                            insurancePhoto,
                            fitnessCertificate,
                            vehiclePhoto,
                            areasCovered,
                            schoolServiced,
                            profilePicture,
                            aboutMe,
                            verificationState,
                            availabilityStatus,
                            termsAccepted.toString(),
                            referralCode.value,
                            enteredReferralCode.value
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)

            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp
                    )
                } else {
                    Text("Register", color = Color.White)
                }
            }

            val registrationSuccess by viewModel.driverRegistrationSuccess.collectAsState()

            LaunchedEffect(registrationSuccess) {
                if (registrationSuccess?.status == true) {
                    isLoading = false
                    val uid = registrationSuccess?.id.toString()
                    navController.navigate("schoolOnRegistration/$uid")  // Navigate to the login screen
                } else {
                    isLoading = false
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSchoolRegistrationDropDowns()
        }
    }
}


@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}


@Composable
fun DropdownField(
    label: String,
    selectedValue: String,
    options: List<String?>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldWidth = remember { mutableStateOf(0) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        disabledBorderColor = Color.LightGray,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.Gray,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                textFieldWidth.value = coordinates.size.width
            }
    ) {
        // Wrap with clickable to open dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                label = { Text(label) },
                readOnly = true,
                enabled = false, // Make field non-interactive, so Box handles clicks
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldWidth.value.toDp() })
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp))
                .shadow(4.dp, shape = RoundedCornerShape(10.dp))
        ) {
            options.forEach { option ->
                option?.let {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        },
                        onClick = {
                            onValueChange(it)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun FileUploadField(
    label: String, imageBase64: String, onImageUploaded: (String) -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                if (uri != null) {
                    imageUri = uri
                    val base64Image = uriToBase64(context, uri)
                    onImageUploaded(base64Image)  // Convert and upload the Base64 image
                }
            })

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (imageUri == null) label else "Change Image", color = Color.White)
        }

        // Display the selected image
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
    }
}

fun uriToBase64(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes()
    inputStream?.close()
    return if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else ""
}


// Function to generate a random referral code
fun generateReferralCodeDriver(): String {
    val randomDigits = (100000..999999).random()
    return "MKV${randomDigits}D"
}
