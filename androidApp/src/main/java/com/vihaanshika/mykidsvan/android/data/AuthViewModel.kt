package com.example.authapp.presentation.viewmodel

import com.google.auth.oauth2.GoogleCredentials

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.vihaanshika.mykidsvan.android.data.AuthRepository
import com.vihaanshika.mykidsvan.android.data.dto.request.PhotoOfVehicle
import com.vihaanshika.mykidsvan.android.data.dto.request.ReferByResponse
import com.vihaanshika.mykidsvan.android.data.dto.request.SchoolRegistrationRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendRequestToDriverResponse
import com.vihaanshika.mykidsvan.android.data.dto.request.UpdateVehicleImageRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.CommissionResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.District
import com.vihaanshika.mykidsvan.android.data.dto.response.DocumentUploadResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverData
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverMessage
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverMob
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverAllMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetParentMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.LoginResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.Parent
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentData
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentsResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.RegistrationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.RequestData
import com.vihaanshika.mykidsvan.android.data.dto.response.School
import com.vihaanshika.mykidsvan.android.data.dto.response.State
import com.vihaanshika.mykidsvan.android.data.dto.response.Taluka
import com.vihaanshika.mykidsvan.android.utils.LoginState
import com.vihaanshika.mykidsvan.android.utils.PaymentState
import com.vihaanshika.mykidsvan.android.utils.PlaceHolders
import com.vihaanshika.mykidsvan.android.utils.Resource
import com.vihaanshika.mykidsvan.android.utils.UpdatePasswordState
import com.vihaanshika.mykidsvan.android.utils.UserPreferences
import com.google.gson.Gson
import com.vihaanshika.mykidsvan.android.data.dto.request.SendMessageRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.MessageResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.OtpResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.OtpVerificationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException

data class UserSessionData(
    val isLoggedIn: Boolean,
    val userId: String?,
    val userRole: String?,
    val assignVehicleId: String?,
    val status: String?,
    val referCode: String?
)

