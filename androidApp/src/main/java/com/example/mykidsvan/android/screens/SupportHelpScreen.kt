package com.example.mykidsvan.android.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportHelpScreen() {
    val faqExpanded = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme // To access current theme colors

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Opening chat...", Toast.LENGTH_SHORT).show()
                },
                containerColor = colors.primary, // Adapt to primary color based on theme
                shape = CircleShape,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Email, contentDescription = "Chat")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Text(
                text = "How can we help you?",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp),
                color = colors.onBackground // Ensure text color adapts to the theme
            )

            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search for help...") },
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = colors.onSurface) // Adjust icon color
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary, // Primary color for focused state
                    unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f), // Subtle border color for unfocused state
                    cursorColor = colors.onSurface
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Expandable FAQ Section
            ExpandableSupportCard(
                title = "Account Issues",
                description = "Find solutions to account-related problems like password reset, login issues, and more.",
                expanded = faqExpanded.value,
                onClick = { faqExpanded.value = !faqExpanded.value }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // More Support Categories
            SupportCategoryCard("💳 Payment Help", "Resolve issues with payments and transactions.")
            SupportCategoryCard("📱 App Usage", "Learn how to use different features of the app.")
            SupportCategoryCard("🔒 Privacy & Security", "Understand your data rights and privacy settings.")
        }
    }
}

@Composable
fun ExpandableSupportCard(
    title: String,
    description: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colors.surface), // Surface color for dark mode
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface // Text color adapts to the theme
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.onSurface // Icon color adapts to the theme
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    color = colors.onSurface // Text color adapts to the theme
                )
            }
        }
    }
}

@Composable
fun SupportCategoryCard(title: String, description: String) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.surface), // Surface color for dark mode
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface // Text color adapts to the theme
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface.copy(alpha = 0.7f) // Subtle text color for description
            )
        }
    }
}


