package com.example.mykidsvan.android.ui
// AndroidX Compose UI
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Navigation
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.dto.request.DriverMessageToAllParentsRequest
import com.example.mykidsvan.android.data.dto.request.DriverMessageToIndividualParentsRequest
import com.example.mykidsvan.android.data.dto.response.GetDriverMessagesResponse
import com.example.mykidsvan.android.data.dto.response.GetParentMessagesResponse
import com.example.mykidsvan.android.utils.Constants
import com.example.mykidsvan.android.utils.Resource
import com.example.mykidsvan.android.utils.UnifiedMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    parentIdOrName: String?,
    viewModel: AuthViewModel, // or use Koin or your DI
    navController: NavController,
    assignVehicleId: String?,
    userId: String?
) {
    var messageText by remember { mutableStateOf("") }
    val parentMessagesState by viewModel.parentMessagesState.collectAsState()
    val driverMessageState by viewModel.driverMessagesState.collectAsState()
    val sendStateP by viewModel.sendMessageToParentState.collectAsState()
    val sendStateD by viewModel.sendMessageToDriverState.collectAsState()
    val parentList by viewModel.parentNamesList.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (parentIdOrName != null) {
            // one to one  // individual
            viewModel.getParentMessages(parentIdOrName)
            viewModel.getDriverMessagesForIndividual(
                userId.toString(),
                parentIdOrName
            ) // Fetch messages for assigned vehicle/driver
        } else {
            // one to many group
            parentList.forEach { parent ->
                val parentId = viewModel.getParentIdFromName(parent)
                viewModel.getParentMessages(parentId.toString())
            }
            viewModel.getDriverMessages(userId.toString()) // Fetch messages for assigned vehicle/driver
        }
    }
    // Handle send message response state
    LaunchedEffect(sendStateP) {
        when (sendStateP) {
            is Resource.Success -> {
                Toast.makeText(context, "Message sent successfully!", Toast.LENGTH_SHORT).show()
                messageText = ""
                // Refresh messages after sending
                viewModel.getDriverMessages(userId.toString())
                viewModel.getDriverMessagesForAllAssignedParents()
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
                // parent case  send message to driver from parent
                viewModel.getParentMessages(userId.toString())
                viewModel.getDriverMessagesForIndividual(assignVehicleId.toString(),userId.toString())
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
    // Show loading spinner
    if (sendStateD is Resource.Loading || sendStateP is Resource.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSendMessageResponse()
        }
    }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = parentIdOrName ?: "All Parents")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 24.dp,
                )
        ) {

            // Message List
            val backgroundColorP = Color(0xFFDFF6FF) // light blue // For parent
            val backgroundColorD = Color(0xFFFFF3CD) // light yellow // for driver
            val parentMessages =
                (parentMessagesState as? Resource.Success<GetParentMessagesResponse>)?.data?.data?.map {
                    val firstDriver = it.drivers.firstOrNull()
                    UnifiedMessage(
                        it.message,
                        it.createdAt ?: "",
                        firstDriver?.driverName.orEmpty(),
                        Constants.BASE_URL + (firstDriver?.profilePicture.orEmpty()),
                        Constants.USER_PARENT
                    )
                } ?: emptyList()

            val driverMessages =
                (driverMessageState as? Resource.Success<GetDriverMessagesResponse>)?.data?.data?.flatMap { driverMessage ->
                    driverMessage.parents.map { parent ->
                        UnifiedMessage(
                            driverMessage.message,
                            driverMessage.createdAt.orEmpty(),
                            parent.parentName.orEmpty(),
                            Constants.BASE_URL + parent.profilePicture.orEmpty(),
                            Constants.USER_DRIVER
                        )
                    }
                } ?: emptyList()

            val allMessages = parentMessages + driverMessages

            // Sort messages if needed by date
            val sortedMessages = allMessages.sortedBy { it.createdAt }

            Log.d("TAG", "ChatScreen: ${sortedMessages}")


            val listState = rememberLazyListState()
            LaunchedEffect(sortedMessages.size) {
                if (sortedMessages.isNotEmpty()) {
                    listState.animateScrollToItem(sortedMessages.lastIndex)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            ) {
                items(sortedMessages) { message ->
                    val isParent = message.senderType == Constants.USER_PARENT
                    val bubbleColor = if (isParent) backgroundColorP else backgroundColorD
                    val bubbleShape = if (isParent) {
                        RoundedCornerShape(14.dp, 14.dp, 14.dp, 0.dp)
                    } else {
                        RoundedCornerShape(14.dp, 14.dp, 0.dp, 14.dp)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isParent) Arrangement.Start else Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (isParent) {

                            Log.d("TAG", "Profile Url: ${message.profileUrl}")

                            // Profile Image on Left
                            AsyncImage(
                                model = message.profileUrl
                                    ?: "https://i.pravatar.cc/150?img=5", // fallback if null
                                contentDescription = "Parent Avatar",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        // Message Bubble
                        Card(
                            shape = bubbleShape,
                            colors = CardDefaults.cardColors(containerColor = bubbleColor),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = message.name ?: "",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = message.message ?: "",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                val (date, time) = formatDateTime(message.createdAt)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = date,
                                        fontSize = 10.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = time,
                                        fontSize = 10.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }

                        if (!isParent) {
                            Spacer(modifier = Modifier.width(6.dp))
                            // Profile Image on Right
                            AsyncImage(
                                model = message.profileUrl ?: "https://i.pravatar.cc/150?img=15",
                                contentDescription = "Driver Avatar",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                        }
                    }
                }
            }

            // Message Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Type a message…") },
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF1E88E5)
                    )
                )

                IconButton(onClick = {
                    if (messageText.isBlank()) {
                        Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT)
                            .show()
                        return@IconButton
                    }
//                    all
                    if (parentIdOrName != null) {
                        val request = DriverMessageToIndividualParentsRequest(
                            searchSpecific = Constants.INDIVIDUAL_CHAT,
                            vehiclesId = userId,
                            parentId = parentIdOrName,
                            message = messageText
                        )
                        viewModel.sendMessageToParent(request)
                    } else {
                        val request = DriverMessageToAllParentsRequest(
                            searchSpecific = Constants.GROUP_CHAT,
                            driverId = userId, // login user
                            message = messageText
                        )
                        viewModel.sendMessageToParent(request)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color(0xFF1E88E5)
                    )
                }
            }
        }
    }
}
