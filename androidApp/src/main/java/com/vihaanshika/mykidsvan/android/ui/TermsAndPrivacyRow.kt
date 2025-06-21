package com.vihaanshika.mykidsvan.android.ui
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vihaanshika.mykidsvan.android.utils.Constants

@Composable
fun TermsAndPrivacyRow(
    termsAccepted: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val defaultTextColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = termsAccepted, onCheckedChange = onCheckedChange)

        val annotatedText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = defaultTextColor)) {
                append("Accept ")
            }
            pushStringAnnotation(
                tag = "TERMS",
                annotation = Constants.TERMS_AND_CONDITIONS
            )
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("Terms and Conditions")
            }
            pop()

            withStyle(style = SpanStyle(color = defaultTextColor)) {
                append(" and ")
            }

            pushStringAnnotation(
                tag = "PRIVACY",
                annotation = Constants.PRIVACY_AND_POLICY
            )
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("Privacy Policy")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            style = TextStyle(fontSize = 14.sp),
            onClick = { offset ->
                annotatedText.getStringAnnotations(start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                        context.startActivity(intent)
                    }
            }
        )
    }
}
