package com.example.mykidsvan.android.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.mykidsvan.android.data.dto.response.Parent



@Composable
fun AssignedVehicleScreen(viewModel: AuthViewModel, userId: String?) {

    val schoolOptions by viewModel.schoolAllOptions.collectAsState()
    val selectedSchool by viewModel.selectedAllSchool.collectAsState()

    val isLoading by viewModel.isAssigningVehicle.collectAsState()
    val assignMessage by viewModel.assignVehicleMessage.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(assignMessage) {
        assignMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearVehicleAssignMessage()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // Dropdown
            DropdownField(
                label = "School",
                selectedValue = selectedSchool?.schoolName ?: "",
                options = schoolOptions.map { it.schoolName },
                onValueChange = { selectedName ->
                    schoolOptions.find { it.schoolName == selectedName }?.let {
                        viewModel.onAllSchoolSelected(it)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Assign Button
            Button(
                onClick = {
                    if (selectedSchool == null || userId.isNullOrEmpty()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("TAG", "AssignedVehicleScreen: user id - $userId  && school id - ${selectedSchool!!.id}")
                        viewModel.assignVehicleToParent(userId, selectedSchool!!.id ?: "")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Assign", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // observe the stateFlow message and show to user

            LaunchedEffect(assignMessage) {
                assignMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearAssignSchoolMessage()
                }
            }
        }
    }
}
















