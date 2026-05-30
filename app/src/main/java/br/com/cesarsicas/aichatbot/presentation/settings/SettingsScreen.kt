package br.com.cesarsicas.aichatbot.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import br.com.cesarsicas.aichatbot.presentation.theme.ColloquyWordmark
import br.com.cesarsicas.aichatbot.presentation.theme.DiamondRule
import br.com.cesarsicas.aichatbot.presentation.theme.HairlineRule
import br.com.cesarsicas.aichatbot.presentation.theme.SmallLabel
import br.com.cesarsicas.aichatbot.ui.theme.Ink
import br.com.cesarsicas.aichatbot.ui.theme.InkFaint
import br.com.cesarsicas.aichatbot.ui.theme.InkSoft
import br.com.cesarsicas.aichatbot.ui.theme.Line
import br.com.cesarsicas.aichatbot.ui.theme.LineSoft
import br.com.cesarsicas.aichatbot.ui.theme.OnDeviceGreen
import br.com.cesarsicas.aichatbot.ui.theme.Paper
import br.com.cesarsicas.aichatbot.ui.theme.PaperLt
import br.com.cesarsicas.aichatbot.ui.theme.SerifDisplay

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Paper)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = InkSoft,
                    )
                }
                Text(
                    text = "Settings",
                    fontFamily = SerifDisplay,
                    fontSize = 20.sp,
                    color = Ink,
                    modifier = Modifier.weight(1f),
                )
            }
            HairlineRule(color = Line)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                // On-device hero card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(PaperLt)
                        .border(1.dp, Line, RoundedCornerShape(4.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(OnDeviceGreen.copy(alpha = 0.12f))
                            .border(1.dp, OnDeviceGreen.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudOff,
                            contentDescription = null,
                            tint = OnDeviceGreen,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Runs 100% on your device",
                        fontFamily = SerifDisplay,
                        fontSize = 20.sp,
                        color = Ink,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Every word of every conversation stays on " +
                            "this device. No servers, no logs, no data shared.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSoft,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Language model card
                SettingsCard(title = "Language model") {
                    InfoRow(label = "Model", value = "Llama 3.2 · 3B Instruct")
                    HairlineRule(color = LineSoft)
                    InfoRow(label = "Quantization", value = "Q4_K_M")
                    HairlineRule(color = LineSoft)
                    InfoRow(label = "Context window", value = "4,096 tokens")
                }

                Spacer(Modifier.height(14.dp))

                // Knowledge base card
                SettingsCard(title = "Knowledge base") {
                    InfoRow(label = "Sherlock Holmes", value = "4 works · 1,840 passages")
                    HairlineRule(color = LineSoft)
                    InfoRow(label = "Marcus Aurelius", value = "3 works · 1,210 passages")
                }

                Spacer(Modifier.height(28.dp))
                DiamondRule(color = LineSoft)
                Spacer(Modifier.height(28.dp))

                // Wordmark + version footer
                ColloquyWordmark()
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Version 1.0 · offline build",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkFaint,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.08.em,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(PaperLt)
            .border(1.dp, Line, RoundedCornerShape(4.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Paper.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            SmallLabel(text = title, color = InkFaint)
        }
        HairlineRule(color = LineSoft)
        content()
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink,
        )
    }
}
