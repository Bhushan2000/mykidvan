package com.vihaanshika.mykidsvan.android.utils

object Constants {

    // terms and conditions and privacy policy
    const val TERMS_AND_CONDITIONS = "https://mykidvan.com/terms-and-conditions/"
    const val PRIVACY_AND_POLICY = "https://mykidvan.com/privacy-policy/"

    // location service
    const val LOCATION_BROADCAST_ACTION = "LOCATION_UPDATE"
    const val SERVER_LOCATION_BROADCAST_ACTION = "SERVER_LOCATION_UPDATE"
    const val TRACKING_STATUS_CHANGED = "TRACKING_STATUS_CHANGED"

    const val CHANNEL_ID = "location_channel"
    const val CHANNEL_NAME = "Location Tracking"

    // razorpay
    const val AMOUNT_IN_PAISE = 10000 // ₹100

    //    const val AMOUNT_IN_PAISE = 100 // ₹1
    const val RAZORPAY_TEST_KEY = "rzp_test_BVJygtmA6ljXBB"

    const val PAYMENT_NAME = "Assign Driver"
    const val PAYMENT_DESCRIPTION = "Driver Assignment"
    const val PAYMENT_CURRENCY = "INR"
    const val PREFILL_EMAIL = "example@example.com"
    const val PREFILL_CONTACT = "9876543210"
    const val ORDER_ID = "order_QgKr8AhpkWq63A" // Used

    // app name
    const val MY_KID_VAN = "My Kid Van"

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
    const val USER_DRIVER = "driver"
    const val USER_PARENT = "parent"

    // logout dialog
    const val YES = "Yes"
    const val No = "No"

    // tracking button
    const val START = "Start"
    const val STOP = "Stop"

    // chat type
    const val GROUP_CHAT = "all"
    const val INDIVIDUAL_CHAT = "individual"

    // otp type
    const val REGISTER_OTP = "register"
    const val FORGOT_OTP = "forget"

    // lat_status
    const val ACTIVE_TRACKING = "start"
    const val INACTIVE_TRACKING = "stop"

    // request status
    const val REQUEST_ACCEPTED = "accepted"
    const val REQUEST_REJECTED = "rejected"
    const val REQUEST_PENDING = "pending"

    // support system
    const val SUPPORT_PHONE_NO = "7276888566"
    const val SUPPORT_EMAIL = "support@mykidvan.com"
    const val SUPPORT_WEBSITE = "https://vihaanshika.com"

    // notification channel id
    const val CHAT_CHANNEL = "chat_channel"
    const val PAYMENT_CHANNEL = "payment_channel"
    const val TRACKING_CHANNEL = "tracking_channel"
    const val TRACKING_REQUEST_CHANNEL = "tracking_request_channel"
    const val LOCATION_CHANNEL = "location_channel"
    const val ACTION_STOP_TRACKING = "ACTION_STOP_TRACKING"
    const val ACTION_START_POLLING ="ACTION_START_POLLING"
    const val ACTION_STOP_POLLING = "ACTION_STOP_POLLING"
}