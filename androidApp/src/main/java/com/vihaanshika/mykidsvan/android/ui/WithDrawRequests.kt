package com.vihaanshika.mykidsvan.android.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.data.dto.request.WithdrawRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.CommissionParentResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.CommissionResponse
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithDrawRequests(viewModel: AuthViewModel, userId: String, role: String) {
    val context = LocalContext.current

    val showSheet = remember { mutableStateOf(false) }
    val withdrawAmount = remember { mutableStateOf("") }
    val bankName = remember { mutableStateOf("") }
    val ifscCode = remember { mutableStateOf("") }
    val accountNumber = remember { mutableStateOf("") }
    val upiId = remember { mutableStateOf("") }
    // Simulated values for now:
    var totalEarnedAmount = remember { mutableStateOf("0.0") }
    var totalWithdrawnAmount = remember { mutableStateOf("0.0") }
    var remainingAmount = remember { mutableStateOf("0.0") }
    val withdrawState by viewModel.withdraw.collectAsState()
    val isLoading = withdrawState is Resource.Loading
    val commissionState by viewModel.commissionState.collectAsState()

    // Fetch commission based on user role once
    LaunchedEffect(Unit) {
//        val driverId = if (viewModel.userRole.value == Constants.USER_PARENT) {
//            viewModel.assignedVehicleId.value.toString()
//        } else {
//            viewModel.userId.value.toString()
//        }
        viewModel.getCommission(userId, userRole = role)
    }

    LaunchedEffect(withdrawState) {
        when (withdrawState) {
            is Resource.Success -> {
                val message =
                    (withdrawState as Resource.Success).data?.message ?: "Withdrawal successful"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetWithdrawRequest()
                withdrawAmount.value = ""
                bankName.value = ""
                ifscCode.value = ""
                accountNumber.value = ""
                upiId.value = ""
                showSheet.value = false
                viewModel.getCommission(userId, userRole = role)
                viewModel.withdrawRequestStatus(userId, role)
            }

            is Resource.Error -> {
                val message = (withdrawState as Resource.Error).message ?: "Something went wrong"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetWithdrawRequest()
            }

            else -> {}
        }
    }

    LaunchedEffect(commissionState) {
        when (val state = commissionState) {
            is Resource.Success -> {
                when (val data = state.data) {
                    is CommissionResponse -> {
                        val commissions = data.data
                        totalEarnedAmount.value = commissions.amount ?: "0.0"
                        totalWithdrawnAmount.value = commissions.totalWithdrawn ?: "0.0"
                        remainingAmount.value = commissions.remaining ?: "0.0"
                        Log.d("TAG", "Role Driver - ${totalEarnedAmount.value}  ${totalWithdrawnAmount.value}  ${remainingAmount.value}")
                    }

                    is CommissionParentResponse -> {
                        val commissionsP = data.data
                        totalEarnedAmount.value = commissionsP.commission ?: "0.0"
                        totalWithdrawnAmount.value = commissionsP.totalWithdrawn ?: "0.0"
                        remainingAmount.value = commissionsP.remaining ?: "0.0"
                        Log.d("TAG", "Role Parent - ${totalEarnedAmount.value}  ${totalWithdrawnAmount.value}  ${remainingAmount.value}")
                    }

                    else -> {
                        Log.e("TAG", "Unknown response type: ${data?.javaClass?.name}")
                    }
                }
            }

            is Resource.Error -> {
                Log.d("TAG", "ReferAppScreen: Error while showing")
            }

            is Resource.Idle<*> -> {}
            is Resource.Loading<*> -> {}
        }
    }

    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(top = 80.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val withdrawStatus by viewModel.withdrawStatus.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.withdrawRequestStatus(userId = userId, role = role)
            }
            // Commission Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Ensures full width for proper horizontal centering
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Centers content horizontally
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Commission Summary",
                        color = Color(0xFF1565C0),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Total Earned: ${totalEarnedAmount.value}", color = Color(0xFF2E7D32))
                    Text("Withdrawn: ${totalWithdrawnAmount.value}", color = Color(0xFFF57C00))
                    Text(
                        "Remaining: ${remainingAmount.value}",
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = { showSheet.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text("Withdraw Commission", color = Color.White)
            }
            if (showSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet.value = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .verticalScroll(scrollState)
                            .imePadding() // 👈 adds padding when keyboard is visible
                            .padding(bottom = 32.dp) // 👈 manual bottom padding for Submit button spacing
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Withdraw Commission",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        val fieldModifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = withdrawAmount.value,
                            onValueChange = { withdrawAmount.value = it },
                            label = { Text("Withdraw Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )

                        OutlinedTextField(
                            value = bankName.value,
                            onValueChange = { bankName.value = it },
                            label = { Text("Bank Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )

                        OutlinedTextField(
                            value = ifscCode.value,
                            onValueChange = { ifscCode.value = it },
                            label = { Text("IFSC Code") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )

                        OutlinedTextField(
                            value = accountNumber.value,
                            onValueChange = { accountNumber.value = it },
                            label = { Text("Account Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("OR", modifier = Modifier.align(Alignment.CenterHorizontally))

                        OutlinedTextField(
                            value = upiId.value,
                            onValueChange = { upiId.value = it },
                            label = { Text("UPI ID (Optional)") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (withdrawAmount.value.toInt() <= remainingAmount.value.toInt()) {
                                    val request = WithdrawRequest(
                                        userId,
                                        role,
                                        withdrawAmount.value,
                                        bankName.value,
                                        ifscCode.value,
                                        accountNumber.value,
                                        upiId.value
                                    )
                                    viewModel.withdrawRequest(request)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Withdraw amount should be less than Remaining amount",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                // Don't dismiss yet until success
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White, modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("Submit", color = Color.White)
                            }
                        }
                    }
                }
            }

            WithdrawRequestList(
                withdrawStatus = withdrawStatus,
                onRetry = { viewModel.withdrawRequestStatus(userId = userId, role = role) }
            )
        }
    }
}

