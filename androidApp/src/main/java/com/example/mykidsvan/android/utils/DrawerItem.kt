package com.example.mykidsvan.android.utils

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mykidsvan.android.R


sealed class DrawerItem(val title: String, val route: String, val iconRes: Int) {
    object Home : DrawerItem(Constants.TITLE_HOME, Constants.ROUTE_HOME, R.drawable.ic_home)
    object Profile : DrawerItem(Constants.TITLE_PROFILE, Constants.ROUTE_PROFILE, R.drawable.ic_profile)
    object FindVehicle : DrawerItem(Constants.TITLE_FIND_VEHICLE, Constants.ROUTE_FIND_VEHICLE, R.drawable.ic_find_vehicle)
    object VehicleDetails : DrawerItem(Constants.TITLE_VEHICLE_DETAILS, Constants.ROUTE_VEHICLE_DETAILS, R.drawable.vehicle_details)
    object RegisterSchool : DrawerItem(Constants.TITLE_REGISTER_SCHOOL, Constants.ROUTE_REGISTER_SCHOOL, R.drawable.ic_school)
    object Message : DrawerItem(Constants.TITLE_MESSAGES, Constants.ROUTE_MESSAGES, R.drawable.ic_message)
    object SupportHelp : DrawerItem(Constants.TITLE_SUPPORT, Constants.ROUTE_SUPPORT, R.drawable.ic_help)
    object FindStudent : DrawerItem(Constants.TITLE_FIND_STUDENT, Constants.ROUTE_FIND_STUDENT, R.drawable.ic_find_student)
    object AssignedStudent : DrawerItem(Constants.TITLE_ASSIGNED_STUDENT, Constants.ROUTE_ASSIGNED_STUDENT, R.drawable.ic_assigned_student)
    object VehiclePhoto : DrawerItem(Constants.TITLE_VEHICLE_PHOTO, Constants.ROUTE_VEHICLE_PHOTO, R.drawable.ic_find_vehicle)
    object ReferApp : DrawerItem(Constants.TITLE_REFER_APP, Constants.ROUTE_REFER_APP, R.drawable.ic_share)
}

