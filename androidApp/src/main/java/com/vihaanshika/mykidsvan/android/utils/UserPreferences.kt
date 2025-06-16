package com.vihaanshika.mykidsvan.android.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(context: Context) {
    // Extension function to create DataStore instance at the top-level
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")
    private val dataStore = context.dataStore  // Use the new way to initialize DataStore

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val ASSIGNED_VEHICLE_ID = stringPreferencesKey("assign_vehicle_id")
        private val STATUS = stringPreferencesKey("status")
        private val REFER_CODE = stringPreferencesKey("refer_code")
        private val TRACKING_STATUS = stringPreferencesKey("tracking_status")
    }

    // Save login state (suspend function)
    suspend fun saveLoginState(isLoggedIn: Boolean) {
        dataStore.edit { prefs -> prefs[IS_LOGGED_IN] = isLoggedIn }
    }

    suspend fun saveLoginUserDetails(id: String, name: String,role:String,refer_code: String) {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_ID] = id
            prefs[USER_NAME] = name
            prefs[USER_ROLE] = role
            prefs[REFER_CODE] = refer_code
        }
    }

    suspend fun updateVehicleDetails(vehicleId: String, status: String) {
        dataStore.edit { prefs ->
            prefs[ASSIGNED_VEHICLE_ID] = vehicleId
            prefs[STATUS] = status
        }
    }

    suspend fun saveTrackingStatus(status: String){
        dataStore.edit { prefs->
            prefs[TRACKING_STATUS] = status
        }
    }

    val assignVehicleIdFlow: Flow<String?> = dataStore.data.map { it[ASSIGNED_VEHICLE_ID] }
    val statusFlow: Flow<String?> = dataStore.data.map { it[STATUS] }
    val trackingStatusFlow: Flow<String?> = dataStore.data.map { it[TRACKING_STATUS] }

    suspend fun clearUserData() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun saveDriverId(driverId: String) {
        dataStore.edit { preferences ->
            preferences[ASSIGNED_VEHICLE_ID] = driverId
        }
    }

    // Flow to observe login state
    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false  // Return false if key is absent
    }

    val userIdFlow: Flow<String?> = dataStore.data.map { it[USER_ID] }

    val userRole:Flow<String?> = dataStore.data.map { it[USER_ROLE] }

    val referCode:Flow<String?> = dataStore.data.map { it[REFER_CODE] }

 }