class AuthViewModel(
    private val repository: AuthRepository, private val userPreferences: UserPreferences
) : ViewModel() {
    // login
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    private val _stateOptions = MutableStateFlow<List<State>>(emptyList())
    val stateOptions: StateFlow<List<State>> = _stateOptions

    //////////////////////////////////////

    private val _driverAllRequest = MutableStateFlow<List<RequestData>>(emptyList())
    val driverAllRequest: StateFlow<List<RequestData>> = _driverAllRequest

    private val _selectedDriverRequest = MutableStateFlow<DriverMob?>(null)
    val selectedDriverRequest: StateFlow<DriverMob?> = _selectedDriverRequest

    ///////////////////////////////////////

    private val _districtOptions = MutableStateFlow<List<District>>(emptyList())
    val districtOptions: StateFlow<List<District>> = _districtOptions

    private val _talukaOptions = MutableStateFlow<List<Taluka>>(emptyList())
    val talukaOptions: StateFlow<List<Taluka>> = _talukaOptions

    private val _schoolOptions = MutableStateFlow<List<School>>(emptyList())
    val schoolOptions: StateFlow<List<School>> = _schoolOptions

    /////////////////////////////////////////////////////////////////////
    private val _parentsOptions = MutableStateFlow<List<Parent>>(emptyList())
    val parentsOptions: StateFlow<List<Parent>> = _parentsOptions

    private val _driverOptions = MutableStateFlow<List<DriverMob>>(emptyList())
    val driverOptions: StateFlow<List<DriverMob>> = _driverOptions

    // driver list by mob no
    val foundDriver = MutableStateFlow<DriverMob?>(null) // Create a state flow
    ///////////////////////////////////////////////////////////////////

    private val _selectedState = MutableStateFlow<State?>(null)
    val selectedState: StateFlow<State?> = _selectedState

    private val _selectedDistrict = MutableStateFlow<District?>(null)
    val selectedDistrict: StateFlow<District?> = _selectedDistrict

    private val _selectedTaluka = MutableStateFlow<Taluka?>(null)
    val selectedTaluka: StateFlow<Taluka?> = _selectedTaluka

    private val _selectedSchool = MutableStateFlow<School?>(null)
    val selectedSchool: StateFlow<School?> = _selectedSchool

    private val _selectedAllSchool = MutableStateFlow<School?>(null)
    val selectedAllSchool: StateFlow<School?> = _selectedAllSchool

    // driver registration
    private val _driverRegistrationSuccess = MutableStateFlow<RegistrationResponse?>(null)
    val driverRegistrationSuccess: StateFlow<RegistrationResponse?> = _driverRegistrationSuccess

    // parent registration
//    private val _parentRegistrationSuccess = MutableStateFlow(false)
//    val parentRegistrationSuccess: StateFlow<Boolean> = _parentRegistrationSuccess

    private val _parentRegistrationSuccess = MutableStateFlow<RegistrationResponse?>(null)
    val parentRegistrationSuccess: StateFlow<RegistrationResponse?> = _parentRegistrationSuccess

    // school registration
    private val _schoolRegistrationSuccess = MutableStateFlow(false)
    val schoolRegistrationSuccess: StateFlow<Boolean> = _schoolRegistrationSuccess

    // StateFlow to manage OTP states
    private val _sendOtpState = MutableStateFlow<Resource<OtpResponse>>(Resource.Idle())
    val sendOtpState: StateFlow<Resource<OtpResponse>> = _sendOtpState

    // StateFlow to verify OTP
    private val _verifyOtp = MutableStateFlow<Resource<OtpVerificationResponse>>(Resource.Idle())
    val verifyOtp: StateFlow<Resource<OtpVerificationResponse>> = _verifyOtp

    // update Password
    private val _state = MutableStateFlow(UpdatePasswordState())
    val state: StateFlow<UpdatePasswordState> = _state

    // user id
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _referCode = MutableStateFlow<String?>(null)
    val referCode: StateFlow<String?> = _referCode

    private val _assignedVehicleId = MutableStateFlow<String?>(null)
    val assignedVehicleId: StateFlow<String?> = _assignedVehicleId

    private val _vehicleStatus = MutableStateFlow<String?>(null)
    val vehicleStatus: StateFlow<String?> = _vehicleStatus

    init {
        val combined = combine(
            userPreferences.isLoggedInFlow,
            userPreferences.userIdFlow,
            userPreferences.userRole,
            userPreferences.assignVehicleIdFlow,
            userPreferences.statusFlow
        ) { isLoggedIn, id, role, vehicleId, status ->
            arrayOf(isLoggedIn, id, role, vehicleId, status)
        }

        viewModelScope.launch {
            combine(combined, userPreferences.referCode) { array, referCode ->
                UserSessionData(
                    isLoggedIn = array[0] as Boolean,
                    userId = array[1] as String?,
                    userRole = array[2] as String?,
                    assignVehicleId = array[3] as String?,
                    status = array[4] as String?,
                    referCode = referCode
                )
            }.collect { session ->
                // same as above...
                if (session.isLoggedIn) {
                    _loginState.value = LoginState(success = true, message = "Welcome Back!")
                    _userId.value = session.userId
                    _userRole.value = session.userRole
                    _assignedVehicleId.value = session.assignVehicleId
                    _vehicleStatus.value = session.status
                    _referCode.value = session.referCode

                    session.userId?.let { loadAllRequests(it) }
                }
            }
        }


        loadStateOptions()
    }

    private val _navigateToMessageScreen = MutableStateFlow(false)
    val navigateToMessageScreen = _navigateToMessageScreen.asStateFlow()

    fun triggerMessageScreenNavigation() {
        _navigateToMessageScreen.value = true
    }

    fun consumeNavigationFlag() {
        _navigateToMessageScreen.value = false
    }

    // Function to send OTP
    fun sendOtp(phone: String,purpose: String) {
        _sendOtpState.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.sendOtp(phone,purpose)
                if (response.status == true) {
                    _sendOtpState.value = Resource.Success(response)
                    Log.d("AuthViewModel", "sendOtp: ${response.message}")
                } else {
                    _sendOtpState.value = Resource.Error(response.message ?: "Failed to send OTP.")
                }
            } catch (e: Exception) {
                _sendOtpState.value = Resource.Error("An error occurred: ${e.localizedMessage}", e)
                Log.e("AuthViewModel", "Failed to send OTP", e)
            }
        }
    }

    // Function to verify OTP
    fun verifyOtp(phone: String, otp: String) {
        _verifyOtp.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.verifyOtp(phone, otp)
                if (response.status) {
                    _verifyOtp.value = Resource.Success(response)
                    Log.d("AuthViewModel", "verifyOtp: ${response.message}")
                } else {
                    _verifyOtp.value = Resource.Error(response.message ?: "Failed to verify OTP.")
                }
            } catch (e: Exception) {
                _verifyOtp.value = Resource.Error("An error occurred: ${e.localizedMessage}", e)
                Log.e("AuthViewModel", "Failed to verify OTP", e)
            }
        }
    }

    fun resendOtp(phone: String,purpose: String) {
        sendOtp(phone,purpose)
    }

    fun resetSendOtpState() {
        _sendOtpState.value = Resource.Idle()
    }
    fun resetVerifyOtpState(){
        _verifyOtp.value = Resource.Idle()
    }

    fun login(username: String, password: String) {
        _loginState.value = LoginState(isLoading = true)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // send `token` to your server using an API
                viewModelScope.launch {
                    try {
                        val response = repository.login(username, password,token)

                        if (response.status == true) {
                            val role = when {
                                response.message?.contains(
                                    "vehicle",
                                    ignoreCase = true
                                ) == true -> "driver"

                                response.message?.contains(
                                    "parent",
                                    ignoreCase = true
                                ) == true -> "parent"

                                else -> null
                            }

                            role?.let {
                                when (it) {
                                    "driver" -> {
                                        Log.d("TAG", "login driver Token - $token")
                                        val driverData =
                                            Gson().fromJson(response.data, DriverData::class.java)
                                        _loginState.value = LoginState(
                                            success = true,
                                            message = "Vehicle Owner login success",
                                            driver = driverData
                                        )
                                        userPreferences.saveLoginState(true)  // Save login state

                                        userPreferences.saveLoginUserDetails(
                                            driverData.id,
                                            driverData.driver_name,
                                            it,
                                            driverData.refer_id.toString()
                                        )
                                    }

                                    "parent" -> {
                                        Log.d("TAG", "login parent Token - $token")

                                        val parentData =
                                            Gson().fromJson(response.data, ParentData::class.java)
                                        _loginState.value = LoginState(
                                            success = true,
                                            message = "Parent login success",
                                            parent = parentData
                                        )
                                        userPreferences.saveLoginState(true)  // Save login state

                                        parentData.id?.let { it1 ->
                                            parentData.parentName?.let { it2 ->
                                                parentData.referId?.let { refer_code ->
                                                    userPreferences.saveLoginUserDetails(
                                                        it1, it2, it, refer_code
                                                    )
                                                }
                                            }
                                        }
                                        parentData.vehicleId?.let { it1 ->
                                            parentData.status?.let { it2 ->
                                                userPreferences.updateVehicleDetails(
                                                    it1, it2
                                                )
                                            }
                                        }
                                    }

                                    else -> {}
                                }
                            } ?: run {
                                _loginState.value = LoginState(error = "Unknown user type")
                            }

                        } else {
                            _loginState.value =
                                LoginState(error = response.message ?: "Login failed.")
                        }


                    } catch (e: Exception) {
                        val errorMessage = when (e) {
                            is HttpException -> {
                                try {
                                    val errorBody = e.response()?.errorBody()?.string()
                                    val errorResponse =
                                        Gson().fromJson(errorBody, LoginResponse::class.java)
                                    errorResponse?.message ?: "Login failed with code ${e.code()}"
                                } catch (ex: Exception) {
                                    "Login failed with code ${e.code()}"
                                }
                            }

                            else -> "Something went wrong: ${e.localizedMessage}"
                        }

                        _loginState.value = LoginState(error = errorMessage)
                        Log.e("LoginViewModel", "Login error", e)
                    }
                }

            }else{
                _loginState.value = LoginState(error = "Unable to generate device token")
            }
        }
    }
    fun generateAccessTokenFromServiceAccount(context: Context, onResult: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("serviceAccountKey.json")
                val credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
                credentials.refreshIfExpired()
                val token = credentials.accessToken.tokenValue

                withContext(Dispatchers.Main) {
                    onResult(token)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            userPreferences.saveLoginState(false)  // Clear login state
            userPreferences.clearUserData()
            _loginState.value = LoginState()
            clearResponses()
            clearFields()
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState()  // Reset state to default
    }

    fun onStateSelected(state: State) {
        _selectedState.value = state
        _selectedDistrict.value = null  // Reset district selection
        _selectedTaluka.value = null    // Reset taluka selection
        _selectedSchool.value = null    // Reset school selection

        _districtOptions.value = emptyList()
        _talukaOptions.value = emptyList()
        _schoolOptions.value = emptyList()
        _driverOptions.value = emptyList()

        loadDistrictOptions(state.id)
    }

    fun onDistrictSelected(district: District) {
        _selectedDistrict.value = district
        _selectedTaluka.value = null   // Reset taluka selection
        _selectedSchool.value = null   // Reset school selection
        _talukaOptions.value = emptyList()
        _schoolOptions.value = emptyList()
        _driverOptions.value = emptyList()

        _selectedState.value?.id?.let { stateId ->
            loadTalukaOptions(stateId, district.id)
        }
    }

    fun onTalukaSelected(taluka: Taluka) {
        _selectedTaluka.value = taluka
        _selectedSchool.value = null  // Reset school selection
        _schoolOptions.value = emptyList()
        _driverOptions.value = emptyList()

        _selectedState.value?.id?.let { stateId ->
            _selectedDistrict.value?.id?.let { districtId ->
                loadSchoolsOptions(stateId, districtId, taluka.id)
            }
        }
    }

    fun onSchoolSelected(school: School?) {
        _selectedSchool.value = school
    }

    fun onAllSchoolSelected(school: School) {
        _selectedAllSchool.value = school
    }

    fun resetSchoolRegistrationDropDowns() {
        _selectedState.value = null
        _selectedDistrict.value = null
        _selectedTaluka.value = null
        _selectedSchool.value = null
        _selectedAllSchool.value = null

        _districtOptions.value = emptyList()
        _talukaOptions.value = emptyList()
        _schoolOptions.value = emptyList()
        _driverOptions.value = emptyList()
    }

    fun loadStateOptions() = viewModelScope.launch {
        try {
            val response = repository.getStates()
            _stateOptions.value = response.data?.takeIf { it.isNotEmpty() } ?: listOf(
                State(
                    id = "", state_name = "No data found"
                )
            )
            Log.d("TAG", "States loaded: ${_stateOptions.value}")
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Failed to load states", e)
        }
    }

    fun loadAllRequests(driver_id: String) = viewModelScope.launch {
        try {
            val response = repository.getDriverRequests(driver_id)
            _driverAllRequest.value = response.data.takeIf { it.isNotEmpty() } ?: listOf(
                RequestData(
                    id = "",
                    vehicleId = "",
                    message = "",
                    parentId = "",
                    status = "",
                    parentName = "No data found",
                    contactNumber = "",
                    password = "",
                    state = "",
                    district = "",
                    taluka = "",
                    city = "",
                    parentAddress = "",
                    childName = "",
                    childSchoolName = "",
                    childClass = "",
                    childDob = "",
                    pickUp = "",
                    dropOff = "",
                    numberOfChlid = "",
                    emergencyContact = "",
                    termsCondition = "",
                    role = "",
                    schoolId = ""
                )
            )
            Log.d("TAG", "States loaded: ${_driverAllRequest.value}")
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Failed to load schools", e)
        }
    }

    fun selectDriverRequest(driver: DriverMob) {
        _selectedDriverRequest.value = driver
    }

    fun clearSelectedRequest() {
        _selectedDriverRequest.value = null
    }

    fun loadDistrictOptions(stateId: String) = viewModelScope.launch {
        try {
            val response = repository.getDistricts(stateId)
            _districtOptions.value = response.data?.takeIf { it.isNotEmpty() } ?: listOf(
                District(
                    id = "", "", district_name = "No data found"
                )
            )
            Log.d("TAG", "Districts loaded: ${_districtOptions.value}")
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Failed to load districts", e)
        }
    }

    fun loadTalukaOptions(stateId: String, districtId: String) = viewModelScope.launch {
        try {
            val response = repository.getTalukas(stateId, districtId)
            _talukaOptions.value = response.data?.takeIf { it.isNotEmpty() } ?: listOf(
                Taluka(
                    id = "", "", "", taluka_name = "No data found"
                )
            )
            Log.d("TAG", "Talukas loaded: ${_talukaOptions.value}")
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Failed to load talukas", e)
        }
    }

    fun loadSchoolsOptions(stateId: String, districtId: String, talukaId: String) =
        viewModelScope.launch {
            try {
                val response = repository.getSchools(stateId, districtId, talukaId)
                val schoolsFromServer = response.data.orEmpty()

                _schoolOptions.value = if (schoolsFromServer.isNotEmpty()) {
                    schoolsFromServer + School(
                        id = "other", schoolName = "Other",
                        "", "", "",
                        "", "", ""
                    )
                } else {
                    listOf(
                        School(
                            id = "", schoolName = "No data found",
                            "", "", "",
                            "", "", ""
                        )
                    )
                }

                Log.d("TAG", "Schools loaded: ${_schoolOptions.value}")
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Failed to load schools", e)
            }
        }

    fun loadDriverList(schoolId: String) = viewModelScope.launch {
        try {
            val response = repository.getDriver(schoolId)
            _driverOptions.value = response.data?.takeIf { it.isNotEmpty() } ?: listOf(
                DriverMob(
                    id = "",
                    driverName = "No data found",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
            Log.d("TAG", "Driver list loaded: ${_driverOptions.value}")
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Failed to load driver list", e)
        }
    }

    fun loadParentList(schoolId: String) = viewModelScope.launch {
        try {
            val response = repository.getParent(schoolId)
            _parentsOptions.value = response.data?.takeIf { it.isNotEmpty() } ?: listOf(
                Parent(
                    id = null,
                    parentName = "No data found",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
            Log.d("TAG", "Parent list loaded: ${_parentsOptions.value}")
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Failed to load parent list", e)
        }
    }

    fun clearParentList() {
        _parentsOptions.value = emptyList()
    }

    fun registerDriver(
        ownerName: String,
        contactNumber: String,
        password: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        address: String,
        aadharPhoto: String,
        licensePhoto: String,
        vehicleRegNumber: String,
        vehicleModel: String,
        seatingCapacity: String,
        vehicleType: String,
        insurancePhoto: String,
        fitnessCertificate: String,
        vehiclePhoto: String,
        areasCovered: String,
        schoolServiced: String,
        profilePicture: String,
        aboutMe: String,
        verificationState: String,
        availabilityStatus: String,
        termsAccepted: String,
        referCode: String,
        referby: String
    ) = viewModelScope.launch {
        try {
            val response = repository.registerDriver(
                ownerName,
                contactNumber,
                password,
                state,
                district,
                taluka,
                city,
                address,
                aadharPhoto,
                licensePhoto,
                vehicleRegNumber,
                vehicleModel,
                seatingCapacity,
                vehicleType,
                insurancePhoto,
                fitnessCertificate,
                vehiclePhoto,
                areasCovered,
                schoolServiced,
                profilePicture,
                aboutMe,
                verificationState,
                availabilityStatus,
                termsAccepted,
                referCode,
                referby
            )
            if (response.status == true) {
                _driverRegistrationSuccess.value = response
            } else {
                _driverRegistrationSuccess.value = response
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Driver Registration failed", e)
        }
    }

    fun resetDriverRegistrationResult() {
        _driverRegistrationSuccess.value = null
    }

    fun registerParent(
        parentName: String,
        contactNumber: String,
        password: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        address: String,
        childName: String,
        childClass: String,
        schoolName: String,
        dob: String,
        pickupLocation: String,
        dropOffLocation: String,
        numberOfChildren: String,
        emergencyContact: String,
        termsAccepted: String,
        referCode: String,
        referby: String
    ) = viewModelScope.launch {
        try {
            val response = repository.registerParent(
                parentName,
                contactNumber,
                password,
                state,
                district,
                taluka,
                city,
                address,
                childName,
                childClass,
                schoolName,
                dob,
                pickupLocation,
                dropOffLocation,
                numberOfChildren,
                emergencyContact,
                termsAccepted,
                referCode,
                referby
            )
            if (response.status == true) {
                _parentRegistrationSuccess.value = response
            } else {
                _parentRegistrationSuccess.value = response
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Parent Registration failed", e)
        }
    }

    fun resetParentRegistrationResult() {
        _parentRegistrationSuccess.value = null
    }

    fun registerSchool(schoolRegistrationRequest: SchoolRegistrationRequest) =
        viewModelScope.launch {
            try {
                val response = repository.registerSchool(schoolRegistrationRequest)
                if (response.status) {
                    Log.d("TAG", "School Registration Successful: ${response.message}")
                    _schoolRegistrationSuccess.value = true
                } else {
                    Log.e("TAG", "School Registration Failed: ${response.message}")
                    _schoolRegistrationSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error during school registration", e)
            }
        }

    fun onNewPasswordChange(password: String) {
        _state.value = _state.value.copy(newPassword = password)
    }

    fun onConfirmPasswordChange(password: String) {
        _state.value = _state.value.copy(confirmPassword = password)
    }

    fun updatePassword(phone: String) {
        if (!validatePasswords()) return

        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val response = repository.updatePassword(
                    phone, _state.value.newPassword
                )
                if (response.status) {
                    _state.value = _state.value.copy(isPasswordUpdated = true, isLoading = false)
                } else {
                    _state.value = _state.value.copy(
                        errorMessage = "Failed to update password. Try again.", isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "An error occurred: ${e.message}", isLoading = false
                )
            }
        }
    }

    fun clearFields() {
        _state.value = _state.value.copy(
            newPassword = "",
            confirmPassword = "",
            isPasswordUpdated = false  // Reset the flag after successful navigation
        )
    }

    private fun validatePasswords(): Boolean {
        val stateValue = _state.value
        return when {
            stateValue.newPassword.length < 6 -> {
                _state.value =
                    stateValue.copy(errorMessage = "New password must be at least 6 characters.")
                false
            }

            stateValue.newPassword != stateValue.confirmPassword -> {
                _state.value = stateValue.copy(errorMessage = "Passwords do not match.")
                false
            }

            else -> true
        }
    }

    fun loadProfile(userId: String, userType: String) {
        viewModelScope.launch {
            try {
                isLoading = true

                _profileData.value = if (userType == "parent") {
                    repository.getParentProfile(userId).data.firstOrNull()
                } else {
                    repository.getDriverProfile(userId).data.firstOrNull()
                }
            } catch (e: UnknownHostException) {
                Log.e("Profile", "No Internet Connection: ${e.localizedMessage}")
                _profileData.value = null // Or show error UI
            } catch (e: Exception) {
                Log.e("Profile", "Error: ${e.localizedMessage}")
                _profileData.value = null // Or show fallback
            } finally {
                isLoading = false
            }
        }
    }


    private val _profileData = MutableStateFlow<Any?>(null)
    val profileData: StateFlow<Any?> = _profileData.asStateFlow()

    var isLoading by mutableStateOf(false)

    private val _isAssigningSchool = MutableStateFlow(false)
    val isAssigningSchool: StateFlow<Boolean> = _isAssigningSchool

    fun findDriverByMobile(mobile: String) {
        viewModelScope.launch {
            _isAssigningSchool.value = true
            try {
                val driverByMob = repository.getDriverByMob(mobile)
                foundDriver.value = driverByMob.driverMob
            } catch (e: Exception) {
                Log.d("TAG", "findDriverByMobile: Driver not found or error occurred")
            } finally {
                _isAssigningSchool.value = false
            }
        }
    }

    fun clearFindDriverByMobile() {
        foundDriver.value = null
    }


    private val _assignRequestResponse = MutableStateFlow<SendRequestToDriverResponse?>(null)
    val assignRequestResponse: StateFlow<SendRequestToDriverResponse?> = _assignRequestResponse


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Send Assign Request
    fun sendAssignRequest(vehicleId: String, parentId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.sendAssignRequest(vehicleId, parentId)
                _assignRequestResponse.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun clearResponses() {
        _assignRequestResponse.value = null
        _errorMessage.value = null
    }

    val updatingRequestId = MutableStateFlow<String?>(null)
    val isUpdatingRequest = MutableStateFlow(false)
    val updateMessage = MutableStateFlow<String?>(null)

    fun updateRequestStatus(parentId: String, status: String) {
        viewModelScope.launch {
            updatingRequestId.value = parentId
            isUpdatingRequest.value = true
            try {
                val response = repository.updateAssignRequest(parentId, status)
                updateMessage.value = response.message ?: "Updated successfully"

                // 👇 Reload list after update
                loadDriverRequests(userId.value.toString())

            } catch (e: Exception) {
                updateMessage.value = e.message ?: "Failed to update"
            } finally {
                isUpdatingRequest.value = false
                updatingRequestId.value = null
            }
        }
    }

    fun clearUpdateMessage() {
        updateMessage.value = null
    }


    //////////////////////////////////////////////////////////////
    private val _driverRequests = MutableStateFlow<List<RequestData>>(emptyList())
    val driverRequests: StateFlow<List<RequestData>> = _driverRequests

    var toastMessage by mutableStateOf<String?>(null)
        private set

    fun loadDriverRequests(driverId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getDriverRequests(driverId)
                _driverRequests.value = response.data
                Log.e("TAG", "loadDriverRequests: ${_driverRequests.value}")
            } catch (e: Exception) {
                toastMessage = "Failed to load requests: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearToastMessage() {
        toastMessage = null
    }

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _uploadMessage = MutableStateFlow<String?>(null)
    val uploadMessage: StateFlow<String?> = _uploadMessage

    fun uploadVehiclePhotos(
        userId: Int,
        frontBase64: String,
        backBase64: String,
        insideBase64: String,
        outsideBase64: String
    ) {
        viewModelScope.launch {
            _isUploading.value = true

            try {
                val photoOfVehicle = PhotoOfVehicle().apply {
                    if (frontBase64.isNotBlank()) frontImage = frontBase64
                    if (backBase64.isNotBlank()) backImage = backBase64
                    if (insideBase64.isNotBlank()) insideImage = insideBase64
                    if (outsideBase64.isNotBlank()) otherImage = outsideBase64
                }
                val hasAtLeastOneImage = listOf(
                    photoOfVehicle.frontImage,
                    photoOfVehicle.backImage,
                    photoOfVehicle.insideImage,
                    photoOfVehicle.otherImage
                ).any { !it.isNullOrBlank() }

                if (!hasAtLeastOneImage) {
                    _uploadMessage.value = "Please select at least one image to upload."
                    _isUploading.value = false
                    return@launch
                }
                val request = UpdateVehicleImageRequest(
                    id = userId,
                    photoOfVehicle = photoOfVehicle
                )

                val response = repository.updateVehiclePhotos(request)

                _uploadMessage.value = response.message ?: if (response.status == true) {
                    "Upload successful"
                } else {
                    "Upload failed"
                }

            } catch (e: Exception) {
                _uploadMessage.value = "Exception occurred: ${e.localizedMessage}"
                Log.e("Upload", "Error uploading vehicle photos", e)
            }

            _isUploading.value = false
        }
    }

    fun clearUploadMessage() {
        _uploadMessage.value = null
    }

    private val _paymentStatusState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentStatusState: StateFlow<PaymentState> = _paymentStatusState

    fun updatePaymentStatus(
        id: Int,
        transactionId: String,
        amount: String,
        paymentStatus: String,
        expireDate: String,
        paymentDate: String,
        assignStatus: String,
        assignDate: String
    ) {
        viewModelScope.launch {
            _paymentStatusState.value = PaymentState.Loading
            try {
                val response = repository.updatePaymentStatus(
                    id,
                    transactionId,
                    amount,
                    paymentStatus,
                    expireDate,
                    paymentDate,
                    assignStatus,
                    assignDate
                )
                if (response.status == "success") {
                    _paymentStatusState.value = PaymentState.Success(response.message)
                } else {
                    _paymentStatusState.value =
                        PaymentState.Error(response.message ?: "Unknown error")
                }

            } catch (e: Exception) {
                _paymentStatusState.value =
                    PaymentState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    private val _vehiclePhotos = MutableStateFlow<List<String>>(emptyList())
    val vehiclePhotos: StateFlow<List<String>> = _vehiclePhotos.asStateFlow()

    fun getVehiclePhotos() {
        viewModelScope.launch {
            try {
                val response = repository.getVehiclePhotos(userId.value.toString())

                if (response.status == true) {
                    // Clean and normalize URLs
                    val baseUrl = "https://avschoolerp.com/"
                    val formattedPhotos = response.photos.map { photo ->
                        if (photo.startsWith("http")) photo else baseUrl + photo
                    }
                    _vehiclePhotos.value = formattedPhotos
                } else {
                    // Handle false status case if needed
                    Log.e("VehiclePhotos", "Status false from server")
                }
            } catch (e: Exception) {
                Log.e("VehiclePhotos", "Error fetching vehicle photos: ${e.localizedMessage}")
            }
        }
    }

    private val _isProfileUpdating = MutableStateFlow(false)
    val isProfileUpdating: StateFlow<Boolean> = _isProfileUpdating

    private val _updateProfileMessage = MutableStateFlow<String?>(null)
    val updateProfileMessage: StateFlow<String?> = _updateProfileMessage

    fun updateProfile(
        context: Context,
        userId: String,
        userType: String,
        name: String,
        contact: String,
        address: String,
        childName: String,
        schoolName: String,
        mobile: String,
        vehicle: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isProfileUpdating.value = true
            _updateProfileMessage.value = null

            try {
                val profilePicBase64 = uriToBase64(context, imageUri)

                val result = when (userType.lowercase()) {
                    "driver" -> {
                        repository.updateProfileDriver(
                            userId,
                            name,
                            mobile,
                            vehicle,
                            state,
                            district,
                            taluka,
                            city,
                            address,
                            schoolName,
                            profilePicBase64
                        )
                    }

                    "parent" -> {
                        repository.updateProfileParent(
                            userId,
                            name,
                            contact,
                            address,
                            childName,
                            schoolName,
                            profilePicBase64
                        )
                    }

                    else -> throw IllegalArgumentException("Unknown user type")
                }

                if (result.status == true) {
                    _updateProfileMessage.value = "Profile updated successfully"
                    loadProfile(userId, userType) // refresh after update
                } else {
                    _updateProfileMessage.value = "Update failed: ${result.message}"
                }

            } catch (e: Exception) {
                _updateProfileMessage.value = "An error occurred: ${e.localizedMessage}"
            } finally {
                _isProfileUpdating.value = false
            }
        }
    }

    fun uriToBase64(context: Context, uri: Uri?): String? {
        if (uri == null) return null
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }


    private val _schoolRegistrationPSuccess = MutableStateFlow(false)
    val schoolRegistrationPSuccess: StateFlow<Boolean> = _schoolRegistrationPSuccess

    private val _schoolRegistrationDSuccess = MutableStateFlow(false)
    val schoolRegistrationDSuccess: StateFlow<Boolean> = _schoolRegistrationDSuccess

    fun registerSchoolParent(
        parentId: String,
        schoolId: String,
        schoolName: String,
        contactNumber: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        schoolAddress: String,

        ) {
        viewModelScope.launch {
            try {
                val response = repository.parentSchoolOnRegister(
                    parentId,
                    schoolId,
                    schoolName,
                    contactNumber,
                    state,
                    district,
                    taluka,
                    city,
                    schoolAddress
                )

                if (response.status == true) {
                    Log.d("TAG", "School Registration (Parent) Successful: ${response.message}")
                    _schoolRegistrationPSuccess.value = true
                } else {
                    Log.e("TAG", "School Registration (Parent) Failed: ${response.message}")
                    _schoolRegistrationPSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error during school registration (Parent)", e)
            }
        }
    }

    fun registerSchoolDriver(
        vehicleId: String,
        schoolId: String,
        schoolName: String,
        contactNumber: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        schoolAddress: String
    ) {
        viewModelScope.launch {
            try {
                val response = repository.driverSchoolOnRegister(
                    vehicleId,
                    schoolId,
                    schoolName,
                    contactNumber,
                    state,
                    district,
                    taluka,
                    city,
                    schoolAddress
                )
                if (response.status == true) {
                    Log.d("TAG", "School Registration (Driver) Successful: ${response.message}")
                    _schoolRegistrationDSuccess.value = true
                } else {
                    Log.e("TAG", "School Registration (Driver) Failed: ${response.message}")
                    _schoolRegistrationDSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error during school registration (Driver)", e)
            }
        }
    }

    private val _uploadDocSuccess = MutableStateFlow<DocumentUploadResponse?>(null)
    val uploadDocSuccess: StateFlow<DocumentUploadResponse?> = _uploadDocSuccess

    fun uploadDocumentsToDatabase(data: Map<String, String>) {
        viewModelScope.launch {
            try {
                val response = repository.uploadDocumentsToDatabase(
                    data["id"] ?: "",
                    data["profile_picture"] ?: "",
                    data["adhar_number"] ?: "",
                    data["driver_license"] ?: "",
                    data["insurance_detail"] ?: "",
                    data["fitness_certificat"] ?: "",
                    data["photo_of_vehicle"] ?: ""
                )

                if (response.status) {
                    _uploadDocSuccess.value = response
                } else {
                    Log.e("TAG", "Upload failed: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error while uploading documents ", e)
            }
        }
    }

    fun clearUploadDocToDatabase() {
        _uploadDocSuccess.value = null
    }

    private val _referBySuccess = MutableStateFlow<ReferByResponse?>(null)
    val referBySuccess: StateFlow<ReferByResponse?> = _referBySuccess

    fun checkReferBy(referby: String) {
        viewModelScope.launch {
            try {
                val response = repository.checkReferBy(referby)
                _referBySuccess.value = response
            } catch (e: Exception) {
                Log.e("TAG", "Error while verifying refer code ", e)
                _referBySuccess.value = null // Optional: Reset on failure
            }
        }
    }

    fun clearReferByResponse() {
        _referBySuccess.value = null
    }


    private val _commissionState =
        MutableStateFlow<Resource<CommissionResponse>>(Resource.Loading())
    val commissionState: StateFlow<Resource<CommissionResponse>> = _commissionState

    fun getCommission(driver_id: String) {
        viewModelScope.launch {
            _commissionState.value = Resource.Loading() // Emit loading state
            try {
                val response = repository.getCommission(driver_id)
                _commissionState.value = Resource.Success(response)
            } catch (e: Exception) {
                Log.e("TAG", "Error while fetching commission", e)
                _commissionState.value = Resource.Error(e.message ?: "Something went wrong", e)
            }
        }
    }
}





