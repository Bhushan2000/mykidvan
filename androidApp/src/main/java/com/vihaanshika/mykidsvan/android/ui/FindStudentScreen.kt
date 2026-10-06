package com.vihaanshika.mykidsvan.android.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.data.dto.response.Parent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindStudentScreen(viewModel: AuthViewModel) {

    // Observe the list of states from the ViewModel
    val stateOptions by viewModel.stateOptions.collectAsState()
    val districtOptions by viewModel.districtOptions.collectAsState()
    val talukaOptions by viewModel.talukaOptions.collectAsState()
    val schoolOptions by viewModel.schoolOptions.collectAsState()

    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedTaluka by viewModel.selectedTaluka.collectAsState()
    val selectedSchool by viewModel.selectedSchool.collectAsState()

    val parentOptions by viewModel.parentsOptions.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()   // adds padding for status bar
                .padding(
                    top = padding.calculateTopPadding() + 48.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
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

            DropdownField(
                label = "School",
                selectedValue = selectedSchool?.schoolName ?: "",
                options = schoolOptions.map { it.schoolName },
                onValueChange = { selectedName ->
                    val school = schoolOptions.find { it.schoolName == selectedName }
                    school?.let { viewModel.onSchoolSelected(it) }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Button with Loading Indicator
            Button(
                onClick = {
                    if (selectedState == null || selectedDistrict == null ||
                        selectedTaluka == null || selectedSchool == null
                    ) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        selectedSchool!!.id?.let { viewModel.loadParentList(it) }
                        isLoading = false
                    }
                },
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
                    Text(text = "Search", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Driver List with Animation
            if (parentOptions.isNotEmpty()) {
                AnimatedVisibility(visible = parentOptions.isNotEmpty()) {
                    Column(
                        modifier = Modifier.animateContentSize()
                    ) {
                        parentOptions.forEach { parent ->
                            if (parent.id != null) {
                                StudentCard(
                                    parent = parent
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                            } else {
                                // Show this if no parent data is found
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "No Students Registered for This School",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Currently, no students are available for this school. Try again later or check a different school",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSchoolRegistrationDropDowns()
            viewModel.clearParentList()
        }
    }
}

@Composable
fun StudentCard(parent: Parent) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = parent.parentName ?: "Unknown Name",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = parent.parentAddress ?: "No Address Available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = parent.contactNumber ?: "No Contact Info",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = parent.pickUp ?: "No Pickup point",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "No of child: ${parent.numberOfChlid}" ?: "N/A",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Optional: Action Icons
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${parent.contactNumber}")
                    }
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Call, contentDescription = "Call Driver", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}


