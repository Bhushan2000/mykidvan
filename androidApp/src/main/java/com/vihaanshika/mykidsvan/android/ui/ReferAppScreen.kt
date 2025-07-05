package com.vihaanshika.mykidsvan.android.ui

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.vihaanshika.mykidsvan.android.data.dto.request.WithdrawRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.CommissionParentResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.CommissionResponse
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.Resource
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferAppScreen(viewModel: AuthViewModel, userRole: String, userId: String) {
    val context = LocalContext.current

    val referCode by viewModel.referCode.collectAsState()
    val commissionState by viewModel.commissionState.collectAsState()

    val referralCode = referCode.toString()

    val showSheet = remember { mutableStateOf(false) }
    val withdrawAmount = remember { mutableStateOf("") }
    val bankName = remember { mutableStateOf("") }
    val ifscCode = remember { mutableStateOf("") }
    val accountNumber = remember { mutableStateOf("") }
    val upiId = remember { mutableStateOf("") }

// Simulated values for now:
    var totalEarnedAmount = remember { mutableStateOf("0.0") }
    var totalWithdrawnAmount = remember { mutableStateOf("0.0") }
    var remainingAmount = remember { mutableStateOf("0.0") }

    val withdrawState by viewModel.withdraw.collectAsState()

    val isLoading = withdrawState is Resource.Loading


    val yourHindiReferralProgramText = """
रेफ़रल प्रोग्राम

कैसे काम करता है:
जब आप किसी माता-पिता को ‘MyKidVan’ ऐप में रजिस्टर कराते हैं और वह प्रीमियम सेवा के लिए 100 रुपये का पेमेंट करते है, तो पॉइंट्स इस तरह मिलते है:

आपको तुरंत 30 पॉइंट्स (₱30) क्रेडिट होते हैं।

अगर आपको किसी अन्य ड्राइवर ने रेफ़र किया था, तो उस रेफ़र करने वाले ड्राइवर को 20 पॉइंट्स (₱20) क्रेडिट होते हैं।

ज्यादा कमाई कैसे करें:
आपने जिन स्कूल वँन मालक/ चालक को रेफ़र किया है, जब भी कोई माता-पिता प्रीमियम सेवा के लिए 100 रुपये का पेमेंट करते है, तो आपको 20 पॉइंट्स प्रति ट्रांज़ैक्शन मिलते रहेंगे।

आपके फायदे:
तुरंत इनाम: हर सफल रजिस्ट्रेशन पर आपके खाते में तुरंत 30 पॉइंट्स जुड़ते हैं।

लगातार आय: जिन मालक/ चालक/ड्राइवरों को आपने रेफ़र किया है, उनके हर पेमेंट पर आपको 20 पॉइंट्स मिलते रहेंगे।

नेटवर्क बढ़ाएं: जितने ज़्यादा ड्राइवर आप रेफ़र करेंगे, उतनी ही ज़्यादा कमाई के अवसर मिलेंगे।

मुख्य लाभ (MyKidVan):

रीयल-टाइम मॉनिटरिंग: माता-पिता रीयल-टाइम GPS ट्रैकिंग से अपने बच्चे की वैन की स्थिति देख सकते हैं।

आसान वाहन खोज: माता-पिता अपने बच्चे के लिए किफायती ऑटो/वैन खोज सकते हैं और ड्राइवर का विवरण मुफ्त में प्राप्त कर सकते हैं।

सीधा इन-ऐप संवाद: माता-पिता और ड्राइवर सीधे ऐप में चैट और कॉल कर सकते हैं।

निशुल्क एक्सेस: प्लेटफ़ॉर्म का उपयोग ड्राइवर और माता-पिता दोनों के लिए फ्री है।

लचीला किराया निर्धारण: ड्राइवर और माता-पिता स्वयं अपने किराए तय कर सकते हैं।

टिप्पणी:
पंजीकरण के समय रेफ़रल कोड भरना आवश्यक है। रेफ़रल लाभ केवल तभी मान्य हैं।

1 पॉइंट = ₹1
""".trimIndent()

    val driverReferalText = "\uD83D\uDE90 MyKidVan - ड्राइवर रेफ़रल प्रोग्राम\n" +
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

    val parentReferalText = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67 रेफ़रल प्रोग्राम\n" +
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

    // Fetch commission based on user role once
    LaunchedEffect(Unit) {
        val driverId = if (viewModel.userRole.value == Constants.USER_PARENT) {
            viewModel.assignedVehicleId.value.toString()
        } else {
            viewModel.userId.value.toString()
        }
        viewModel.getCommission(driverId, userRole = userRole)
    }

    LaunchedEffect(withdrawState) {
        when (withdrawState) {
            is Resource.Success -> {
                val message =
                    (withdrawState as Resource.Success).data?.message ?: "Withdrawal successful"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetWithdrawRequest()
            }

            is Resource.Error -> {
                val message = (withdrawState as Resource.Error).message ?: "Something went wrong"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetWithdrawRequest()
            }

            else -> {}
        }
    }

    // Commission State
    LaunchedEffect(commissionState) {
        when (val state = commissionState) {
            is Resource.Success -> {
                if (userRole == Constants.USER_DRIVER) {
                    val commissions = (state.data as CommissionResponse).data
                    // set the Commission summary here
                    // like total amount
                    // withdraw
                    // remaining
                    totalEarnedAmount.value = commissions.amount ?: "0.0"
                    totalWithdrawnAmount.value = commissions.totalWithdrawn ?: "0.0"
                    remainingAmount.value = commissions.remaining ?: "0.0"
                    Log.d(
                        "TAG",
                        "ReferAppScreen: role driver - ${totalEarnedAmount.value}  ${totalWithdrawnAmount.value}  ${remainingAmount.value}"
                    )

                } else if (userRole == Constants.USER_PARENT) {
                    val commissionsP = (state.data as CommissionParentResponse).data
                    // set the Commission summary here
                    // like total amount
                    // withdraw
                    // remaining
                    totalEarnedAmount.value = commissionsP.commission ?: "0.0"
                    totalWithdrawnAmount.value = commissionsP.totalWithdrawn ?: "0.0"
                    remainingAmount.value = commissionsP.remaining ?: "0.0"
                    Log.d(
                        "TAG",
                        "ReferAppScreen: role parent - ${totalEarnedAmount.value}  ${totalWithdrawnAmount.value}  ${remainingAmount.value}"
                    )
                }
            }

            is Resource.Error -> {
                Log.d("TAG", "ReferAppScreen: Error while showing")
            }

            is Resource.Idle<*> -> {}
            is Resource.Loading<*> -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Referral Code Animation Card
            var scale by remember { mutableStateOf(1f) }
            LaunchedEffect(Unit) {
                scale = 1.1f
                delay(500L)
                scale = 1f
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .scale(scale),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Ensures Column takes full width of Card
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Your Referral Code",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1565C0)
                    )
                    Text(
                        text = referralCode,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color(0xFF1565C0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Share Now Button
            Button(
                onClick = {
                    val message =
                        if (userRole == Constants.USER_DRIVER) driverReferalText else parentReferalText
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                        `package` = "com.whatsapp"
                    }

                    try {
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Share Now", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Commission Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Ensures full width for proper horizontal centering
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Centers content horizontally
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Commission Summary",
                        color = Color(0xFF1565C0),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Total Earned: ${totalEarnedAmount.value}", color = Color(0xFF2E7D32))
                    Text("Withdrawn: ${totalWithdrawnAmount.value}", color = Color(0xFFF57C00))
                    Text(
                        "Remaining: ${remainingAmount.value}",
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showSheet.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text("Withdraw Commission", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (userRole == Constants.USER_DRIVER) driverReferalText else parentReferalText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (showSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet.value = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .verticalScroll(scrollState)
                            .imePadding() // 👈 adds padding when keyboard is visible
                            .padding(bottom = 32.dp) // 👈 manual bottom padding for Submit button spacing
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Withdraw Commission",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        val fieldModifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = withdrawAmount.value,
                            onValueChange = { withdrawAmount.value = it },
                            label = { Text("Withdraw Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )

                        OutlinedTextField(
                            value = bankName.value,
                            onValueChange = { bankName.value = it },
                            label = { Text("Bank Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )

                        OutlinedTextField(
                            value = ifscCode.value,
                            onValueChange = { ifscCode.value = it },
                            label = { Text("IFSC Code") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )

                        OutlinedTextField(
                            value = accountNumber.value,
                            onValueChange = { accountNumber.value = it },
                            label = { Text("Account Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("OR", modifier = Modifier.align(Alignment.CenterHorizontally))

                        OutlinedTextField(
                            value = upiId.value,
                            onValueChange = { upiId.value = it },
                            label = { Text("UPI ID (Optional)") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = fieldModifier
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val request = WithdrawRequest(
                                    withdrawAmount.value,
                                    bankName.value,
                                    ifscCode.value,
                                    accountNumber.value,
                                    upiId.value
                                )
                                viewModel.withdrawRequest(request)
                                // Don't dismiss yet until success
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("Submit", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}