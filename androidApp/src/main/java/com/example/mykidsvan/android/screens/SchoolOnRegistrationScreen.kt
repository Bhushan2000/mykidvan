package com.example.mykidsvan.android.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.dto.request.SchoolRegistrationRequest

@Composable
fun SchoolOnRegistrationScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    uid: String,
    userRole: String?
) {
    val context = LocalContext.current

    val schoolName = remember { mutableStateOf("") }
    val contactNumber = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val schoolAddress = remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        disabledBorderColor = Color.LightGray,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.Gray
    )

    val stateOptions by viewModel.stateOptions.collectAsState()
    val districtOptions by viewModel.districtOptions.collectAsState()
    val talukaOptions by viewModel.talukaOptions.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()

    // Observe success state based on role
    val parentSuccess by viewModel.schoolRegistrationPSuccess.collectAsState()
    val driverSuccess by viewModel.schoolRegistrationDSuccess.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("TAG", "SchoolOnRegistrationScreen: uid -- $uid && userRole -- $userRole")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = schoolName.value,
            onValueChange = { schoolName.value = it },
            placeholder = { Text("Enter school name") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contactNumber.value,
            onValueChange = { contactNumber.value = it },
            placeholder = { Text("Enter contact number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        DropdownField(
            label = "Select State",
            selectedValue = selectedState?.state_name ?: "",
            options = stateOptions.map { it.state_name },
            onValueChange = { selectedName ->
                stateOptions.find { it.state_name == selectedName }?.let {
                    viewModel.onStateSelected(it)
                }
            }
        )

        DropdownField(
            label = "Select District",
            selectedValue = selectedDistrict?.district_name ?: "",
            options = districtOptions.map { it.district_name },
            onValueChange = { selectedName ->
                districtOptions.find { it.district_name == selectedName }?.let {
                    viewModel.onDistrictSelected(it)
                }
            }
        )

        DropdownField(
            label = "Select Taluka",
            selectedValue = selectedTaluka?.taluka_name ?: "",
            options = talukaOptions.map { it.taluka_name },
            onValueChange = { selectedName ->
                talukaOptions.find { it.taluka_name == selectedName }?.let {
                    viewModel.onTalukaSelected(it)
                }
            }
        )

        OutlinedTextField(
            value = city.value,
            onValueChange = { city.value = it },
            placeholder = { Text("Enter city") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = schoolAddress.value,
            onValueChange = { schoolAddress.value = it },
            placeholder = { Text("Enter full address") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val selectedStateName = selectedState?.state_name ?: ""
                val selectedDistrictName = selectedDistrict?.district_name ?: ""
                val selectedTalukaName = selectedTaluka?.taluka_name ?: ""

                if (schoolName.value.isBlank() || selectedStateName.isBlank() ||
                    selectedDistrictName.isBlank() || selectedTalukaName.isBlank() ||
                    city.value.isBlank()
                ) {
                    Toast.makeText(context, "Please fill all mandatory fields.", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                isLoading = true

                if (userRole?.lowercase() == "parent") {
                    viewModel.registerSchoolParent(
                        uid.toInt(),
                        schoolName.value,
                        contactNumber.value,
                        selectedStateName,
                        selectedDistrictName,
                        selectedTalukaName,
                        city.value,
                        schoolAddress.value
                    )
                } else if (userRole?.lowercase() == "driver") {
                    viewModel.registerSchoolDriver(
                        uid.toInt(),
                        schoolName.value,
                        contactNumber.value,
                        selectedStateName,
                        selectedDistrictName,
                        selectedTalukaName,
                        city.value,
                        schoolAddress.value
                    )
                } else {
                    isLoading = false
                    Toast.makeText(context, "Unknown user role.", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Observe registration success based on role
    LaunchedEffect(parentSuccess, driverSuccess) {
        if ((userRole == "parent" && parentSuccess) || (userRole == "driver" && driverSuccess)) {
            isLoading = false
            schoolName.value = ""
            contactNumber.value = ""
            city.value = ""
            schoolAddress.value = ""
            viewModel.resetSchoolRegistrationDropDowns()
            Toast.makeText(context, "School Registration Successful!", Toast.LENGTH_SHORT).show()
            if (userRole == "parent")
                navController.navigate("login") {
                    popUpTo("schoolOnRegistration") { inclusive = true }
                }
            else {
                navController.navigate("fileupload/$uid")
            }

        } else if ((userRole == "parent" && !parentSuccess) || (userRole == "driver" && !driverSuccess)) {
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSchoolRegistrationDropDowns()
        }
    }
}
