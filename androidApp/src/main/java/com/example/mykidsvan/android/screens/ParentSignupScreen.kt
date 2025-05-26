package com.example.mykidsvan.android.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.R
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
    val contactNumber = remember { mutableStateOf("") }
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
    val childClassList =
        listOf("Nursery", "KG-I", "KG-II", "1st std", "2nd std", "3rd std", "4th std", "5th std")
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
            OutlinedTextField(
                value = contactNumber.value,
                onValueChange = { contactNumber.value = it },
                label = { Text("Contact Number") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
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


            OutlinedTextField(
                value = schoolName.value,
                onValueChange = { schoolName.value = it },
                label = { Text("Child's School Name") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
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
                onValueChange = { emergencyContact.value = it },
                label = { Text("Emergency Contact Number") },
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )
            // Referral Code UI
            Spacer(modifier = Modifier.height(16.dp))

            /*          Text(
                          "Your Referral Code: ${referralCode.value}",
                          fontSize = 16.sp,
                          fontWeight = FontWeight.Bold
                      )
          */
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
                        .padding(start = 8.dp), // Optional, to add some spacing between elements
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text(text = "Verify", color = Color.White)
                }
            }


            Row(
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
            }


            Button(
                onClick = {
                    if (parentName.value.isBlank() || contactNumber.value.isBlank() || !termsAccepted.value) {
                        Toast.makeText(
                            context,
                            "Please fill mandatory fields and accept the terms.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        isLoading = true  // Start loading when the button is clicked
                        viewModel.registerParent(
                            parentName.value,
                            contactNumber.value,
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
                        contactNumber.value = ""
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

                        Toast.makeText(context, "Parent Registration Successful!", Toast.LENGTH_SHORT).show()

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
