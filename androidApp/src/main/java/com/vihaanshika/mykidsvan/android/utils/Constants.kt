package com.vihaanshika.mykidsvan.android.utils

object Constants {
    // api
    const val BASE_URL = "https://avschoolerp.com/"

    // location service
    const val LOCATION_BROADCAST_ACTION = "LOCATION_UPDATE"
    const val CHANNEL_ID = "location_channel"
    const val CHANNEL_NAME = "Location Tracking"

    // razorpay
    const val AMOUNT_IN_PAISE = 10000 // ₹100
    const val RAZORPAY_TEST_KEY = "rzp_test_BVJygtmA6ljXBB"
    const val PAYMENT_NAME = "Assign Driver"
    const val PAYMENT_DESCRIPTION = "Driver Assignment"
    const val PAYMENT_CURRENCY = "INR"
    const val PREFILL_EMAIL = "example@example.com"
    const val PREFILL_CONTACT = "9876543210"

    // app name
    const val MY_KID_VAN = "My Kids Van"

    // screens and routes for DrawerItem
    const val TITLE_HOME = "Home"
    const val ROUTE_HOME = "home"

    const val TITLE_PROFILE = "Profile"
    const val ROUTE_PROFILE = "profile"

    const val TITLE_FIND_VEHICLE = "Find Vehicle"
    const val ROUTE_FIND_VEHICLE = "find_vehicle"

    const val TITLE_VEHICLE_DETAILS = "Vehicle Details"
    const val ROUTE_VEHICLE_DETAILS = "vehicle_details"

    const val TITLE_REGISTER_SCHOOL = "Register School"
    const val ROUTE_REGISTER_SCHOOL = "register_school"

    const val TITLE_MESSAGES = "Messages"
    const val ROUTE_MESSAGES = "message"

    const val TITLE_SUPPORT = "Support"
    const val ROUTE_SUPPORT = "support_help"

    const val TITLE_FIND_STUDENT = "Find Student"
    const val ROUTE_FIND_STUDENT = "find_student"

    const val TITLE_ASSIGNED_STUDENT = "Assigned Student"
    const val ROUTE_ASSIGNED_STUDENT = "assigned_student"

    const val TITLE_VEHICLE_PHOTO = "Vehicle Photo"
    const val ROUTE_VEHICLE_PHOTO = "vehicle_photo"

    const val TITLE_REFER_APP = "Refer App"
    const val ROUTE_REFER_APP = "refer_app"

    // user types
    const val USER_DRIVER ="driver"
    const val USER_PARENT ="parent"

    // logout dialog
    const val YES = "Yes"
    const val No = "No"
    // tracking button
    const val START = "Start"
    const val STOP = "Stop"

    // refer app


    // support app
    const val HELP_SCREEN_SEARCH_HINT = "Search for help..."
    const val ACCOUNT_ISSUES_TITLE = "Account Issues"
    const val ACCOUNT_ISSUES_DESCRIPTION = "Find solutions to account-related problems like password reset, login issues, and more."
    const val PAYMENT_HELP_TITLE = "💳 Payment Help"
    const val PAYMENT_HELP_DESCRIPTION = "Resolve issues with payments and transactions."
    const val APP_USAGE_TITLE = "📱 App Usage"
    const val APP_USAGE_DESCRIPTION = "Learn how to use different features of the app."
    const val PRIVACY_SECURITY_TITLE = "🔒 Privacy & Security"
    const val PRIVACY_SECURITY_DESCRIPTION = "Understand your data rights and privacy settings."
    const val OPENING_CHAT_TOAST = "Opening chat..."
    const val SEARCH_ICON_DESCRIPTION = "Search"
    const val CHAT_ICON_DESCRIPTION = "Chat"

    // chat type
    const val GROUP_CHAT = "all"
    const val INDIVIDUAL_CHAT = "individual"

}