package com.example.mykidsvan.android.utils

import android.content.Context
import com.google.android.gms.maps.model.LatLng

object SharedPrefUtil {
    private const val PREF_NAME = "tracking_prefs"
    private const val KEY_COORDS = "saved_coords"

    fun saveLatLng(context: Context, latLng: LatLng) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val current = getSavedLatLngList(context).toMutableList()
        current.add("${latLng.latitude},${latLng.longitude}")
        prefs.edit().putStringSet(KEY_COORDS, current.toSet()).apply()
    }

    fun getSavedLatLngList(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_COORDS, emptySet())?.toList() ?: emptyList()
    }

    fun clearSavedLatLng(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_COORDS).apply()
    }
}
