package com.example.mykidsvan.android.ui

import androidx.compose.foundation.lazy.items
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.dto.request.DriverMessageToAllParentsRequest
import com.example.mykidsvan.android.data.dto.request.DriverMessageToIndividualParentsRequest
import com.example.mykidsvan.android.data.dto.request.ParentMessageToDriverRequest
import com.example.mykidsvan.android.data.dto.response.GetDriverMessagesResponse
import com.example.mykidsvan.android.data.dto.response.GetParentMessagesResponse
import com.example.mykidsvan.android.utils.Constants
import com.example.mykidsvan.android.utils.DriverMessageResponseWrapper
import com.example.mykidsvan.android.utils.PlaceHolders

import com.example.mykidsvan.android.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    viewModel: AuthViewModel,
    userId: String?,
    userRole: String?,
    assignVehicleId: String?
) {
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf("All Parents") }
    val filterOptions = listOf("All Parents", "By Name")
    var specificSelection by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    val parentList by viewModel.parentNamesList.collectAsState()
    val sendStateP by viewModel.sendMessageToParentState.collectAsState()
    val sendStateD by viewModel.sendMessageToDriverState.collectAsState()
    val parentMessagesState by viewModel.parentMessagesState.collectAsState()
    val driverMessageState by viewModel.driverMessagesState.collectAsState()
    val vehicleTrackingStatus by viewModel.vehicleStatus.collectAsState()

    // Fetch parent list on launch
    LaunchedEffect(Unit) {
        viewModel.getAllParents()
        if (Constants.USER_PARENT.equals(userRole))
            viewModel.getParentMessages(userId.toString()) // Fetch messages for assigned vehicle/driver
        else
            viewModel.getDriverMessages(userId.toString())
    }

    // Handle send message response state
    LaunchedEffect(sendStateP) {
        when (sendStateP) {
            is Resource.Success -> {
                Toast.makeText(context, "Message sent successfully!", Toast.LENGTH_SHORT).show()
                messageText = ""
                // Refresh messages after sending
                viewModel.getParentMessages(userId.toString())
            }

            is Resource.Error -> {
                val error = (sendStateP as Resource.Error).message
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }

            is Resource.Loading -> { /* optional loading UI */
            }

            is Resource.Idle<*> -> {}
        }
    }
    LaunchedEffect(sendStateD) {
        when (sendStateD) {
            is Resource.Success -> {
                Toast.makeText(context, "Message sent successfully!", Toast.LENGTH_SHORT).show()
                messageText = ""
                // Refresh messages after sending
                viewModel.getDriverMessages(assignVehicleId.toString())
            }

            is Resource.Error -> {
                val error = (sendStateD as Resource.Error).message
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }

            is Resource.Loading -> { /* optional loading UI */
            }

            is Resource.Idle<*> -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSendMessageResponse()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // 🚫 Parent – show driver not assigned message
        if (userRole == Constants.USER_PARENT && (assignVehicleId.isNullOrBlank() || !vehicleTrackingStatus.equals(
                PlaceHolders.ACCEPTED, ignoreCase = true
            ))
        ) {

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
                            text = PlaceHolders.MSG_NO_VEHICLE_ASSIGNED,
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = PlaceHolders.MSG_REQUEST_DRIVER_ASSIGNMENT,
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {

            if (Constants.USER_DRIVER.equals(userRole)) {
                // Filter Dropdown
                DropdownSelector(
                    label = "Select Filter",
                    options = filterOptions,
                    selectedOption = selectedFilter,
                    onOptionSelected = {
                        selectedFilter = it
                        specificSelection = ""
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Show selection dropdown or input based on selected filter
                when (selectedFilter) {
                    "By Name" -> {
                        DropdownSelector(
                            label = "Select Parent",
                            options = parentList,
                            selectedOption = specificSelection,
                            onOptionSelected = { specificSelection = it }
                        )
                    }

                    "Individual Parent" -> {
                        OutlinedTextField(
                            value = specificSelection,
                            onValueChange = { specificSelection = it },
                            label = { Text("Enter Parent Mobile or ID") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                unfocusedBorderColor = Color.Gray,
                                containerColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Message Box
            RoundedMessageTextField(
                value = messageText,
                onValueChange = { messageText = it },
                label = "Type your message"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Send Button
            Button(
                onClick = {
                    if (messageText.isBlank()) {
                        Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (Constants.USER_DRIVER.equals(userRole)) {
                        // driver case
                        when (selectedFilter) {
                            "All Parents" -> {
                                val request = DriverMessageToAllParentsRequest(
                                    searchSpecific = "all",
                                    driverId = userId, // login user
                                    message = messageText
                                )
                                viewModel.sendMessageToParent(request)
                            }

                            "By Name" -> {
                                val parentId =
                                    viewModel.getParentIdFromName(specificSelection) ?: "0"
                                val request = DriverMessageToIndividualParentsRequest(
                                    parentId = parentId,
                                    message = messageText
                                )
                                viewModel.sendMessageToParent(request)
                            }
                        }
                    } else {
                        // parent case
                        val request = ParentMessageToDriverRequest(
                            vehiclesId = assignVehicleId.toString(), // login user
                            message = messageText
                        )
                        viewModel.sendMessageToDriver(request)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Send Message",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Messages List from Parents ---

            Text(
                text = "Messages",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (Constants.USER_PARENT.equals(userRole))
                when (parentMessagesState) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    is Resource.Error -> {
                        Text(
                            text = "Error loading messages",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    is Resource.Success -> {
                        val messages =
                            (parentMessagesState as Resource.Success<GetParentMessagesResponse>).data.data

                        if (messages.isEmpty()) {
                            Text(
                                text = "No messages available.",
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = Color.Gray
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f) // gives proper height to LazyColumn
                                    .fillMaxWidth()
                            ) {
                                if (messages.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No messages available.",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            color = Color.Gray
                                        )
                                    }
                                } else {
                                    items(messages) { message ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            Text(
                                                text = message.message ?: "",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            val input = message.createdAt.toString()
                                            val (date, time) = formatDateTime(input)

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = date,
                                                    fontSize = 12.sp,
                                                    color = Color.DarkGray
                                                )
                                                Text(
                                                    text = time,
                                                    fontSize = 12.sp,
                                                    color = Color.DarkGray
                                                )
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                    is Resource.Idle<*> -> {}

                }
            else
                when (driverMessageState) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    is Resource.Error -> {
                        Text(
                            text = "Error loading messages",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    is Resource.Success -> {
                        val messages =
                            (driverMessageState as Resource.Success<GetDriverMessagesResponse>).data.data

                        if (messages.isEmpty()) {
                            Text(
                                text = "No messages available.",
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = Color.Gray
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f) // gives proper height to LazyColumn
                                    .fillMaxWidth()
                            ) {
                                if (messages.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No messages available.",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            color = Color.Gray
                                        )
                                    }
                                } else {
                                    items(messages) { message ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            Text(
                                                text = message.message ?: "",
                                                fontSize = 14.sp
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            val input = message.createdAt.toString()
                                            val (date, time) = formatDateTime(input)

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = date,
                                                    fontSize = 12.sp,
                                                    color = Color.DarkGray
                                                )
                                                Text(
                                                    text = time,
                                                    fontSize = 12.sp,
                                                    color = Color.DarkGray
                                                )
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                    is Resource.Idle<*> -> {}

                }
        }
    }
}


fun formatDateTime(input: String): Pair<String, String> {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = inputFormat.parse(input)

    val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val outputTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val formattedDate = outputDateFormat.format(date!!)
    val formattedTime = outputTimeFormat.format(date)

    return Pair(formattedDate, formattedTime)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    var textFieldWidth by remember { mutableStateOf(0) }

    Column {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopStart)
        ) {
            // Use a clickable Box wrapping the OutlinedTextField to avoid conflicts
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    }
                    .clickable { expanded = true }
            ) {
                OutlinedTextField(
                    value = selectedOption,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    enabled = false, // disable text input to avoid focus issues
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline,
                        containerColor = colorScheme.surface,
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
                        cursorColor = colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldWidth.toDp() })
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.surface),
                offset = DpOffset(x = 0.dp, y = 0.dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = colorScheme.onSurface) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedMessageTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    height: Dp = 150.dp
) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = colorScheme.onSurfaceVariant) },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp)),
        maxLines = 5,
        shape = RoundedCornerShape(16.dp),
        textStyle = TextStyle(fontSize = 16.sp, color = colorScheme.onSurface),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            containerColor = colorScheme.surface,
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface,
            cursorColor = colorScheme.primary
        )

    )
}

