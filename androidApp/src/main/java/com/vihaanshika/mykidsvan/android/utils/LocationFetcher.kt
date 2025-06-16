package com.vihaanshika.mykidsvan.android.utils

interface LocationFetcher {
    fun startFetchingFromServer(role:String,assignVehicleId:String,trackingStatus: String)
}