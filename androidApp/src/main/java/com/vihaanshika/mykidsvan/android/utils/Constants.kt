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

    const val TITLE_ABOUT_DEVELOPER = "About Developer"
    const val ROUTE_ABOUT_DEVELOPER = "about_developer"

    const val TITLE_COMMISSION = "Commission"
    const val ROUTE_COMMISSION = "commission"

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

    // Parent Status
    const val ACTIVE_PARENT = "Active"
    const val INACTIVE_PARENT = "InActive"

    const val PAYMENT_STATUS_PAID = "Paid"
    const val PAYMENT_STATUS_NOT_PAID = "Not Paid"

    // request status
    const val REQUEST_ACCEPTED = "accepted"
    const val REQUEST_REJECTED = "rejected"
    const val REQUEST_PENDING = "pending"

    // support system
    const val SUPPORT_PHONE_NO = "7276888566"
    const val SUPPORT_EMAIL = "support@mykidvan.com"
    const val SUPPORT_WEBSITE = "https://mykidvan.com"

    const val SUPPORT_YOUTUBE = "https://www.youtube.com/@MyKidVan"
    const val SUPPORT_INSTAGRAM = "https://www.instagram.com/mykid_van/"
    const val SUPPORT_FACEBOOK = "https://www.facebook.com/profile.php?viewas=100000686899395&id=61577242647822"

    const val FAQ_DRIVER = "https://mykidvan.com/faq-for-drivers/"
    const val FAQ_PARENT = "https://mykidvan.com/faq-for-parents/"

    const val REFERRAL_PARENT = "https://mykidvan.com/parents-referral-program/"
    const val REFERRAL_DRIVER = "https://mykidvan.com/driver-referral-details/"

    const val SUPPORT_PHONE_PLACEHOLDER = "Call Support: 7276888566"
    const val SUPPORT_EMAIL_PLACEHOLDER = "Email Support: support@mykidvan.com"
    const val SUPPORT_WEBSITE_PLACEHOLDER = "Visit our Website"
    const val SUPPORT_YOUTUBE_PLACEHOLDER = "Watch us on YouTube"
    const val SUPPORT_INSTAGRAM_PLACEHOLDER = "Follow us on Instagram"
    const val SUPPORT_FACEBOOK_PLACEHOLDER = "Connect on Facebook"
    const val FAQ_DRIVER_PLACEHOLDER = "FAQ for Drivers"
    const val FAQ_PARENT_PLACEHOLDER = "FAQ for Parents"

    // notification channel id
    const val CHAT_CHANNEL = "chat_channel"
    const val PAYMENT_CHANNEL = "payment_channel"
    const val TRACKING_CHANNEL = "tracking_channel"
    const val TRACKING_REQUEST_CHANNEL = "tracking_request_channel"
    const val LOCATION_CHANNEL = "location_channel"
    const val ACTION_STOP_TRACKING = "ACTION_STOP_TRACKING"
    const val ACTION_START_POLLING ="ACTION_START_POLLING"
    const val ACTION_STOP_POLLING = "ACTION_STOP_POLLING"

    const val REFERRAL_PARENT_CONTENT =  "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67 रेफ़रल प्रोग्राम\n" +
            "कैसे काम करता है:\n" +
            "जब आप किसी अन्य माता-पिता को ‘MyKidVan’ ऐप डाउनलोड करने और प्रीमियम सेवा (₹199 पेमेंट) के लिए रजिस्टर करने के लिए रेफ़र करते हैं, तो आपको मिलते हैं:\n" +
            "\n" +
            "\uD83C\uDF81 ₹50 (50 पॉइंट्स) सीधे आपके खाते में!\n" +
            "\n" +
            "ज्यादा रेफ़र करें, ज्यादा कमाएं:\n" +
            "जितने अधिक माता-पिता आप रेफ़र करेंगे, उतनी अधिक बार ₹50 कमाने का मौका मिलेगा।\n" +
            "\n" +
            "कोई सीमा नहीं — हर सफल रेफ़रल पर ₹50 आपके खाते में।\n" +
            "\n" +
            "आपके फायदे:\n" +
            "✅ तुरंत इनाम: जब आपका रेफ़र किया हुआ माता-पिता ₹199 की प्रीमियम सेवा लेते हैं, तो आपको तुरंत ₹50 मिलते हैं।\n" +
            "✅ आसान शेयरिंग: अपना रेफ़रल कोड SMS, WhatsApp, या सोशल मीडिया के ज़रिए शेयर करें।\n" +
            "✅ अपनी प्रगति ट्रैक करें: \"मेरे रेफ़रल\" सेक्शन में जाकर अपने रेफ़रल की स्थिति देख सकते हैं।\n" +
            "\n" +
            "\uD83D\uDD12 जरूरी सूचना:\n" +
            "रेफ़रल कोड का इस्तेमाल अनिवार्य है। रजिस्ट्रेशन के समय आपके कोड का उपयोग नहीं किया गया तो आपको लाभ नहीं मिलेगा।\n" +
            "\n" +
            "1 पॉइंट = ₹1 होता है।\n" +
            "\n" +
            "\uD83D\uDE80 कैसे शुरू करें?\n" +
            "अपने \"Parent Login\" से ऐप में लॉगिन करें।\n" +
            "\n" +
            "\"रेफ़र ऐप\" सेक्शन में जाकर अपना यूनिक रेफ़रल कोड पाएं।\n" +
            "\n" +
            "नए माता-पिता को ऐप डाउनलोड कराएं और रजिस्ट्रेशन के समय आपका कोड इस्तेमाल करने को कहें।\n" +
            "\n" +
            "जैसे ही वे प्रीमियम सेवा लेते हैं, ₹50 आपके अकाउंट में जुड़ जाएंगे।"

    const val REFERRAL_DRIVER_CONTENT = "\uD83D\uDE90 MyKidVan - ड्राइवर रेफ़रल प्रोग्राम\n" +
            "\uD83D\uDCA1 कैसे काम करता है?\n" +
            "जब आप किसी माता-पिता को ‘MyKidVan’ ऐप पर रजिस्टर कराते हैं और वह ₹199 देकर प्रीमियम सेवा एक्टिवेट करता है, तो आपको और आपके रेफ़रल नेटवर्क को इस तरह से पॉइंट्स मिलते हैं:\n" +
            "\n" +
            "\uD83D\uDD39 आपको तुरंत मिलते हैं 80 पॉइंट्स (₹80)\n" +
            "\uD83D\uDD39 यदि आपने खुद किसी ड्राइवर के रेफ़रल से ऐप जॉइन किया था, तो उस ड्राइवर को मिलते हैं 30 पॉइंट्स (₹30)\n" +
            "\n" +
            "\uD83D\uDCC8 ज्यादा कमाई कैसे करें?\n" +
            "जिन ड्राइवरों या वैन मालिकों को आपने रेफ़र किया है, अगर उनके माध्यम से कोई भी माता-पिता ₹199 की प्रीमियम सेवा लेते हैं, तो आपको हर ट्रांज़ैक्शन पर 30 पॉइंट्स (₹30) मिलते रहेंगे — कभी खत्म न होने वाली कमाई!\n" +
            "\n" +
            "\uD83C\uDF81 आपके फायदे:\n" +
            "✅ तुरंत इनाम: हर सफल रजिस्ट्रेशन पर ₹80 सीधा आपके वॉलेट में।\n" +
            "✅ निरंतर आय: आपके द्वारा रेफ़र किए गए ड्राइवरों से होने वाली हर बिक्री पर ₹30 मिलते रहेंगे।\n" +
            "✅ बिना लिमिट के कमाई: जितने ज्यादा ड्राइवर और माता-पिता आप रेफ़र करेंगे, उतनी ही ज्यादा कमाई।\n" +
            "\n" +
            "\uD83D\uDEE0\uFE0F MyKidVan ऐप की मुख्य विशेषताएं:\n" +
            "\uD83D\uDD0D रीयल-टाइम मॉनिटरिंग\n" +
            "माता-पिता ऐप में वैन की GPS लोकेशन लाइव देख सकते हैं — अब बार-बार कॉल करने की जरूरत नहीं।\n" +
            "\n" +
            "\uD83D\uDE90 आसान वाहन खोज\n" +
            "माता-पिता अपने बच्चे के लिए नज़दीकी और किफायती वैन ढूंढ सकते हैं, बिना किसी शुल्क के।\n" +
            "\n" +
            "\uD83D\uDC68\u200D\uD83C\uDF93 स्टूडेंट पूल एक्सेस\n" +
            "ड्राइवर आस-पास के छात्र जोड़कर अपनी वैन की सीटें भर सकते हैं और आय बढ़ा सकते हैं।\n" +
            "\n" +
            "\uD83D\uDCAC सीधा इन-ऐप संवाद\n" +
            "माता-पिता और ड्राइवर ऐप के माध्यम से सीधे चैट या कॉल कर सकते हैं।\n" +
            "\n" +
            "\uD83C\uDF89 फ्री प्लेटफ़ॉर्म एक्सेस\n" +
            "MyKidVan ऐप का उपयोग ड्राइवर और माता-पिता दोनों के लिए फ्री है। सिर्फ माता-पिता को लाइव ट्रैकिंग के लिए एक नाममात्र वार्षिक शुल्क देना होता है।\n" +
            "\n" +
            "\uD83D\uDCB8 लचीला किराया निर्धारण\n" +
            "ड्राइवर और माता-पिता स्वयं आपसी सहमति से किराया तय कर सकते हैं। कंपनी कोई कमीशन नहीं लेती।\n" +
            "\n" +
            "\uD83D\uDCDD ज़रूरी टिप्\u200Dपणी:\n" +
            "रजिस्ट्रेशन के समय रेफ़रल कोड दर्ज करना अनिवार्य है।\n" +
            "\n" +
            "बिना कोड के रेफ़रल लाभ नहीं मिलेगा।\n" +
            "\n" +
            "अपना यूनिक रेफ़रल कोड SMS, WhatsApp, Facebook, या अन्य सोशल मीडिया पर शेयर करें।\n" +
            "\n" +
            "रेफ़रल की स्थिति देखने के लिए ऐप के \"मेरे रेफ़रल\" सेक्शन में जाएँ।\n" +
            "\n" +
            "\uD83D\uDCCC 1 पॉइंट = ₹1 — सीधा आपके वॉलेट में!\n" +
            "\n" +
            "अब आप भी बनें MyKidVan नेटवर्क का हिस्सा और कमाएं हर कनेक्शन से!"
}