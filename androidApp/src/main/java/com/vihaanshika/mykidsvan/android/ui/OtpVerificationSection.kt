package com.vihaanshika.mykidsvan.android.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.utils.OtpState

@Composable
fun OtpVerificationSection(
    contactNumber: String,
    otpState: OtpState,
    onSendOtp: (String) -> Unit,
    onVerifyOtp: (String, String) -> Unit,
    onResendOtp: (String) -> Unit,
    onOtpSuccess: () -> Unit // New callback
) {
    var otp by remember { mutableStateOf("") }
    var timerSeconds by remember { mutableStateOf(60) }
    var showOtpField by remember { mutableStateOf(false) }

    // Start countdown
    LaunchedEffect(otpState.success) {
        if (otpState.success) {
            showOtpField = true
            onOtpSuccess() // ✅ notify parent composable
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (!showOtpField) {
            Button(
                onClick = { onSendOtp(contactNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Send OTP")
            }
        }

        if (showOtpField && !otpState.success) {
            OutlinedTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 6) {
                        otp = it
                        if (otp.length == 6) {
                            onVerifyOtp(contactNumber, otp)
                        }
                    }
                },
                label = { Text("Enter OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            if (otpState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }

            otpState.error?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp)
            }

            otpState.message?.let {
                Text(text = it, color = Color.Green, fontSize = 14.sp)
            }

            Text(
                text = if (timerSeconds > 0) "Resend OTP in $timerSeconds seconds"
                else "Didn't receive OTP?",
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (timerSeconds == 0) {
                TextButton(onClick = {
                    onResendOtp(contactNumber)
                    timerSeconds = 60
                }) {
                    Text("Resend OTP")
                }
            }
        }
    }
}


