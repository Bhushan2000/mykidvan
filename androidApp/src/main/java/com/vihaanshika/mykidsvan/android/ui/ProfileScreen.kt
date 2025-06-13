package com.vihaanshika.mykidsvan.android.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverMob
import com.vihaanshika.mykidsvan.android.data.dto.response.Parent
import com.vihaanshika.mykidsvan.android.utils.APIEndpoints
import com.vihaanshika.mykidsvan.android.utils.Constants
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(viewModel: AuthViewModel, userId: String, userType: String) {
    val context = LocalContext.current
    val profileData by viewModel.profileData.collectAsState()
    var visible by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // Editable fields
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var childName by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }

    var mobileNumber by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var taluka by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    val isUpdating by viewModel.isProfileUpdating.collectAsState()
    val updateMessage by viewModel.updateMessage.collectAsState()

    val stateOptions by viewModel.stateOptions.collectAsState()
    val districtOptions by viewModel.districtOptions.collectAsState()
    val talukaOptions by viewModel.talukaOptions.collectAsState()
    val schoolOptions by viewModel.schoolOptions.collectAsState()

    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()
    val selectedSchool by viewModel.selectedSchool.collectAsState()


    // Gallery launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile(userId, userType)
    }

    LaunchedEffect(profileData) {
        try {
            delay(300)
            visible = true

            profileData?.let { data ->
                when (data) {
                    is Parent -> {
                        name = data.parentName.orEmpty()
                        contact = data.contactNumber.orEmpty()
                        address = data.parentAddress.orEmpty()
                        childName = data.childName.orEmpty()
                        state = data.state.orEmpty()
                        district = data.district.orEmpty()
                        taluka = data.taluka.orEmpty()
                        city = data.city.orEmpty()
                        schoolName = data.schoolId.orEmpty()
                    }

                    is DriverMob -> {
                        name = data.driverName.orEmpty()
                        mobileNumber = data.number.orEmpty()
                        vehicleNumber = data.vehicleNumber.orEmpty()
                        state = data.state.orEmpty()
                        district = data.district.orEmpty()
                        taluka = data.taluka.orEmpty()
                        city = data.city.orEmpty()
                        schoolName = data.schoolId.orEmpty()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "ProfileScreen: $e")
        }

    }
    LaunchedEffect(stateOptions, districtOptions, talukaOptions, schoolOptions) {
        if (stateOptions.isNotEmpty() && selectedState == null) {
            stateOptions.find { it.state_name == state }?.let {
                viewModel.onStateSelected(it)
            }
        }

        if (districtOptions.isNotEmpty() && selectedDistrict == null) {
            districtOptions.find { it.district_name == district }?.let {
                viewModel.onDistrictSelected(it)
            }
        }

        if (talukaOptions.isNotEmpty() && selectedTaluka == null) {
            talukaOptions.find { it.taluka_name == taluka }?.let {
                viewModel.onTalukaSelected(it)
            }
        }

        if (schoolOptions.isNotEmpty() && selectedSchool == null) {
            schoolOptions.find { it.schoolName == schoolName || it.id.equals(schoolName) }?.let {
                viewModel.onSchoolSelected(it)
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            profileData?.let { data ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(700)) + scaleIn(initialScale = 0.8f),
                            exit = fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.TopEnd) {
                                    ProfileImage(
                                        imageUrl = when (data) {
                                            is DriverMob -> profileImageUri?.toString()
                                                ?: "${APIEndpoints.BASE_URL}${data.profilePicture}"

                                            is Parent -> profileImageUri?.toString()
                                                ?: "${APIEndpoints.BASE_URL}${data.profilePicture}"

                                            else -> profileImageUri?.toString() ?: ""
                                        }
                                    )

                                    if (isEditing) {
                                        IconButton(
                                            onClick = {
                                                imagePickerLauncher.launch("image/*")
                                            },
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .size(32.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Image",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = name.capitalize(),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${userType.capitalize()} Profile",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (isEditing) {
                                            val selectedStateName =
                                                selectedState?.id ?: ""
                                            val selectedDistrictName =
                                                selectedDistrict?.id ?: ""
                                            val selectedTalukaName =
                                                selectedTaluka?.id ?: ""
                                            val selectedSchoolName =
                                                selectedSchool?.id ?: ""
                                            // Save logic - you can pass all fields and imageUri to ViewModel
                                            viewModel.updateProfile(
                                                context = context,
                                                userId = userId,
                                                userType = userType,
                                                name = name,
                                                contact = contact,
                                                address = address,
                                                childName = childName,
                                                schoolName = selectedSchoolName,
                                                mobile = mobileNumber,
                                                vehicle = vehicleNumber,
                                                state = selectedStateName,
                                                district = selectedDistrictName,
                                                taluka = selectedTalukaName,
                                                city = city,
                                                imageUri = profileImageUri
                                            )
                                        }
                                        isEditing = !isEditing
                                    }
                                ) {
                                    Text(if (isEditing) "Save" else "Edit", color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(1000)),
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    when (data) {
                                        is Parent -> {
                                            if (isEditing) {
                                                EditableField(true, "Parent Name", name) {
                                                    name = it
                                                }
                                                EditableField(true, "Contact Number", contact) {
                                                    contact = it
                                                }
                                                EditableField(true, "Address", address) {
                                                    address = it
                                                }
                                                EditableField(true, "Child Name", childName) {
                                                    childName = it
                                                }
                                                /*  EditableField(
                                                      "School Name",
                                                      schoolName
                                                  ) { schoolName = it }*/

                                                DropdownField(
                                                    label = "Select State",
                                                    selectedValue = selectedState?.state_name ?: "",
                                                    options = stateOptions.map { it.state_name },
                                                    onValueChange = { selectedName ->
                                                        stateOptions.find { it.state_name == selectedName }
                                                            ?.let {
                                                                viewModel.onStateSelected(it)
                                                            }
                                                    }
                                                )

                                                DropdownField(
                                                    label = "Select District",
                                                    selectedValue = selectedDistrict?.district_name
                                                        ?: "",
                                                    options = districtOptions.map { it.district_name },
                                                    onValueChange = { selectedName ->
                                                        districtOptions.find { it.district_name == selectedName }
                                                            ?.let {
                                                                viewModel.onDistrictSelected(it)
                                                            }
                                                    }
                                                )

                                                DropdownField(
                                                    label = "Select Taluka",
                                                    selectedValue = selectedTaluka?.taluka_name
                                                        ?: "",
                                                    options = talukaOptions.map { it.taluka_name },
                                                    onValueChange = { selectedName ->
                                                        talukaOptions.find { it.taluka_name == selectedName }
                                                            ?.let {
                                                                viewModel.onTalukaSelected(it)
                                                            }
                                                    }
                                                )

                                                /*          DropdownField(
                                                              label = "Select School",
                                                              selectedValue = selectedSchool?.schoolName.orEmpty(),
                                                              options = schoolOptions.map { it.schoolName },
                                                              onValueChange = { name ->
                                                                  schoolOptions.find { it.schoolName == name }
                                                                      ?.let {
                                                                          viewModel.onSchoolSelected(it)
                                                                      }
                                                              }
                                                          )*/

                                                EditableField(
                                                    false,
                                                    "School Name",
                                                    schoolName
                                                ) { schoolName = it }

                                            } else {
                                                ProfileDetailRow("Parent Name", name)
                                                ProfileDetailRow("Contact Number", contact)
                                                ProfileDetailRow("Address", address)
                                                ProfileDetailRow("Child Name", childName)
                                                ProfileDetailRow("School Name", schoolName)
                                            }
                                        }

                                        is DriverMob -> {
                                            if (isEditing) {
                                                EditableField(true, "Driver Name", name) {
                                                    name = it
                                                }
                                                EditableField(
                                                    true,
                                                    "Mobile Number",
                                                    mobileNumber
                                                ) { mobileNumber = it }
                                                EditableField(
                                                    true,
                                                    "Vehicle Number",
                                                    vehicleNumber
                                                ) { vehicleNumber = it }
                                                /*EditableField("State", state) { state = it }
                                                EditableField("District", district) { district = it }
                                                EditableField("Taluka", taluka) { taluka = it }*/
                                                DropdownField(
                                                    label = "Select State",
                                                    selectedValue = selectedState?.state_name ?: "",
                                                    options = stateOptions.map { it.state_name },
                                                    onValueChange = { selectedName ->
                                                        stateOptions.find { it.state_name == selectedName }
                                                            ?.let {
                                                                viewModel.onStateSelected(it)
                                                            }
                                                    }
                                                )

                                                DropdownField(
                                                    label = "Select District",
                                                    selectedValue = selectedDistrict?.district_name
                                                        ?: "",
                                                    options = districtOptions.map { it.district_name },
                                                    onValueChange = { selectedName ->
                                                        districtOptions.find { it.district_name == selectedName }
                                                            ?.let {
                                                                viewModel.onDistrictSelected(it)
                                                            }
                                                    }
                                                )

                                                DropdownField(
                                                    label = "Select Taluka",
                                                    selectedValue = selectedTaluka?.taluka_name
                                                        ?: "",
                                                    options = talukaOptions.map { it.taluka_name },
                                                    onValueChange = { selectedName ->
                                                        talukaOptions.find { it.taluka_name == selectedName }
                                                            ?.let {
                                                                viewModel.onTalukaSelected(it)
                                                            }
                                                    }
                                                )

//                                                DropdownField(
//                                                    label = "Select School",
//                                                    selectedValue = selectedSchool?.schoolName.orEmpty(),
//                                                    options = schoolOptions.map { it.schoolName },
//                                                    onValueChange = { name ->
//                                                        schoolOptions.find { it.schoolName == name }
//                                                            ?.let {
//                                                                viewModel.onSchoolSelected(it)
//                                                            }
//                                                    }
//                                                )
                                                EditableField(
                                                    false,
                                                    "School Name",
                                                    schoolName
                                                ) { schoolName = it }
                                                EditableField(true, "City", city) { city = it }
                                            } else {
                                                ProfileDetailRow("Driver Name", name)
                                                ProfileDetailRow("Mobile Number", mobileNumber)
                                                ProfileDetailRow("Vehicle Number", vehicleNumber)
                                                ProfileDetailRow("State", state)
                                                ProfileDetailRow("District", district)
                                                ProfileDetailRow("Taluka", taluka)
                                                ProfileDetailRow("School", schoolName)
                                                ProfileDetailRow("City", city)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: Text(
                text = "No Data Available",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.error
                ),
                textAlign = TextAlign.Center
            )
        }
        // Show toast if update message is available
        LaunchedEffect(updateMessage) {
            updateMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
fun EditableField(
    enabled: Boolean = true,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled, // <-- use it here
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
fun ProfileImage(imageUrl: String?) {
    val transition = rememberInfiniteTransition(label = "profile_image")
    val scale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "image_scale"
    )

    if (imageUrl != null && imageUrl.startsWith("data:image/png;base64,")) {
        val imageBytes = Base64.decode(imageUrl.substringAfter("base64,"), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(140.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    } else {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(140.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

@Composable
fun ProfileDetailRow(title: String, value: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title.uppercase(), // Optional: uppercase for better distinction
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = value ?: "N/A",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        )
    }
}

