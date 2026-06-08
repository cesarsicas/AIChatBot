package br.com.cesarsicas.aichatbot.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import br.com.cesarsicas.aichatbot.presentation.theme.CharacterTheme
import br.com.cesarsicas.aichatbot.presentation.theme.ColloquyWordmark
import br.com.cesarsicas.aichatbot.presentation.theme.DiamondRule
import br.com.cesarsicas.aichatbot.presentation.theme.HairlineRule
import br.com.cesarsicas.aichatbot.ui.theme.BubbleText
import br.com.cesarsicas.aichatbot.ui.theme.Ink
import br.com.cesarsicas.aichatbot.ui.theme.InkFaint
import br.com.cesarsicas.aichatbot.ui.theme.InkSoft
import br.com.cesarsicas.aichatbot.ui.theme.Line
import br.com.cesarsicas.aichatbot.ui.theme.PaperLt
import br.com.cesarsicas.aichatbot.ui.theme.SerifDisplay

@Composable
fun ModelSetupScreen(
    modifier: Modifier = Modifier,
    characterTheme: CharacterTheme? = null,
    onDownload: () -> Unit,
    onPickFile: () -> Unit,
) {
    val accent = characterTheme?.accent ?: InkSoft

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ColloquyWordmark()
        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(characterTheme?.tint ?: PaperLt)
                .border(1.dp, Line, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.CloudOff,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(32.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "On-device model required",
            fontFamily = SerifDisplay,
            fontSize = 24.sp,
            color = Ink,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "A language model (~1 GB) runs entirely on your device — " +
                "no data ever leaves it.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
        )

        Spacer(Modifier.height(28.dp))
        DiamondRule(color = Line)
        Spacer(Modifier.height(28.dp))

        SetupButton(
            label = "Download model",
            sublabel = "Wi-Fi recommended · ~1 GB",
            icon = { Icon(Icons.Filled.Download, contentDescription = null, tint = BubbleText, modifier = Modifier.size(18.dp)) },
            accent = accent,
            filled = true,
            onClick = onDownload,
        )

        Spacer(Modifier.height(12.dp))

        SetupButton(
            label = "Pick from storage",
            sublabel = "Select a .gguf file",
            icon = { Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp)) },
            accent = accent,
            filled = false,
            onClick = onPickFile,
        )

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.CloudOff,
                contentDescription = null,
                tint = InkFaint,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = "Private · runs locally · no internet required",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
                letterSpacing = 0.08.em,
            )
        }
    }
}

@Composable
private fun SetupButton(
    label: String,
    sublabel: String,
    icon: @Composable () -> Unit,
    accent: androidx.compose.ui.graphics.Color,
    filled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(if (filled) accent else androidx.compose.ui.graphics.Color.Transparent)
            .border(1.dp, accent, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            icon()
            Column {
                Text(
                    text = label,
                    fontFamily = SerifDisplay,
                    fontSize = 16.sp,
                    color = if (filled) BubbleText else accent,
                )
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (filled) BubbleText.copy(alpha = 0.72f) else InkFaint,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}
