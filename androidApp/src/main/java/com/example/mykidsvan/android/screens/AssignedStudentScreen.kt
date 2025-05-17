package com.example.mykidsvan.android.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AssignedStudentScreen(viewModel: AuthViewModel, userId: String?, userRole: String?) {
    val context = LocalContext.current
    val requests by viewModel.driverRequests.collectAsState()
    val isLoading = viewModel.isLoading
    val updatingId by viewModel.updatingRequestId.collectAsState()
    val toast = viewModel.toastMessage

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val updateMsg by viewModel.updateMessage.collectAsState()

    LaunchedEffect(updateMsg) {
        updateMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.clearUpdateMessage()
        }
    }
    // Load data on launch
    LaunchedEffect(Unit) {
        userId?.let { viewModel.loadDriverRequests(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Assigned Students",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (requests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No requests available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    requests.forEach { request ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = request.parentName?.replaceFirstChar { it.uppercase() } ?: "No Name",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "📍 ${request.parentAddress?.replaceFirstChar { it.uppercase() } ?: "No Address"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "📞 ${request.contactNumber ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Text(
                                    text = "* ${request.status?.replaceFirstChar { it.uppercase() } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Only show buttons when status is not accepted/cancelled
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Accept Button - Only show if not accepted
                                    if (!request.status.equals("accepted", ignoreCase = true)) {
                                        Button(
                                            onClick = {
                                                request.parentId?.let {
                                                    viewModel.updateRequestStatus(it, "ok")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                            enabled = updatingId != request.id,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            if (updatingId == request.id) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Text("Accept", color = Color.White)
                                            }
                                        }
                                    }

                                    // Reject Button - Only show if not cancelled
                                    if (!request.status.equals("rejected", ignoreCase = true)) {
                                        Button(
                                            onClick = {
                                                request.parentId?.let {
                                                    viewModel.updateRequestStatus(it, "cancel")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                            enabled = updatingId != request.id,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            if (updatingId == request.id) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Text("Reject", color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

