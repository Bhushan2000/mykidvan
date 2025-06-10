package com.vihaanshika.mykidsvan.android.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.data.dto.response.OtpVerificationResponse
import com.vihaanshika.mykidsvan.android.utils.Resource
import kotlinx.coroutines.delay

@Composable
fun OTPVerificationScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    phoneNumber: String
) {
    val verifyOtpState by viewModel.verifyOtp.collectAsState()
    val otpLength = 4
    val otpValues = remember { List(otpLength) { mutableStateOf("") } }
    val context = LocalContext.current

    // Timer State: 30-second countdown for resend button
    val initialTimerValue = 30
    var timerValue by remember { mutableStateOf(initialTimerValue) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // Timer countdown logic
    LaunchedEffect(isTimerRunning) {
        while (timerValue > 0 && isTimerRunning) {
            delay(1000L)
            timerValue--
        }
        isTimerRunning = false
    }

    // Handle success side effects
    LaunchedEffect(verifyOtpState) {
        when (verifyOtpState) {
            is Resource.Success -> {
                val message =
                    (verifyOtpState as Resource.Success<OtpVerificationResponse>).data.message
                Toast.makeText(context, message ?: "OTP verified!", Toast.LENGTH_SHORT).show()
                navController.navigate("update_password/$phoneNumber")
                viewModel.resetVerifyOtpState()
            }

            is Resource.Error -> {
                val error = (verifyOtpState as Resource.Error).message
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                viewModel.resetVerifyOtpState()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "OTP Verification",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "An authentication code has been sent to $phoneNumber")

        Spacer(modifier = Modifier.height(16.dp))

        OTPTextField(otpValues)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("I didn't receive code.", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            if (isTimerRunning) {
                Text(
                    "Resend Code (${timerValue}s)",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    "Resend Code",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        viewModel.resendOtp(phoneNumber)
                        timerValue = initialTimerValue
                        isTimerRunning = true
                        Toast.makeText(context, "OTP resent", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (verifyOtpState is Resource.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val otpEntered = otpValues.joinToString("") { it.value }
                    if (otpEntered.length == otpLength) {
                        viewModel.verifyOtp(phoneNumber, otpEntered)
                    } else {
                        Toast.makeText(context, "Please enter the complete OTP", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Verify Now", color = Color.White)
            }
        }
    }
}


@Composable
fun OTPTextField(otpValues: List<MutableState<String>>) {
    val otpLength = otpValues.size
    val focusRequesters = List(otpLength) { FocusRequester() }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        otpValues.forEachIndexed { index, otpValue ->
            OutlinedTextField(
                value = otpValue.value,
                onValueChange = { value ->
                    if (value.length <= 1) {
                        otpValue.value = value
                        if (value.isNotEmpty() && index < otpLength - 1) {
                            focusRequesters[index + 1].requestFocus()  // Move focus to next field
                        }
                    }
                },
                modifier = Modifier
                    .size(75.dp)
                    .padding(4.dp)
                    .focusRequester(focusRequesters[index]),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center),
                singleLine = true,
                maxLines = 1
            )
        }
    }

    // Autofocus the first OTP field
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}
