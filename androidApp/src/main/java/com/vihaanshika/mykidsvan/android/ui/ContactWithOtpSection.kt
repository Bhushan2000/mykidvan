package com.vihaanshika.mykidsvan.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vihaanshika.mykidsvan.android.utils.OtpState
import kotlinx.coroutines.delay
@Composable
fun ContactWithOtpSection(
    color: TextFieldColors,
    contactNumber: String,
    onContactChange: (String) -> Unit,
    otp: String,
    onOtpChange: (String) -> Unit,
    timerSeconds: Int,
    showOtpField: Boolean,
    isVerified: Boolean,
    otpState: OtpState,
    onSendOtp: (String) -> Unit,
    onVerifyOtp: (String, String) -> Unit,
    onResendOtp: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = contactNumber,
                onValueChange = {
                    if (!isVerified && it.length <= 10) onContactChange(it)
                },
                label = { Text("Contact Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                trailingIcon = {
                    if (isVerified) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = Color.Green
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                enabled = !isVerified,
                colors = color
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { onSendOtp(contactNumber) },
                enabled = contactNumber.length == 10 && !isVerified
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send OTP",
                    tint = if (contactNumber.length == 10 && !isVerified)
                        MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }

        if (showOtpField && !isVerified) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 4 && it.all(Char::isDigit)) onOtpChange(it)
                },
                label = { Text("OTP") },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .width(200.dp)
                    .height(64.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(14.dp),
                colors = color
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (otpState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterHorizontally),
                    strokeWidth = 2.dp
                )
            }

            otpState.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            otpState.message?.let {
                Text(
                    text = it,
                    color = Color.Green,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (timerSeconds > 0) {
                Text(
                    text = "Resend in $timerSeconds s",
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                TextButton(
                    onClick = { onResendOtp(contactNumber) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Resend OTP")
                }
            }
        }
    }
}

