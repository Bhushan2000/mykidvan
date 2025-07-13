package com.vihaanshika.mykidsvan.android.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vihaanshika.mykidsvan.android.data.dto.response.RequestStatus
import com.vihaanshika.mykidsvan.android.data.dto.response.WithdrawRequestStatus
import com.vihaanshika.mykidsvan.android.utils.Resource

@Composable
fun WithdrawRequestList(
    withdrawStatus: Resource<WithdrawRequestStatus>,
    onRetry: () -> Unit = {}
) {
    when (withdrawStatus) {
        is Resource.Idle, is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Resource.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${withdrawStatus.message}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }

        is Resource.Success -> {
            val dataList = withdrawStatus.data.data

            if (dataList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No withdrawal requests found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dataList) { request ->
                        WithdrawRequestItem(request)
                    }
                }
            }
        }
    }
}
@Composable
fun WithdrawRequestItem(request: RequestStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Withdraw Amount: ₹${request.withdrawAmount ?: "N/A"}", fontWeight = FontWeight.Bold)
            Text("Account Name: ${request.accountName ?: "N/A"}")
            request.upiId?.let {
                if (it.isNotBlank()) Text("UPI ID: $it")
            }
            request.ifscCode?.let {
                if (it.isNotBlank()) Text("IFSC: $it")
            }
            Text("Account No.: ${request.accountNumber ?: "N/A"}")
            Spacer(Modifier.height(6.dp))
            Text(
                "Status: ${request.status ?: "Pending"}",
                color = when (request.status?.lowercase()) {
                    "pending" -> Color.Gray
                    "approved" -> Color(0xFF4CAF50)
                    "rejected" -> Color.Red
                    else -> Color.Black
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewWithdrawItem() {
    WithdrawRequestItem(
        request = RequestStatus(
            id = "1",
            userId = "123",
            role = "Driver",
            withdrawAmount = "1500",
            accountName = "John Doe",
            accountNumber = "1234567890",
            ifscCode = "SBIN0000001",
            upiId = "john@upi",
            status = "Approved"
        )
    )
}