package com.example.mykidsvan.android.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.draw.scale

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.utils.Constants
import com.example.mykidsvan.android.utils.Resource
import kotlinx.coroutines.delay

@Composable
fun ReferAppScreen(viewModel: AuthViewModel) {
    val context = LocalContext.current

    val referCode by viewModel.referCode.collectAsState()
    val commissionState by viewModel.commissionState.collectAsState()

    val referralCode = referCode.toString()

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


    // Fetch commission based on user role once
    LaunchedEffect(Unit) {
        val driverId = if (viewModel.userRole.value == Constants.USER_PARENT) {
            viewModel.assignedVehicleId.value.toString()
        } else {
            viewModel.userId.value.toString()
        }
        viewModel.getCommission(driverId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // Referral Code Card with animation
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
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your Referral Code", style = MaterialTheme.typography.titleMedium)
                    Text(
                        referralCode,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Share Button
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey! Use my referral code: $referralCode to sign up.\nMyKidVan Team"
                        )
                        setPackage("com.whatsapp")
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(text = "Share Now", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Commission State UI
            when (commissionState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is Resource.Success -> {
                    val commissions = (commissionState as Resource.Success).data.data
                    if (commissions.isNotEmpty()) {
                        val totalAmount = commissions.sumOf { it.amount?.toDoubleOrNull() ?: 0.0 }
                        Text(
                            text = "Total Commission Earned: ₹%.2f".format(totalAmount),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Text(
                            text = "No commissions yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                is Resource.Error -> {
                    val message = (commissionState as Resource.Error).message
                    Text(
                        text = "No commission available",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is Resource.Idle<*> -> {

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Text
            Text(
                text = yourHindiReferralProgramText, // see below for the constant
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

