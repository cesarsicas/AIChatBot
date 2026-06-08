package br.com.cesarsicas.aichatbot.presentation.characterdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import br.com.cesarsicas.aichatbot.domain.model.Character
import br.com.cesarsicas.aichatbot.presentation.theme.CharacterTheme
import br.com.cesarsicas.aichatbot.presentation.theme.DiamondRule
import br.com.cesarsicas.aichatbot.presentation.theme.HairlineRule
import br.com.cesarsicas.aichatbot.presentation.theme.OfflineBadge
import br.com.cesarsicas.aichatbot.presentation.theme.SmallLabel
import br.com.cesarsicas.aichatbot.presentation.theme.theme
import br.com.cesarsicas.aichatbot.ui.theme.BubbleText
import br.com.cesarsicas.aichatbot.ui.theme.Ink
import br.com.cesarsicas.aichatbot.ui.theme.InkFaint
import br.com.cesarsicas.aichatbot.ui.theme.InkSoft
import br.com.cesarsicas.aichatbot.ui.theme.Line
import br.com.cesarsicas.aichatbot.ui.theme.LineSoft
import br.com.cesarsicas.aichatbot.ui.theme.PaperLt
import br.com.cesarsicas.aichatbot.ui.theme.SerifDisplay

@Composable
fun CharacterDetailScreen(
    characterId: String,
    onNavigateBack: () -> Unit,
    onBeginConversation: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val character = Character.entries.firstOrNull { it.characterId == characterId }
        ?: return
    val ct: CharacterTheme = character.theme

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
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = InkSoft,
                    )
                }
                OfflineBadge()
                Spacer(Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(8.dp))

                // Framed portrait
                FramedPortrait(
                    imageRes = ct.fullRes,
                    characterName = character.displayName,
                    accent = ct.accent,
                    width = 186.dp,
                    height = 232.dp,
                )

                Spacer(Modifier.height(18.dp))
                SmallLabel(text = ct.era, color = ct.accent)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = character.displayName,
                    fontFamily = SerifDisplay,
                    fontSize = 30.sp,
                    color = Ink,
                    lineHeight = 34.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = ct.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = InkSoft,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(18.dp))
                DiamondRule(color = Line)
                Spacer(Modifier.height(18.dp))

                Text(
                    text = ct.blurb,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                )

                Spacer(Modifier.height(22.dp))

                // Expertise section
                SmallLabel(
                    text = "Areas of expertise",
                    color = InkFaint,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                StrengthsGrid(strengths = ct.strengths, accent = ct.accent)

                Spacer(Modifier.height(22.dp))

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = LineSoft, shape = RoundedCornerShape(4.dp))
                        .background(color = PaperLt.copy(alpha = 0.4f))
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatChip(value = ct.docs.toString(), label = "Source works")
                    HairlineRule(modifier = Modifier.width(1.dp).height(40.dp), color = LineSoft)
                    StatChip(value = ct.chunks.formatThousands(), label = "Passages indexed")
                    HairlineRule(modifier = Modifier.width(1.dp).height(40.dp), color = LineSoft)
                    StatChip(value = "100%", label = "On device")
                }

                Spacer(Modifier.height(22.dp))

                // Begin button
                BeginButton(
                    label = "Begin the conversation",
                    accent = ct.accent,
                    deep = ct.deep,
                    onClick = { onBeginConversation(character.characterId) },
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FramedPortrait(
    imageRes: Int,
    characterName: String,
    accent: androidx.compose.ui.graphics.Color,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .background(color = PaperLt)
            .border(width = 1.dp, color = Line, shape = RoundedCornerShape(2.dp))
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(width = 1.dp, color = LineSoft, shape = RoundedCornerShape(1.dp))
                .clip(RoundedCornerShape(1.dp))
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = characterName,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize(),
            )
            // Inner vignette
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            0f to androidx.compose.ui.graphics.Color.Transparent,
                            0.6f to androidx.compose.ui.graphics.Color.Transparent,
                            1f to accent.copy(alpha = 0.08f),
                        )
                    )
            )
        }
    }
}

@Composable
private fun StrengthsGrid(strengths: List<String>, accent: androidx.compose.ui.graphics.Color) {
    val chunked = strengths.chunked(2)
    chunked.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            row.forEach { strength ->
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    // Diamond bullet
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(color = accent),
                    )
                    Text(
                        text = strength,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink,
                    )
                }
            }
            if (row.size < 2) Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatChip(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
        Text(
            text = value,
            fontFamily = SerifDisplay,
            fontSize = 23.sp,
            color = Ink,
            lineHeight = 24.sp,
        )
        Spacer(Modifier.height(5.dp))
        SmallLabel(text = label, color = InkFaint)
    }
}

@Composable
private fun BeginButton(
    label: String,
    accent: androidx.compose.ui.graphics.Color,
    deep: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(accent)
            .then(
                Modifier.border(
                    width = 0.dp,
                    color = androidx.compose.ui.graphics.Color.Transparent,
                    shape = RoundedCornerShape(4.dp),
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = label,
                fontFamily = SerifDisplay,
                fontSize = 18.sp,
                color = BubbleText,
                letterSpacing = 0.02.em,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = BubbleText.copy(alpha = 0.85f),
                modifier = Modifier.size(17.dp),
            )
        }
    }
}

private fun Int.formatThousands(): String {
    return if (this >= 1000) {
        val thousands = this / 1000
        val remainder = this % 1000
        if (remainder == 0) "${thousands},000"
        else "${thousands},${remainder.toString().padStart(3, '0')}"
    } else this.toString()
}
