package br.com.cesarsicas.aichatbot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// System serif fonts — approximates Playfair Display (headings) and EB Garamond (body)
// Replace with GoogleFont("Playfair Display") / GoogleFont("EB Garamond") via
// ui-text-google-fonts + font_certs.xml for pixel-perfect fidelity.
val SerifDisplay = FontFamily.Serif
val SerifBody    = FontFamily.Serif

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 31.sp,
        lineHeight = 36.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = SerifBody,
        fontWeight = FontWeight.Normal,
        fontSize = 16.5.sp,
        lineHeight = 26.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SerifBody,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SerifBody,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SerifBody,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.14.em,
    ),
    labelSmall = TextStyle(
        fontFamily = SerifBody,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 0.22.em,
    ),
)
