package com.vihaanshika.mykidsvan.android.utils

object APIEndpoints {
    // api
    const val BASE_URL = "https://avschoolerp.com/"

    // Path Keys
    const val PATH_STATE_ID = "state_id"
    const val PATH_DISTRICT_ID = "district_id"
    const val PATH_TALUKA_ID = "taluka_id"
    const val PATH_SCHOOL_ID = "school_id"
    const val PATH_PARENT_ID = "parent_id"
    const val PATH_DRIVER_ID = "driver_id"
    const val PATH_MOBILE_NO = "mobile_no"

    // Path
    const val LOGIN = "index.php/api/Logincontroller/login"

    const val REGISTER_DRIVER = "index.php/api/AccountsController/vehicles"
    const val REGISTER_PARENT = "index.php/api/AccountsController/parent"
    const val REGISTER_SCHOOL = "index.php/api/AccountsController/school"
    const val DRIVER_SCHOOL_ON_REGISTER = "index.php/api/AccountsController/school_vehicles"
    const val PARENT_SCHOOL_ON_REGISTER = "index.php/api/AccountsController/school"

    const val GET_STATE = "index.php/api/AccountsController/get_state"
    const val GET_DISTRICT = "index.php/api/AccountsController/get_district/{state_id}"
    const val GET_TALUKA = "index.php/api/AccountsController/get_taluka/{state_id}/{district_id}"
    const val GET_SCHOOLS = "index.php/api/AccountsController/get_school/{state_id}/{district_id}/{taluka_id}"
    const val GET_ALL_SCHOOLS = "index.php/api/AccountsController/get_school_registeration"

    const val GET_PARENTS = "index.php/api/AccountsController/get_parent/{school_id}"
    const val GET_DRIVERS = "index.php/api/AccountsController/get_driver/{school_id}"
    const val GET_DRIVER_BY_MOBILE = "index.php/api/AccountsController/get_driverbynumber/{mobile_no}"
    const val GET_ALL_PARENTS = "index.php/api/AccountsController/get_all_parents"

    const val SEND_OTP = "index.php/api/AccountsController/store_otp"
    const val VERIFY_OTP = "index.php/api/AccountsController/verify_otp"
    const val UPDATE_PASSWORD = "index.php/api/AccountsController/update_password"

    const val GET_PARENT_PROFILE = "index.php/api/AccountsController/get_parentdetail/{parent_id}"
    const val GET_DRIVER_PROFILE = "index.php/api/AccountsController/get_driverdetail/{driver_id}"

    const val ASSIGN_VEHICLE = "index.php/api/AccountsController/update_vehiclesschool_id"
    const val ASSIGN_STUDENT = "index.php/api/AccountsController/update_parentschool_id"

    const val UPDATE_PROFILE_PARENT = "index.php/api/AccountsController/update_parentdetail"
    const val UPDATE_PROFILE_DRIVER = "index.php/api/AccountsController/update_driverdetail"
    const val UPDATE_VEHICLE_PHOTOS = "index.php/api/AccountsController/update_vehicle_photo"
    const val UPLOAD_DOCUMENTS = "index.php/api/AccountsController/update_images"

    const val UPDATE_PAYMENT = "index.php/api/AccountsController/update_payment"
    const val CREATE_ORDER = "index.php/api/AccountsController/get_orderidcreation"
    const val GET_VEHICLE_PHOTOS = "index.php/api/AccountsController/get_vehicle_photos/{driver_id}"

    const val CHECK_REFER_CODE = "index.php/api/AccountsController/check_refer_by_status"
    const val GET_DRIVER_COMMISSION = "index.php/api/AccountsController/get_commision/{driver_id}"
    const val GET_PARENT_COMMISSION = "index.php/api/AccountsController/get_parent_commision/{parent_id}"

    const val SEND_LAT_LONG = "index.php/api/AccountsController/update_vehicles"
    const val GET_LAT_LONG = "index.php/api/AccountsController/get_vehicles/{driver_id}"

    const val SEND_ASSIGN_REQUEST = "index.php/api/AccountsController/vehicle_message"
    const val UPDATE_ASSIGN_REQUEST = "index.php/api/AccountsController/vehicle_message"
    const val GET_DRIVER_REQUESTS = "index.php/api/AccountsController/get_vehicle_message/{driver_id}"

    const val SEND_MESSAGE_TO_DRIVER_FROM_PARENT = "index.php/api/AccountsController/parent_message"
    const val SEND_MESSAGE_TO_PARENT_FROM_DRIVER = "index.php/api/AccountsController/driver_message"
    const val GET_PARENT_MESSAGES = "index.php/api/AccountsController/get_parent_message/{parent_id}"
    const val GET_DRIVER_MESSAGES = "index.php/api/AccountsController/get_driver_message/{driver_id}"
    const val GET_DRIVER_ALL_MESSAGES = "index.php/api/AccountsController/get_all_message/{driver_id}"


}
