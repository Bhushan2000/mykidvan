package com.vihaanshika.mykidsvan.android.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "user_prefs")
    private val dataStore = context.dataStore

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val ASSIGNED_VEHICLE_ID = stringPreferencesKey("assign_vehicle_id")
        private val STATUS = stringPreferencesKey("status")
        private val PAYMENT_STATUS = stringPreferencesKey("payment_status")
        private val REFER_CODE = stringPreferencesKey("refer_code")
        private val TRACKING_STATUS = stringPreferencesKey("tracking_status")
        private val TRIAL_DATE = stringPreferencesKey("trial_date")
        private val SCHOOL_PROFILE_URL = stringPreferencesKey("school_profile_url")
        private val PAY_AMOUNT = stringPreferencesKey("pay_amount")
    }

    // Save basic login details
    suspend fun saveLoginUserDetails(
        id: String,
        name: String,
        role: String,
        referCode: String,
        trialDate: String,
        schoolProfileUrl: String,
        payAmount: String
    ) {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_ID] = id
            prefs[USER_NAME] = name
            prefs[USER_ROLE] = role
            prefs[REFER_CODE] = referCode
            prefs[TRIAL_DATE] = trialDate
            prefs[SCHOOL_PROFILE_URL] = schoolProfileUrl
            prefs[PAY_AMOUNT] = payAmount
        }
    }

    suspend fun saveLoginUserDetails(
        id: String,
        name: String,
        role: String,
        referCode: String,
        trialDate: String,
        schoolProfileUrl: String,
        payAmount: String,
        vehicleId: String,
        status: String,
        paymentStatus: String
    ) {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_ID] = id
            prefs[USER_NAME] = name
            prefs[USER_ROLE] = role
            prefs[REFER_CODE] = referCode
            prefs[TRIAL_DATE] = trialDate
            prefs[SCHOOL_PROFILE_URL] = schoolProfileUrl
            prefs[PAY_AMOUNT] = payAmount

            // Required vehicle-related values
            prefs[ASSIGNED_VEHICLE_ID] = vehicleId
            prefs[STATUS] = status
            prefs[PAYMENT_STATUS] = paymentStatus
        }
    }


    // Update vehicle details
    suspend fun updateVehicleDetails(vehicleId: String?, status: String?, paymentStatus: String?) {
        dataStore.edit { prefs ->
            vehicleId?.let { prefs[ASSIGNED_VEHICLE_ID] = it }
            status?.let { prefs[STATUS] = it }
            paymentStatus?.let { prefs[PAYMENT_STATUS] = it }
        }
    }

    // Save tracking status
    suspend fun saveTrackingStatus(status: String) {
        dataStore.edit { prefs ->
            prefs[TRACKING_STATUS] = status
        }
    }

    // Save login state separately
    suspend fun saveLoginState(isLoggedIn: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = isLoggedIn
        }
    }

    // Save only driver ID (helper function)
    suspend fun saveDriverId(driverId: String) {
        dataStore.edit { prefs ->
            prefs[ASSIGNED_VEHICLE_ID] = driverId
        }
    }

    // Clear all stored user data
    suspend fun clearUserData() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    // ────── Flows to observe values ──────

    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val userIdFlow: Flow<String?> = dataStore.data.map { it[USER_ID] }
    val userNameFlow: Flow<String?> = dataStore.data.map { it[USER_NAME] }
    val userRoleFlow: Flow<String?> = dataStore.data.map { it[USER_ROLE] }
    val assignVehicleIdFlow: Flow<String?> = dataStore.data.map { it[ASSIGNED_VEHICLE_ID] }
    val statusFlow: Flow<String?> = dataStore.data.map { it[STATUS] }
    val paymentStatusFlow: Flow<String?> = dataStore.data.map { it[PAYMENT_STATUS] }
    val referCodeFlow: Flow<String?> = dataStore.data.map { it[REFER_CODE] }
    val trackingStatusFlow: Flow<String?> = dataStore.data.map { it[TRACKING_STATUS] }
    val trialDateFlow: Flow<String?> = dataStore.data.map { it[TRIAL_DATE] }
    val schoolPictureUrlFlow: Flow<String?> = dataStore.data.map { it[SCHOOL_PROFILE_URL] }
    val payAmountFlow: Flow<String?> = dataStore.data.map { it[PAY_AMOUNT] }
}
