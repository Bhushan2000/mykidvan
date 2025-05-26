package com.example.mykidsvan.android.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.presentation.viewmodel.AuthViewModel

@Composable
fun ReferAppScreen(viewModel: AuthViewModel) {
    val isConfettiVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val referCode by viewModel.referCode.collectAsState()

    val referralCode = referCode.toString()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Refer and Earn!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )


            // Referral Code Card with Animation
            var scale by remember { mutableStateOf(1f) }
            LaunchedEffect(Unit) {
                scale = 1.1f
                kotlinx.coroutines.delay(500L)
                scale = 1f
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .scale(scale),
                shape = RoundedCornerShape(16.dp),
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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Share Button
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Hey! Use my referral code: $referralCode to sign up.\n MyKidVan Team")
                        `package` = "com.whatsapp" // Open WhatsApp directly
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                    }                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(text = "Share Now", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "रेफ़रल प्रोग्राम\n" +
                        "\n" +
                        "\n" +
                        "कैसे काम करता है:\n" +
                        "से`\n" +
                        "जब आप किसी माता-पिता को ‘MyKidVan’ ऐप में रजिस्टर कराते हैं और वह प्रीमियम सेवा के लिए 100 रुपये का पेमेंट करते है, तो पॉइंट्स इस तरह मिलते है:\n" +
                        "\n" +
                        "आपको तुरंत 30 पॉइंट्स (₱30) क्रेडिट होते हैं।\n" +
                        "\n" +
                        "अगर आपको किसी अन्य ड्राइवर ने रेफ़र किया था, तो उस रेफ़र करने वाले ड्राइवर को 20 पॉइंट्स (₱20) क्रेडिट होते हैं।\n" +
                        "\n" +
                        "ज्यादा कमाई कैसे करें:\n" +
                        "\n" +
                        "आपने जिन स्कूल वँन मालक/ चालक को रेफ़र किया है, जब भी कोई माता-पिता प्रीमियम सेवा के लिए 100 रुपये का पेमेंट करते है, तो आपको 20 पॉइंट्स प्रति ट्रांज़ैक्शन मिलते रहेंगे।\n" +
                        "\n" +
                        "आपके फायदे\n" +
                        "तुरंत इनाम: हर सफल रजिस्ट्रेशन पर आपके खाते में तुरंत 30 पॉइंट्स जुड़ते हैं।\n" +
                        "\n" +
                        "लगातार आय: जिन मालक/ चालक/ड्राइवरों को आपने रेफ़र किया है, उनके हर पेमेंट पर आपको 20 पॉइंट्स मिलते रहेंगे।\n" +
                        "\n" +
                        "नेटवर्क बढ़ाएं: जितने ज़्यादा ड्राइवर आप रेफ़र करेंगे, उतनी ही ज़्यादा कमाई के अवसर मिलेंगे।\n" +
                        "\n" +
                        "मुख्य सामान्य लाभ (MyKidVan) और उनके शीर्षक\n" +
                        "\n" +
                        "रीयल-टाइम मॉनिटरिंग:\n" +
                        "माता-पिता रीयल-टाइम GPS ट्रैकिंग से अपने बच्चे की वैन की स्थिति देख सकते हैं, बार-बार ड्राइवर को कॉल करने की जरूरत नहीं।\n" +
                        "\n" +
                        "आसान वाहन खोज:\n" +
                        "माता-पिता अपने बच्चे के लिए किफायती ऑटो/वैन खोज सकते हैं और ड्राइवर का विवरण मुफ्त में प्राप्त कर सकते हैं।\n" +
                        "\n" +
                        "स्टूडेंट पूल एक्सेस:\n" +
                        "ड्राइवर/वैन मालिक आसपास के छात्रों को ढूंढकर अपनी सीटें भर सकते हैं, जिससे आय बढ़ती है। स्टूडेंट्स का  विवरण मुफ्त में प्राप्त कर सकते हैं।\n" +
                        "\n" +
                        "सीधा इन-ऐप संवाद:\n" +
                        "माता-पिता और ड्राइवर सीधे ऐप में चैट और कॉल कर सकते हैं, संचार आसान और तेज़।\n" +
                        "\n" +
                        "निशुल्क एक्सेस:\n" +
                        "प्लेटफ़ॉर्म का उपयोग ड्राइवर और माता-पिता दोनों के लिए फ्री है। केवल माता-पिता को बच्चे को लाइव ट्रैकिंग के लिए वार्षिक नाममात्र शुल्क देना होता है।\n" +
                        "\n" +
                        "लचीला किराया निर्धारण:\n" +
                        "माता-पिता और ड्राइवर दोनों स्वयं अपने परिवहन किराए तय कर सकते हैं; कंपनी कोई कमीशन नहीं लेती।\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "टिप्\u200Dपणी\n" +
                        "माता-पिता एवं ड्राइवर/वैन मालिक को रजिस्ट्रेशन के समय रेफ़रल कोड अवश्य दर्ज करना होगा; बिना कोड के आपको रेफ़रल लाभ प्राप्त नहीं होंगे।\n" +
                        "\n" +
                        "अपना यूनिक रेफ़रल कोड शेयर करें—SMS, WhatsApp, या सोशल मीडिया पर।\n" +
                        "\n" +
                        "रेफ़रल की प्रगति देखने के लिए “मेरे रेफ़रल” सेक्शन पर जाएँ।\n" +
                        "\n" +
                        "नोट: 1 पॉइंट = ₹1।",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Confetti Animation (dummy placeholder)
        if (isConfettiVisible) {
            Text("🎉🎊🎉 Confetti Animation 🎉🎊🎉", modifier = Modifier.align(Alignment.Center))
        }
    }
}
