package com.vihaanshika.mykidsvan.android.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.vihaanshika.mykidsvan.android.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
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
    val schoolOptions by viewModel.schoolOptions.collectAsState()

    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()
    val selectedSchool by viewModel.selectedSchool.collectAsState()

    // Observe success state based on role


    var showManualFields by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("TAG", "SchoolOnRegistrationScreen: uid -- $uid && userRole -- $userRole")
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Select school") })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
            DropdownField(
                label = "Select School",
                selectedValue = selectedSchool?.schoolName.orEmpty(),
                options = schoolOptions.map { it.schoolName },
                onValueChange = { name ->
                    schoolOptions.find { it.schoolName == name }
                        ?.let { viewModel.onSchoolSelected(it) }
                }
            )

            if (!selectedSchool?.schoolName.isNullOrEmpty() && selectedSchool?.schoolName.equals("Other")) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            onValueChange = { if (it.length <= 10) contactNumber.value = it },
                            placeholder = { Text("Enter contact number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors,
                            modifier = Modifier.fillMaxWidth()
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
                    }
                }
            }

            Button(
                onClick = {
                    val selectedStateName = selectedState?.id ?: ""
                    val selectedDistrictName = selectedDistrict?.id ?: ""
                    val selectedTalukaName = selectedTaluka?.id ?: ""
                    val selectedSchoolName = selectedSchool?.id ?: ""
                    if (selectedSchool?.schoolName.equals("Other") && (schoolName.value.isBlank() || selectedStateName.isBlank() ||
                                selectedDistrictName.isBlank() || selectedTalukaName.isBlank() ||
                                city.value.isBlank())
                    ) {
                        Toast.makeText(
                            context,
                            "Please fill all mandatory fields.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return@Button
                    }

                    isLoading = true

                    if (userRole?.lowercase() == "parent") {
                        if (!selectedSchool?.schoolName.equals("Other"))
                            viewModel.registerSchoolParent(
                                uid,
                                selectedSchoolName,
                                "", "", "", "", "", "", ""
                            )
                        else
                            viewModel.registerSchoolParent(
                                uid,
                                "",
                                schoolName.value,
                                contactNumber.value,
                                selectedStateName,
                                selectedDistrictName,
                                selectedTalukaName,
                                city.value,
                                schoolAddress.value
                            )
                    } else if (userRole?.lowercase() == "driver") {
                        if (!selectedSchool?.schoolName.equals("Other"))
                            viewModel.registerSchoolDriver(
                                uid,
                                selectedSchoolName,
                                "", "", "", "", "", "", ""
                            )
                        else
                            viewModel.registerSchoolDriver(
                                uid,
                                "",
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
                    Text(
                        if (userRole == "parent") "Submit" else "Next",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
         }
    }
    // Observe registration success based on role
//    LaunchedEffect(parentSuccess, driverSuccess) {
//        if ((userRole == "parent" && parentSuccess) || (userRole == "driver" && driverSuccess)) {
//            isLoading = false
//            schoolName.value = ""
//            contactNumber.value = ""
//            city.value = ""
//            schoolAddress.value = ""
//            viewModel.resetSchoolRegistrationDropDowns()
//            if (userRole == "parent") {
//                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
//
//                navController.navigate("login") {
//                    popUpTo("schoolOnRegistration") { inclusive = true }
//                }
//            } else {
//                navController.navigate("fileupload/$uid")
//            }
//
//            // subscribe the user here for fcm messaging
//            // ✅ Subscribe the parent to a topic
//            val topic = parentSuccess.topicUpdated.toString();
//            Firebase.messaging.subscribeToTopic(topic)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d("FCM", "User Subscribed to $topic successfully")
//                    } else {
//                        Log.e("FCM", "Subscription failed: ${task.exception}")
//                    }
//                }
//        } else if ((userRole == "parent" && !parentSuccess) || (userRole == "driver" && !driverSuccess)) {
//            isLoading = false
//        }
//    }

    val parentState by viewModel.schoolRegistrationPSuccess.collectAsState()
    val driverState by viewModel.schoolRegistrationDSuccess.collectAsState()

    LaunchedEffect(parentState, driverState) {
        val successResource = when (userRole) {
            "parent" -> parentState
            "driver" -> driverState
            else -> null
        }

        when (successResource) {
            is Resource.Success -> {
                isLoading = false
                schoolName.value = ""
                contactNumber.value = ""
                city.value = ""
                schoolAddress.value = ""

                viewModel.resetSchoolRegistrationDropDowns()

                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()

                if (userRole == "parent") {
                    navController.navigate("login") {
                        popUpTo("schoolOnRegistration") { inclusive = true }
                    }
                } else {
                    navController.navigate("fileupload/$uid")
                }

                // ✅ FCM Topic Subscription
                val topic = successResource.data.topic ?: ""
                if (topic.isNotBlank()) {
                    Firebase.messaging.subscribeToTopic(topic)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("FCM", "User Subscribed to $topic successfully")
                            } else {
                                Log.e("FCM", "Subscription failed: ${task.exception}")
                            }
                        }
                }
            }

            is Resource.Error -> {
                isLoading = false
                Toast.makeText(context, "Registration Failed: ${successResource.message}", Toast.LENGTH_SHORT).show()
            }

            else -> Unit // Loading state, do nothing
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSchoolRegistrationDropDowns()
            viewModel.resetParentRegistrationResult()
            viewModel.resetDriverRegistrationResult()
        }
    }
}


