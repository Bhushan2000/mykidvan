package com.vihaanshika.mykidsvan.android.ui

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.R
import com.vihaanshika.mykidsvan.android.data.dto.response.GetClassesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.OtpResponse
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.OtpState
import com.vihaanshika.mykidsvan.android.utils.Resource
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentSignupScreen(
    navController: NavController, viewModel: AuthViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val parentName = remember { mutableStateOf("") }
    val state = remember { mutableStateOf("") }
    val district = remember { mutableStateOf("") }
    val taluka = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val childName = remember { mutableStateOf("") }
    val childClass = remember { mutableStateOf("") }
    val schoolName = remember { mutableStateOf("") }
    val childDateOfBirth = remember { mutableStateOf("") }
    val pickupLocation = remember { mutableStateOf("") }
    val dropOffLocation = remember { mutableStateOf("") }
    val numberOfChildren = remember { mutableStateOf("") }
    val numberOfChildList = listOf("1", "2", "3", "4")
    val childClassList = remember { mutableStateListOf<String>() }

    /*  val childClassList =
          listOf("Nursery", "KG-I", "KG-II", "1st std", "2nd std", "3rd std", "4th std", "5th std")*/
    val emergencyContact = remember { mutableStateOf("") }
    val termsAccepted = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }  // Loading state for progress bar

    // for no of child
    var expanded by remember { mutableStateOf(false) }
    // for child class
    var expandedChildren by remember { mutableStateOf(false) }


    val stateOptions by viewModel.stateOptions.collectAsState()
    val districtOptions by viewModel.districtOptions.collectAsState()
    val talukaOptions by viewModel.talukaOptions.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()

    // Referral Code States
    val referralCode = remember { mutableStateOf(generateReferralCodP()) }
    val enteredReferralCode = remember { mutableStateOf("") }
    val isReferralCodeApplied = remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        disabledBorderColor = Color.LightGray,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.Gray
    )

    var contactNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(0) }
    var isVerified by remember { mutableStateOf(false) }
    var showOtpField by remember { mutableStateOf(false) }

    var isVerifying by remember { mutableStateOf(false) }

    val sendOtpState = viewModel.sendOtpState.collectAsState().value
    val verifyOtpState = viewModel.verifyOtp.collectAsState().value
    val getClasses = viewModel.getClassDetails.collectAsState().value
    // Show OTP field and start timer on success message

    LaunchedEffect(Unit) {
        viewModel.loadStateOptions()
        viewModel.getClasses()
    }

    LaunchedEffect(sendOtpState) {
        if (sendOtpState is Resource.Success) {
            val msg = sendOtpState.data.toString()
            if (msg.contains("sent", ignoreCase = true)) {
                showOtpField = true
                timer = 60
                while (timer > 0 && !isVerified) {
                    delay(1000)
                    timer--
                }
            }
        } else if (sendOtpState is Resource.Error) {
            Toast.makeText(context, sendOtpState.message, Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-verify OTP
    LaunchedEffect(otp) {
        if (
            otp.length == 4 &&
            !isVerified &&
            !isVerifying &&
            verifyOtpState !is Resource.Loading
        ) {
            isVerifying = true
            viewModel.verifyOtp(contactNumber, otp)

            // Enforce 5 seconds cooldown
            delay(5000)
            isVerifying = false
        }
    }

    // Handle verification success
    LaunchedEffect(verifyOtpState) {
        when (verifyOtpState) {
            is Resource.Success -> {
                isVerified = true
                showOtpField = false
                isVerifying = false
            }

            is Resource.Error -> {
                Toast.makeText(context, verifyOtpState.message, Toast.LENGTH_SHORT).show()
                isVerifying = false
            }

            is Resource.Loading -> {
                // keep `isVerifying = true`
            }

            is Resource.Idle -> {
                isVerifying = false
            }
        }
    }

    LaunchedEffect(getClasses) {
        when (getClasses) {
            is Resource.Loading -> {
                // Show loading if needed
                Log.d("TAG", "ParentSignupScreen: classes Loading..")
            }

            is Resource.Success -> {
                // Hardcoded list when API success
                val data = getClasses.data
                childClassList.addAll(data.data.mapNotNull { it.className } ?: emptyList())
                Log.d("TAG", "ParentSignupScreen: classes loaded..${childClassList.toList()}")
            }

            is Resource.Idle -> {
                // Optional
            }

            is Resource.Error -> {
                // Handle error
                Log.d("TAG", "ParentSignupScreen: error while getting classes")
            }
        }
    }

    val otpUiState = when (sendOtpState) {
        is Resource.Loading -> OtpState(isLoading = true)
        is Resource.Success -> OtpState(message = (sendOtpState as Resource.Success<OtpResponse>).data.message)
        is Resource.Error -> OtpState(error = (sendOtpState as Resource.Error).message)
        else -> OtpState()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearClasses()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Parents Registration Form") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = parentName.value,
                onValueChange = { parentName.value = it },
                label = { Text("Parent's Name") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

//            OutlinedTextField(
//                value = contactNumber.value,
//                onValueChange = { if (it.length <= 10) contactNumber.value = it },
//                label = { Text("Contact Number") },
//                shape = RoundedCornerShape(14.dp),
//                colors = textFieldColors,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(64.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
//            )

            ContactWithOtpSection(
                color = textFieldColors,
                contactNumber = contactNumber,
                onContactChange = { contactNumber = it },
                otp = otp,
                onOtpChange = { otp = it },
                timerSeconds = timer,
                showOtpField = showOtpField,
                isVerified = isVerified,
                otpState = otpUiState,  // ✅ Fixed
                onSendOtp = { viewModel.sendOtp(it, Constants.REGISTER_OTP) },
                onVerifyOtp = { number, code -> viewModel.verifyOtp(number, code) },
                onResendOtp = {
                    viewModel.resendOtp(it, Constants.REGISTER_OTP)
                    timer = 60
                }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { if (it.length <= 8) password = it },
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
                value = city.value,
                onValueChange = { city.value = it },
                label = { Text("City") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
            OutlinedTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = { Text("Parent's Address") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
            OutlinedTextField(
                value = childName.value,
                onValueChange = { childName.value = it },
                label = { Text("Child's Name") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedChildren,
                onExpandedChange = { expandedChildren = !expandedChildren }
            ) {
                OutlinedTextField(
                    value = childClass.value,
                    onValueChange = {},
                    shape = RoundedCornerShape(14.dp),
                    readOnly = true,
                    label = { Text("Child Class") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedChildren) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedChildren,
                    onDismissRequest = { expandedChildren = false }
                ) {
                    childClassList.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                childClass.value = type
                                expandedChildren = false
                            }
                        )
                    }
                }
            }


            /*            OutlinedTextField(
                            value = schoolName.value,
                            onValueChange = { schoolName.value = it },
                            label = { Text("Child's School Name") },
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        )*/
            OutlinedTextField(
                value = childDateOfBirth.value,
                onValueChange = {}, // Prevent manual input
                readOnly = true,     // Prevent keyboard and focus
                label = { Text("Child's DOB") },
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            showDatePickerDialog(context, calendar) {
                                childDateOfBirth.value = it
                            }
                        }
                    )
                },
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable {
                        showDatePickerDialog(context, calendar) {
                            childDateOfBirth.value = it
                        }
                    }
            )

            OutlinedTextField(
                value = pickupLocation.value,
                onValueChange = { pickupLocation.value = it },
                label = { Text("Pickup Location") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = numberOfChildren.value,
                    onValueChange = {},
                    shape = RoundedCornerShape(14.dp),
                    readOnly = true,
                    label = { Text("Number of Children") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    numberOfChildList.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                numberOfChildren.value = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = emergencyContact.value,
                onValueChange = { if (it.length <= 10) emergencyContact.value = it },
                label = { Text("Emergency Contact Number") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )
            // Referral Code UI

            /*          Text(
                          "Your Referral Code: ${referralCode.value}",
                          fontSize = 16.sp,
                          fontWeight = FontWeight.Bold
                      )
          */

            // Row for Referral Code input and Apply button
            ReferralRow(
                viewModel = viewModel,
                enteredReferralCode = enteredReferralCode,
                isReferralCodeApplied = isReferralCodeApplied
            )

            /*            Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = termsAccepted.value,
                                onCheckedChange = { termsAccepted.value = it })

                            Spacer(modifier = Modifier.width(8.dp)) // Horizontal space between checkbox and text

                            Text(
                                text = "I agree to the Terms & Conditions",
                                modifier = Modifier.weight(1f) // Push the text if needed or adjust for larger space.
                            )
                        }*/
            Spacer(modifier = Modifier.height(8.dp))

            TermsAndPrivacyRow(
                termsAccepted = termsAccepted.value,
                onCheckedChange = { termsAccepted.value = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (parentName.value.isBlank() || contactNumber.isBlank() || !termsAccepted.value) {
                        Toast.makeText(
                            context,
                            "Please fill mandatory fields and accept the terms.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        isLoading = true  // Start loading when the button is clicked
                        viewModel.registerParent(
                            parentName.value,
                            contactNumber,
                            password,
                            selectedState?.id ?: "",
                            selectedDistrict?.id ?: "",
                            selectedTaluka?.id ?: "",
                            city.value,
                            address.value,
                            childName.value,
                            childClass.value,
                            schoolName.value,
                            childDateOfBirth.value,
                            pickupLocation.value,
                            dropOffLocation.value,
                            numberOfChildren.value,
                            emergencyContact.value,
                            termsAccepted.value.toString(),
                            referralCode.value,
                            enteredReferralCode.value
                        )
                    }
                }, enabled = !isLoading,  // Disable button when loading to prevent multiple clicks
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),  // Centered within button space
                        color = Color.White, strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Next", color = Color.White)
                }
            }

            // Collecting registration success or failure state
            val registrationSuccess by viewModel.parentRegistrationSuccess.collectAsState()

            LaunchedEffect(registrationSuccess?.status) {
                registrationSuccess?.let { result ->
                    isLoading = false  // Always stop loading when we get a result
                    if (result.status == true) {
                        // Reset form fields
                        parentName.value = ""
                        contactNumber = ""
                        state.value = ""
                        district.value = ""
                        taluka.value = ""
                        city.value = ""
                        address.value = ""
                        childName.value = ""
                        childClass.value = ""
                        schoolName.value = ""
                        childDateOfBirth.value = ""
                        pickupLocation.value = ""
                        dropOffLocation.value = ""
                        numberOfChildren.value = ""
                        emergencyContact.value = ""
                        termsAccepted.value = false

                        Toast.makeText(
                            context,
                            "Parent Registration Successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate to next screen
                        val uid = result.id.toString()
                        val role = "parent"
                        navController.navigate("schoolOnRegistration/$uid/$role")
                    } else {
                        // 🔁 Reset the result so next click can trigger this again
                        viewModel.resetDriverRegistrationResult()
                    }
                }
            }


        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSchoolRegistrationDropDowns()
            viewModel.resetDriverRegistrationResult()
            viewModel.resetSendOtpState()
//          reset the fields
// ...................................
            contactNumber = ""
            otp = ""
            timer = 0
            isVerified = false
            showOtpField = false
        }
    }
}

// Function to generate a random referral code
fun generateReferralCodP(): String {
    val randomDigits = (100000..999999).random()
    return "MKV${randomDigits}P"
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
