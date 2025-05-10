package com.example.mykidsvan.android.utils

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LatLngPrefs {
    private const val PREF_NAME = "lat_lng_prefs"
    private const val KEY_POINTS = "lat_lng_list"

    fun saveLatLng(context: Context, latLng: LatLng) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val list = getLatLngList(context).toMutableList()
        if (list.contains(latLng)) return // Avoid duplicate
        list.add(latLng)
        val json = Gson().toJson(list)
        prefs.edit().putString(KEY_POINTS, json).apply()
    }

    fun getLatLngList(context: Context): List<LatLng> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_POINTS, null) ?: return emptyList()
        val type = object : TypeToken<List<LatLng>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
