package com.example.mykidsvan.android.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.dto.request.SchoolRegistrationRequest

@Composable
fun SchoolRegistrationScreen(viewModel: AuthViewModel) {
    val context = LocalContext.current

    // State variables with remember
    val schoolName = remember { mutableStateOf("") }
    val contactNumber = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val schoolAddress = remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }  // Loading state for progress bar

    // Scrollable modifier
    val scrollState = rememberScrollState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        disabledBorderColor = Color.LightGray,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.Gray
    )

    // Dropdown state
    val stateOptions by viewModel.stateOptions.collectAsState()
    val districtOptions by viewModel.districtOptions.collectAsState()
    val talukaOptions by viewModel.talukaOptions.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),  // Outer padding
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = schoolName.value,
            onValueChange = { schoolName.value = it },
            label = { Text("School Name") },
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)        )

        OutlinedTextField(
            value = contactNumber.value,
            onValueChange = { contactNumber.value = it },
            label = { Text("Contact Number") },
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        DropdownField(
            label = "* State",
            selectedValue = selectedState?.state_name ?: "",
            options = stateOptions.map { it.state_name },
            onValueChange = { selectedName ->
                val state = stateOptions.find { it.state_name == selectedName }
                state?.let { viewModel.onStateSelected(it) }
            }
        )

        DropdownField(
            label = "* District",
            selectedValue = selectedDistrict?.district_name ?: "",
            options = districtOptions.map { it.district_name },
            onValueChange = { selectedName ->
                val district = districtOptions.find { it.district_name == selectedName }
                district?.let { viewModel.onDistrictSelected(it) }
            }
        )

        DropdownField(
            label = "* Taluka",
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
            label = { Text("* City") },
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        OutlinedTextField(
            value = schoolAddress.value,
            onValueChange = { schoolAddress.value = it },
            label = { Text("School Address") },
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                val selectedStateName = selectedState?.state_name ?: ""
                val selectedDistrictName = selectedDistrict?.district_name ?: ""
                val selectedTalukaName = selectedTaluka?.taluka_name ?: ""

                Log.d("TAG", "SchoolRegistrationScreen schoolName: ${schoolName.value}")
                Log.d("TAG", "SchoolRegistrationScreen contactNumber: ${contactNumber.value}")
                Log.d("TAG", "SchoolRegistrationScreen state: $selectedStateName")
                Log.d("TAG", "SchoolRegistrationScreen district: $selectedDistrictName")
                Log.d("TAG", "SchoolRegistrationScreen taluka: $selectedTalukaName")
                Log.d("TAG", "SchoolRegistrationScreen city: ${city.value}")
                Log.d("TAG", "SchoolRegistrationScreen schoolAddress: ${schoolAddress.value}")

                if (schoolName.value.isBlank() || contactNumber.value.isBlank() ||
                    selectedStateName.isBlank() || selectedDistrictName.isBlank() ||
                    selectedTalukaName.isBlank() || city.value.isBlank() ||
                    schoolAddress.value.isBlank()
                ) {
                    Toast.makeText(
                        context,
                        "Please fill mandatory fields.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    isLoading = true  // Start loading when the button is clicked
                    val request = SchoolRegistrationRequest(
                        school_name = schoolName.value,
                        contact_number = contactNumber.value,
                        selectedState?.id ?: "",
                        selectedDistrict?.id ?: "",
                        selectedTaluka?.id ?: "",
                        city = city.value,
                        school_address = schoolAddress.value
                    )
                    viewModel.registerSchool(request)
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
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Submit", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Observe success/failure and reset form on success
        val schoolRegistrationSuccess by viewModel.schoolRegistrationSuccess.collectAsState()
        LaunchedEffect(schoolRegistrationSuccess) {
            if (schoolRegistrationSuccess) {
                isLoading = false  // Stop loading when response is received

                // Reset form fields on success
                schoolName.value = ""
                contactNumber.value = ""
                city.value = ""
                schoolAddress.value = ""

                // Reset dropdowns
                viewModel.resetSchoolRegistrationDropDowns()

                Toast.makeText(context, "School Registration Successful!", Toast.LENGTH_SHORT).show()
            } else {
                isLoading = false  // Stop loading on failure as well
            }
        }
    }
}
