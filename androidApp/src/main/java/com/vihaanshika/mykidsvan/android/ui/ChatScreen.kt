package com.vihaanshika.mykidsvan.android.ui
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Navigation
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.data.MessagesViewModel
import com.vihaanshika.mykidsvan.android.data.dto.request.DriverMessageToAllParentsRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.DriverMessageToIndividualParentsRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.ParentMessageToDriverRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendMessageRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverAllMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetParentMessagesResponse
import com.vihaanshika.mykidsvan.android.utils.APIEndpoints
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.Resource
import com.vihaanshika.mykidsvan.android.utils.UnifiedMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    parentIdOrName: String?,
    viewModel: MessagesViewModel,
    navController: NavController,
    assignVehicleId: String?,
    userId: String?,
    userRole: String?
) {
    var messageText by remember { mutableStateOf("") }
    val parentMessagesState by viewModel.parentMessagesState.collectAsState()
    val driverMessageState by viewModel.driverMessagesState.collectAsState()
    val getDriverGrpMessage by viewModel.allDriverMessagesState.collectAsState() // index.php/api/AccountsController/get_all_message/{driver_id}

    val sendStateP by viewModel.sendMessageToParentState.collectAsState() // driver n send kele l state aahe // // index.php/api/AccountsController/driver_message
    val sendStateD by viewModel.sendMessageToDriverState.collectAsState() // parent n send kele l state aahe // // index.php/api/AccountsController/parent_message
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
            if (assignVehicleId == null)
                viewModel.getDriverMessagesAll(userId.toString())
            else
                viewModel.getDriverMessagesAll(assignVehicleId.toString())
        }
    }
    // Handle send message response state
    LaunchedEffect(sendStateP) {
        when (sendStateP) {
            is Resource.Success -> {
                Toast.makeText(context, "Message sent successfully! by Driver", Toast.LENGTH_SHORT)
                    .show()
                messageText = ""
                // Refresh messages after sending
                // driver case  send message to parent from driver to group or individual
                if (parentIdOrName == null) {
                    viewModel.getDriverMessagesAll(userId.toString())
                } else {
                    viewModel.getParentMessages(parentIdOrName.toString())
                    viewModel.getDriverMessages(userId.toString())
                }

                viewModel.resetSendMessages()

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
                Toast.makeText(context, "Message sent successfully! by Parent", Toast.LENGTH_SHORT)
                    .show()
                messageText = ""
                // parent case  send message to driver from parent
                if (parentIdOrName == null) {
                    viewModel.getDriverMessagesAll(userId.toString())
                } else {
                    viewModel.getParentMessages(userId.toString())
                    viewModel.getDriverMessagesForIndividual(
                        assignVehicleId.toString(),
                        userId.toString()
                    )
                }
                viewModel.resetSendMessages()
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
    if (sendStateD is Resource.Loading || sendStateP is Resource.Loading ||
        parentMessagesState is Resource.Loading ||
        driverMessageState is Resource.Loading ||
        getDriverGrpMessage is Resource.Loading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSendMessageResponse()
            viewModel.resetGetAllParent()
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

            if (parentIdOrName == null) { // all
                val driverGrpMsg =
                    (getDriverGrpMessage as? Resource.Success<GetDriverAllMessagesResponse>)?.data?.data?.map {
                        UnifiedMessage(
                            it.message,
                            it.createdAt ?: "",
                            it.name,
                            APIEndpoints.BASE_URL + (it.profilePicture.orEmpty()),
                            if (it.parentId?.isEmpty() == true) Constants.USER_DRIVER else Constants.USER_PARENT
                        )
                    } ?: emptyList()

                val allMessages = driverGrpMsg

                // Sort messages if needed by date
                val sortedMessages = allMessages.sortedBy { it.createdAt }

                Log.d("TAG", "ChatScreen: ${sortedMessages}")

                val listState = rememberLazyListState()
                LaunchedEffect(sortedMessages.size) {
                    if (sortedMessages.isNotEmpty()) {
                        listState.animateScrollToItem(sortedMessages.lastIndex)
                    }
                }
                if (sortedMessages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No messages yet",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                } else {
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
                                            .shadow(16.dp, shape = CircleShape, clip = false)
                                            .clip(CircleShape)
                                            .border(1.dp, Color.Gray, CircleShape),


                                        )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                // Message Bubble
                                Card(
                                    shape = bubbleShape,
                                    colors = CardDefaults.cardColors(containerColor = bubbleColor),
                                    elevation = CardDefaults.cardElevation(16.dp),
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
                                        model = message.profileUrl
                                            ?: "https://i.pravatar.cc/150?img=15",
                                        contentDescription = "Driver Avatar",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .shadow(16.dp, shape = CircleShape, clip = false)
                                            .clip(CircleShape)
                                            .border(1.dp, Color.Gray, CircleShape)
                                    )
                                }
                            }
                        }
                    }

                }
            } else {
                val parentMessages =
                    (parentMessagesState as? Resource.Success<GetParentMessagesResponse>)?.data?.data?.map {
                        val firstDriver = it.drivers.firstOrNull()
                        UnifiedMessage(
                            it.message,
                            it.createdAt ?: "",
                            firstDriver?.driverName.orEmpty(),
                            APIEndpoints.BASE_URL + (firstDriver?.profilePicture.orEmpty()),
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
                                APIEndpoints.BASE_URL + parent.profilePicture.orEmpty(),
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

                if (sortedMessages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No messages yet",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                } else {
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
                                            .shadow(16.dp, shape = CircleShape, clip = false)
                                            .clip(CircleShape)
                                            .border(1.dp, Color.Gray, CircleShape),


                                        )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                // Message Bubble
                                Card(
                                    shape = bubbleShape,
                                    colors = CardDefaults.cardColors(containerColor = bubbleColor),
                                    elevation = CardDefaults.cardElevation(16.dp),
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
                                        model = message.profileUrl
                                            ?: "https://i.pravatar.cc/150?img=15",
                                        contentDescription = "Driver Avatar",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .shadow(16.dp, shape = CircleShape, clip = false)
                                            .clip(CircleShape)
                                            .border(1.dp, Color.Gray, CircleShape)
                                    )
                                }
                            }
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
            )
            {
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
                    if (parentIdOrName != null && Constants.USER_DRIVER.equals(userRole)) {
                        val request = SendMessageRequest(
                            searchSpecific = Constants.INDIVIDUAL_CHAT,
                            vehiclesId = userId,
                            parentId = parentIdOrName,
                            message = messageText
                        )
                        viewModel.sendMessageToParent(request)
                    } else if (parentIdOrName == null && Constants.USER_DRIVER.equals(userRole)) {
                        val request = SendMessageRequest(
                            searchSpecific = Constants.GROUP_CHAT,
                            vehiclesId = userId, // login user
                            parentId = "",
                            message = messageText
                        )
                        viewModel.sendMessageToParent(request)
                    }

                    if (parentIdOrName == null && Constants.USER_PARENT.equals(userRole)) {
                        // parent case  send message to driver from parent
                        val request = SendMessageRequest(
                            searchSpecific = Constants.GROUP_CHAT,
                            vehiclesId = assignVehicleId, // login user
                            parentId = userId,
                            message = messageText
                        )
                        viewModel.sendMessageToDriver(request)
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
