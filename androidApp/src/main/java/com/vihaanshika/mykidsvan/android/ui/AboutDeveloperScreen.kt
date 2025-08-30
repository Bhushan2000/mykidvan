package com.vihaanshika.mykidsvan.android.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vihaanshika.mykidsvan.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDeveloperScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val url = "https://wa.me/917276888566"
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                },
                containerColor = Color.White, // or use WhatsApp green: Color(0xFF25D366)
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.size(56.dp) // Adjust size if needed (48dp = standard icon button)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.whatsapp),
                    contentDescription = "WhatsApp Us",
                    modifier = Modifier.size(56.dp), // Typical icon size inside a FAB
                    tint = Color.Unspecified // Preserve original WhatsApp icon color
                )
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                 .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()   // ✅ adds padding for status bar
                .padding(top = innerPadding.calculateTopPadding() + 48.dp, start = 16.dp,end=16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // 👨‍💻 Developer Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Developed & Maintained by",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Vihaanshika Tech Solutions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "📍 Nagpur, Maharashtra",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // 💡 Services Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "🚀 We Build Apps & Websites",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Need a mobile app or website for your business?")
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("✅ School ERP Systems")
                        Text("✅ GPS Tracking Apps")
                        Text("✅ Survey / Logistics / HRMS")
                        Text("✅ Custom Android Applications")
                        Text("✅ Websites starting @ ₹5,000")
                    }
                }
            }

            // 📞 Contact Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Contact Us",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("📞 +91-7276 888 566", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("📧 support@vihaanshika.com", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("🌐 www.vihaanshika.com", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "🟢 Tap the floating WhatsApp button to reach out now!",
                        color = Color(0xFF25D366),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAboutDeveloperScreen() {
    AboutDeveloperScreen()
}
