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

sealed class DrawerItem(val title: String, val route: String, @DrawableRes val iconRes: Int) {

    object Home : DrawerItem("Home", "home", R.drawable.ic_home)
    object Profile : DrawerItem("Profile", "profile", R.drawable.ic_profile)
    object FindStudent : DrawerItem("Find Student", "find_student", R.drawable.ic_find_student)
    object AssignedStudent :
        DrawerItem("Assigned Student", "assigned_student", R.drawable.ic_assigned_student)

    object RegisterSchool : DrawerItem("Register School", "register_school", R.drawable.ic_school)
    object VehiclePhoto :
        DrawerItem("Add Photos", "add_vehicle_photos", R.drawable.ic_find_vehicle)

    object ReferApp : DrawerItem("Refer App", "refer_app", R.drawable.ic_share)

    object FindVehicle : DrawerItem("Find Vehicle", "find_vehicle", R.drawable.ic_find_vehicle)
    object AssignedVehicle :
        DrawerItem("Assigned Vehicle", "assigned_vehicle", R.drawable.ic_assigned_vehicle)

    object Message : DrawerItem("Message", "message", R.drawable.ic_message)
    object SupportHelp : DrawerItem("Support/Help", "support_help", R.drawable.ic_help)
}

