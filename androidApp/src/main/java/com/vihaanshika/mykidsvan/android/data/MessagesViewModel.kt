package com.vihaanshika.mykidsvan.android.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vihaanshika.mykidsvan.android.data.dto.request.SendMessageRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverMessage
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverAllMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetParentMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.MessageResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentsResponse
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.PlaceHolders
import com.vihaanshika.mykidsvan.android.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessagesViewModel(private val repository: AuthRepository) : ViewModel() {

    // Parent to Driver Message
    private val _sendMessageToDriverState =
        MutableStateFlow<Resource<MessageResponse>>(Resource.Loading())
    val sendMessageToDriverState: StateFlow<Resource<MessageResponse>> =
        _sendMessageToDriverState

    // Driver to Parents Message
    private val _sendMessageToParentState =
        MutableStateFlow<Resource<MessageResponse>>(Resource.Loading())
    val sendMessageToParentState: StateFlow<Resource<MessageResponse>> =
        _sendMessageToParentState

    // Get Parent Messages
    private val _parentMessagesState =
        MutableStateFlow<Resource<GetParentMessagesResponse>>(Resource.Loading())
    val parentMessagesState: StateFlow<Resource<GetParentMessagesResponse>> = _parentMessagesState

    // Get Driver Messages
    private val _driverMessagesState =
        MutableStateFlow<Resource<GetDriverMessagesResponse>>(Resource.Loading())
    val driverMessagesState: StateFlow<Resource<GetDriverMessagesResponse>> = _driverMessagesState

    // get all parents message
//    private val _driverMessagesState = MutableStateFlow<Resource<List<UnifiedMessage>>>(Resource.Idle())
//    val driverMessagesState: StateFlow<Resource<List<UnifiedMessage>>> = _driverMessagesState

    fun resetSendMessages(){
        _sendMessageToParentState.value = Resource.Idle()
        _sendMessageToDriverState.value = Resource.Idle()
    }

    fun sendMessageToDriver(request: SendMessageRequest) {
        viewModelScope.launch {
            _sendMessageToDriverState.value = Resource.Loading()
            try {
                val response = repository.sendMessageToDriverFromParent(request) // index.php/api/AccountsController/parent_message
                _sendMessageToDriverState.value = Resource.Success(response)
            } catch (e: Exception) {
                _sendMessageToDriverState.value = Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    fun sendMessageToParent(request: SendMessageRequest) {
        viewModelScope.launch {
            _sendMessageToParentState.value = Resource.Loading()
            try {
                val response = repository.sendMessageToParentFromDriver(request) // index.php/api/AccountsController/driver_message
                _sendMessageToParentState.value = Resource.Success(response)
            } catch (e: Exception) {
                _sendMessageToParentState.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getParentMessages(parentId: String) {
        viewModelScope.launch {
            _parentMessagesState.value = Resource.Loading()
            try {
                val response = repository.getParentMessage(parentId)
                _parentMessagesState.value = Resource.Success(response)
            } catch (e: Exception) {
                _parentMessagesState.value = Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    private val _driverParentMessagesState =
        MutableStateFlow<Resource<GetDriverMessagesResponse>>(Resource.Loading())
    val driverParentMessagesState: StateFlow<Resource<GetDriverMessagesResponse>> =
        _driverParentMessagesState

    fun getDriverMessagesForAllAssignedParents() {
        viewModelScope.launch {
            _driverParentMessagesState.value = Resource.Loading()

            try {
                val allMessages = mutableListOf<DriverMessage>()

                val parentIds = parentNameToIdMap.value.values

                parentIds.forEach { parentId ->
                    val response = repository.getDriverMessage(parentId)
                    allMessages.addAll(response.data)
                }

                val finalResponse = GetDriverMessagesResponse(
                    status = true,
                    message = "Combined messages from all assigned parents",
                    data = ArrayList(allMessages)
                )

                _driverParentMessagesState.value = Resource.Success(finalResponse)

            } catch (e: Exception) {
                _driverParentMessagesState.value = Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }


    fun getDriverMessages(driverId: String) {
        viewModelScope.launch {
            _driverMessagesState.value = Resource.Loading()
            try {
                val response = repository.getDriverMessage(driverId)
                _driverMessagesState.value = Resource.Success(response)
            } catch (e: Exception) {
                _driverMessagesState.value = Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    private val _allDriverMessagesState =
        MutableStateFlow<Resource<GetDriverAllMessagesResponse>>(Resource.Loading())
    val allDriverMessagesState: StateFlow<Resource<GetDriverAllMessagesResponse>> =
        _allDriverMessagesState

    fun getDriverMessagesAll(driverId: String) {
        viewModelScope.launch {
            _allDriverMessagesState.value = Resource.Loading()
            try {
                val response = repository.getDriverAllMessage(driverId) // Create this repo method
                _allDriverMessagesState.value = Resource.Success(response)
            } catch (e: Exception) {
                _allDriverMessagesState.value = Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    fun getDriverMessagesForIndividual(driverId: String, filterParentId: String) {
        viewModelScope.launch {
            _driverMessagesState.value = Resource.Loading()
            try {
                val response = repository.getDriverMessage(driverId)
                // Filter messages by matching parentId
                val filteredMessages = response.data.filter { it.parentId == filterParentId }
                // Create a new response with filtered data
                val filteredResponse = response.copy(data = ArrayList(filteredMessages))
                _driverMessagesState.value = Resource.Success(filteredResponse)
            } catch (e: Exception) {
                _driverMessagesState.value = Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }


    /*fun getDriverMessagesForAllParents(parentNames: List<String>) {
        viewModelScope.launch {
            _driverMessagesState.value = Resource.Loading()
            try {
                val allMessages = mutableListOf<UnifiedMessage>()

                parentNames.forEach { parentName ->
                    val parentId = getParentIdFromName(parentName)
                    Log.d("TAG", "MessageScreen: parent id - $parentId name - $parentName")

                    parentId?.let {
                        val response = repository.getDriverMessage(it)
                        val messages = response.data.flatMap { driverMessage ->
                            driverMessage.parents.map { parent ->
                                UnifiedMessage(
                                    message = driverMessage.message,
                                    createdAt = driverMessage.createdAt ?: "",
                                    name = parent.parentName.orEmpty(),
                                    profileUrl = Constants.BASE_URL + (parent.profilePicture.orEmpty()),
                                    senderType = Constants.USER_DRIVER
                                )
                            }
                        }
                        allMessages.addAll(messages)
                    }
                }

                _driverMessagesState.value = Resource.Success(allMessages)

            } catch (e: Exception) {
                _driverMessagesState.value = Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }*/


    fun clearSendMessageResponse() {
        _sendMessageToDriverState.value = Resource.Idle()
        _sendMessageToParentState.value = Resource.Idle()
    }


    private val _allParentsState = MutableStateFlow<Resource<ParentsResponse>>(Resource.Loading())
    val allParentsState: StateFlow<Resource<ParentsResponse>> = _allParentsState

    val parentNamesList = MutableStateFlow<List<String>>(emptyList())

    // New state to store name-to-ID map
    val parentNameToIdMap = MutableStateFlow<Map<String, String>>(emptyMap())

    val parentNameToProfileUrlMap = mutableStateOf<Map<String, String>>(emptyMap())

    fun getAllParents(userId: String) {
        viewModelScope.launch {
            _allParentsState.value = Resource.Loading()
            try {
                val response = repository.getAllParents()
                _allParentsState.value = Resource.Success(response)

                val parentList = response.data

                // Prepare list for dropdown
                val names = parentList
                    .filter { it.status.equals(PlaceHolders.ACCEPTED) && it.vehicleId.equals(userId) }
                    .mapNotNull { it.parentName }
                parentNamesList.value = names

                // Build name-to-id map
                val nameIdMap = parentList
                    .filter { it.status.equals(PlaceHolders.ACCEPTED) && it.vehicleId.equals(userId) }
                    .mapNotNull { parent ->
                        val name = parent.parentName
                        val id = parent.id
                        if (name != null && id != null) name to id else null
                    }.toMap()
                parentNameToIdMap.value = nameIdMap

                val profileUrlMap = parentList
                    .filter { it.status.equals(PlaceHolders.ACCEPTED) && it.vehicleId.equals(userId) }
                    .mapNotNull { parent ->
                        val name = parent.parentName
                        val url = parent.profilePicture
                        if (name != null && url != null) name to url else null
                    }.toMap()
                parentNameToProfileUrlMap.value = profileUrlMap

            } catch (e: Exception) {
                _allParentsState.value = Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    fun getParentIdFromName(name: String): String? {
        return parentNameToIdMap.value[name]
    }
    fun getProfileUrlFromName(name: String): String? {
        val relativeUrl = parentNameToProfileUrlMap.value[name]
        return relativeUrl?.let { Constants.BASE_URL + it }
    }

    fun resetGetAllParent(){
        _allParentsState.value = Resource.Idle()
    }
}