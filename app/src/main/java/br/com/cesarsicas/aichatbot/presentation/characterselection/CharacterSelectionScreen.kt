package br.com.cesarsicas.aichatbot.presentation.characterselection

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.cesarsicas.aichatbot.domain.model.Character
import br.com.cesarsicas.aichatbot.presentation.theme.CharacterTheme
import br.com.cesarsicas.aichatbot.presentation.theme.ColloquyWordmark
import br.com.cesarsicas.aichatbot.presentation.theme.DiamondRule
import br.com.cesarsicas.aichatbot.presentation.theme.HairlineRule
import br.com.cesarsicas.aichatbot.presentation.theme.OfflineBadge
import br.com.cesarsicas.aichatbot.presentation.theme.SmallLabel
import br.com.cesarsicas.aichatbot.presentation.theme.theme
import br.com.cesarsicas.aichatbot.ui.theme.Ink
import br.com.cesarsicas.aichatbot.ui.theme.InkFaint
import br.com.cesarsicas.aichatbot.ui.theme.InkSoft
import br.com.cesarsicas.aichatbot.ui.theme.Line
import br.com.cesarsicas.aichatbot.ui.theme.LineSoft
import br.com.cesarsicas.aichatbot.ui.theme.PaperLt
import br.com.cesarsicas.aichatbot.ui.theme.SerifDisplay

@Composable
fun CharacterSelectionScreen(
    viewModel: CharacterSelectionViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            ColloquyWordmark()
            Spacer(Modifier.height(22.dp))

            Text(
                text = "Choose your\ninterlocutor",
                fontFamily = SerifDisplay,
                fontSize = 31.sp,
                color = Ink,
                lineHeight = 36.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Each speaks only from their own library —\nretrieved and reasoned on your device alone.",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSoft,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 22.sp,
            )
            Spacer(Modifier.height(18.dp))
            OfflineBadge()
            Spacer(Modifier.height(22.dp))

            uiState.characters.forEach { character ->
                CharCard(
                    character = character,
                    onClick = {
                        viewModel.onIntent(CharacterSelectionIntent.SelectCharacter(character))
                    },
                )
                Spacer(Modifier.height(16.dp))
            }

            // "More minds" placeholder
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .border(width = 1.dp, color = Line, shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        fontFamily = SerifDisplay,
                        fontSize = 20.sp,
                        color = InkFaint,
                    )
                }
                Text(
                    text = "More minds, coming soon",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = InkFaint,
                )
            }
        }
    }
}

@Composable
private fun CharCard(character: Character, onClick: () -> Unit) {
    val ct: CharacterTheme = character.theme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(PaperLt)
            .border(width = 1.dp, color = Line, shape = RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
    ) {
        // Portrait plate (left)
        Box(
            modifier = Modifier
                .width(124.dp)
                .height(160.dp)
                .background(ct.tint)
                .border(width = 0.dp, color = LineSoft, shape = RoundedCornerShape(0.dp)),
        ) {
            Image(
                painter = painterResource(ct.bustRes),
                contentDescription = character.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.TopCenter,
            )
            // Subtle bottom gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                ct.deep.copy(alpha = 0.20f),
                            )
                        )
                    )
            )
        }

        // Text area (right)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 15.dp),
        ) {
            SmallLabel(text = ct.era, color = ct.accent)
            Spacer(Modifier.height(7.dp))
            Text(
                text = character.displayName,
                fontFamily = SerifDisplay,
                fontSize = 22.sp,
                color = Ink,
                lineHeight = 24.sp,
            )
            Text(
                text = ct.subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = InkSoft,
            )
            Spacer(Modifier.height(11.dp))
            HairlineRule(color = LineSoft)
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${ct.docs} works · ${ct.chunks.formatThousands()} passages",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkFaint,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(width = 1.dp, color = ct.accent, shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = ct.accent,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
        }
    }
}

private fun Int.formatThousands(): String {
    return if (this >= 1000) {
        val thousands = this / 1000
        val remainder = this % 1000
        if (remainder == 0) "${thousands},000" else "${thousands},${remainder.toString().padStart(3, '0')}"
    } else this.toString()
}
