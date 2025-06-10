package com.vihaanshika.mykidsvan.android.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.data.dto.request.DriverMessageToAllParentsRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.DriverMessageToIndividualParentsRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.ParentMessageToDriverRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendMessageRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverAllMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetParentMessagesResponse
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.PlaceHolders
import com.vihaanshika.mykidsvan.android.utils.Resource
import com.vihaanshika.mykidsvan.android.utils.UnifiedMessage
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    userId: String?,
    userRole: String?,
    assignVehicleId: String?
) {
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf("All Parents") }
    var specificSelection by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    val parentList by viewModel.parentNamesList.collectAsState()
    val sendStateP by viewModel.sendMessageToParentState.collectAsState() // driver n send kele l state aahe
    val sendStateD by viewModel.sendMessageToDriverState.collectAsState() // parent n send kele l state aahe
    val getDriverGrpMessage by viewModel.allDriverMessagesState.collectAsState()
    val parentMessagesState by viewModel.parentMessagesState.collectAsState()
    val driverMessageState by viewModel.driverMessagesState.collectAsState()
    val vehicleTrackingStatus by viewModel.vehicleStatus.collectAsState()

    // Fetch parent list on launch
    LaunchedEffect(Unit) {
        viewModel.getAllParents()
        if (Constants.USER_PARENT.equals(userRole)) {
            viewModel.getParentMessages(userId.toString())
            viewModel.getDriverMessagesForIndividual(assignVehicleId.toString(), userId.toString())
        } else {
            // driver
            // issue with this loop
            parentList.forEach { parent ->
                val parentId = viewModel.getParentIdFromName(parent)
                viewModel.getParentMessages(parentId.toString()) // loads the driver messages for individual
            }

            viewModel.getDriverMessages(userId.toString()) // loads parent messages
            viewModel.getDriverMessagesAll(userId.toString())  // load driver message for group
        }
    }

    // Handle send message response state
    LaunchedEffect(sendStateP) {
        when (sendStateP) {
            is Resource.Success -> {
                Toast.makeText(context, "Message sent successfully!", Toast.LENGTH_SHORT).show()
                messageText = ""
                // Refresh messages after sending
                // driver case // send message to parent from driver
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
                // Refresh messages after sending
                // parent case // send message to driver from parent
                viewModel.getParentMessages(userId.toString())
                viewModel.getDriverMessagesForIndividual(
                    assignVehicleId.toString(), userId.toString()
                )
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

    LaunchedEffect(getDriverGrpMessage) {
        when (getDriverGrpMessage) {
            is Resource.Success -> {

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
            .padding(top = 80.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
            .verticalScroll(rememberScrollState())
            .imePadding() // pushes the UI up when keyboard appears

    ) {

        // 🚫 Parent – show driver not assigned message
        if (userRole == Constants.USER_PARENT && (assignVehicleId.isNullOrBlank() || !vehicleTrackingStatus.equals(
                PlaceHolders.ACCEPTED, ignoreCase = true
            ))
        ) {

            Box(
                modifier = Modifier.fillMaxSize(), // Fills the whole screen
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
                                Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp)
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

            // choose to chat
            if (Constants.USER_DRIVER.equals(userRole)) {
                Text(
                    text = "Choose Parent to Chat",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp) // ✅ Use fixed or dynamic height
                        .clip(RoundedCornerShape(12.dp)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // First Option: All Parents
                    item {
                        ChatListItem(
                            name = "All Parents", onClick = {
                                selectedFilter = "All"
                                onParentSelected("all", navController, null)
                            })
                    }

                    // List of individual parents
                    items(parentList) { parentName ->
                        ChatListItem(
                            name = parentName, onClick = {
                                selectedFilter = "Individual"
                                specificSelection = parentName
                                val parentId = viewModel.getParentIdFromName(specificSelection)
                                onParentSelected("individual", navController, parentId)
                            })
                    }
                }

            } else {

                // parent

                Text(
                    text = "Select group to group chat.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp) // ✅ Use fixed or dynamic height
                        .clip(RoundedCornerShape(12.dp)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // First Option: All Parents
                    item {
                        ChatListItem(
                            name = "Group", onClick = {
                                onParentSelected("all", navController, null)
                            })
                    }
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // group messages
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

            val driverGrpMsg =
                (getDriverGrpMessage as? Resource.Success<GetDriverAllMessagesResponse>)?.data?.data?.map {
                    UnifiedMessage(
                        it.message,
                        it.createdAt ?: "",
                        "",
                        "",
                        Constants.USER_PARENT
                    )
                } ?: emptyList()
            val allMessages = parentMessages + driverMessages + driverGrpMsg
            // Sort messages if needed by date
            val sortedMessages = allMessages.sortedBy { it.createdAt }
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


                            // Profile Image on Left
                            AsyncImage(
                                model = message.profileUrl
                                    ?: "https://i.pravatar.cc/150?img=5", // fallback if null
                                contentDescription = "Parent Avatar",
                                modifier = Modifier
                                    .size(36.dp)
                                    .shadow(16.dp, shape = CircleShape, clip = false)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
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
                                        text = date, fontSize = 10.sp, color = Color.DarkGray
                                    )
                                    Text(
                                        text = time, fontSize = 10.sp, color = Color.DarkGray
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
                                    .shadow(16.dp, shape = CircleShape, clip = false)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            WhatsAppStyleMessageInput(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isBlank()) {
                        Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT)
                            .show()
                        return@WhatsAppStyleMessageInput
                    }
                    if (Constants.USER_DRIVER.equals(userRole)) {
                        // driver case
                        when (selectedFilter) {
                            "All Parents" -> {
                                val request = SendMessageRequest(
                                    searchSpecific = Constants.GROUP_CHAT,
                                    vehiclesId = userId, // login user
                                    message = messageText,
                                    parentId = ""
                                )
                                viewModel.sendMessageToParent(request)
                            }

                            "By Name" -> {
                                val parentId =
                                    viewModel.getParentIdFromName(specificSelection) ?: "0"
                                val request = SendMessageRequest(
                                    searchSpecific = Constants.INDIVIDUAL_CHAT,
                                    vehiclesId = userId,
                                    parentId = parentId,
                                    message = messageText
                                )
                                viewModel.sendMessageToParent(request)
                            }
                        }
                    } else {
                        // parent case  send message to driver from parent
                        val request = SendMessageRequest(
                            vehiclesId = assignVehicleId, // login user
                            parentId = userId,
                            message = messageText,
                            searchSpecific = Constants.INDIVIDUAL_CHAT,
                        )
                        viewModel.sendMessageToDriver(request)
                    }
                })
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
fun WhatsAppStyleMessageInput(
    messageText: String, onMessageChange: (String) -> Unit, onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = onMessageChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp, max = 150.dp), // ✅ Sets a max height
            placeholder = { Text("Type a message…") },
            shape = RoundedCornerShape(20.dp),
            maxLines = 5, // ✅ Allows internal scrolling after 5 lines
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.LightGray, focusedBorderColor = Color(0xFF1E88E5)
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onSendClick
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color(0xFF1E88E5)
            )
        }
    }
}


fun onParentSelected(
    type: String, navController: NavHostController, parentIdOrName: String?
) {
    val argument = parentIdOrName ?: type // e.g., type = "all"
    navController.navigate("chat/$argument")
}