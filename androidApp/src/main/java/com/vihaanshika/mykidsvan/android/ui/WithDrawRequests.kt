package com.vihaanshika.mykidsvan.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.authapp.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithDrawRequests(viewModel: AuthViewModel,userId:String) {
    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(top = 80.dp , end = 8.dp, bottom = 16.dp, start = 8.dp),
             verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
             val withdrawStatus by viewModel.withdrawStatus.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.withdrawRequestStatus(userId = userId)
            }
            WithdrawRequestList(
                withdrawStatus = withdrawStatus,
                onRetry = { viewModel.withdrawRequestStatus(userId = userId) }
            )
        }
    }
}

