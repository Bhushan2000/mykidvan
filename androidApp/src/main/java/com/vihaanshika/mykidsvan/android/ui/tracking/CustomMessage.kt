package com.vihaanshika.mykidsvan.android.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomMessage(title:String, body: String){
    Box(
        modifier = Modifier
            .fillMaxSize(), // Fills the whole screen
        contentAlignment = Alignment.Center // Centers the inner content
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp) // This adds margin around the whole message
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(
                        Color.White.copy(alpha = 0.8f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = Color.Red,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = body,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}