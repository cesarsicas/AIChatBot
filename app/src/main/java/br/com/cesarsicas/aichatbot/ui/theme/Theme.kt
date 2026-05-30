package br.com.cesarsicas.aichatbot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColloquyColorScheme = lightColorScheme(
    primary              = Ink,
    onPrimary            = BubbleText,
    primaryContainer     = Card,
    onPrimaryContainer   = Ink,
    secondary            = InkSoft,
    onSecondary          = BubbleText,
    secondaryContainer   = PaperDk,
    onSecondaryContainer = Ink,
    background           = Paper,
    onBackground         = Ink,
    surface              = Paper,
    onSurface            = Ink,
    surfaceVariant       = PaperLt,
    onSurfaceVariant     = InkSoft,
    surfaceContainer     = PaperLt,
    outline              = Line,
    outlineVariant       = LineSoft,
    error                = MarcusAccent,
    onError              = BubbleText,
)

@Composable
fun AIChatbotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColloquyColorScheme,
        typography = Typography,
        content = content,
    )
}
