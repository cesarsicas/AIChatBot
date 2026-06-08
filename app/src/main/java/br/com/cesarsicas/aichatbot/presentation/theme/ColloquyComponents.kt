package br.com.cesarsicas.aichatbot.presentation.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import br.com.cesarsicas.aichatbot.ui.theme.Ink
import br.com.cesarsicas.aichatbot.ui.theme.InkFaint
import br.com.cesarsicas.aichatbot.ui.theme.InkSoft
import br.com.cesarsicas.aichatbot.ui.theme.Line
import br.com.cesarsicas.aichatbot.ui.theme.LineSoft
import br.com.cesarsicas.aichatbot.ui.theme.PaperLt
import br.com.cesarsicas.aichatbot.ui.theme.SerifDisplay

// "COLLOQUY" app wordmark with tagline and flanking hairlines
@Composable
fun ColloquyWordmark(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CONVERSATIONS ACROSS TIME",
            style = MaterialTheme.typography.labelSmall,
            color = InkFaint,
            letterSpacing = 0.22.em,
        )
        Spacer(Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            HairlineRule(width = 26.dp)
            Spacer(Modifier.width(12.dp))
            Text(
                text = "COLLOQUY",
                fontFamily = SerifDisplay,
                fontSize = 27.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.18.em,
                color = Ink,
            )
            Spacer(Modifier.width(12.dp))
            HairlineRule(width = 26.dp)
        }
    }
}

// Simple 1dp horizontal hairline
@Composable
fun HairlineRule(
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    color: Color = Line,
) {
    Canvas(
        modifier = if (width == Dp.Unspecified) modifier.fillMaxWidth().height(1.dp)
                   else modifier.width(width).height(1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx()
        )
    }
}

// Hairline with centered diamond divider
@Composable
fun DiamondRule(modifier: Modifier = Modifier, color: Color = Line) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HairlineRule(modifier = Modifier.weight(1f), color = color)
        Spacer(Modifier.width(10.dp))
        Canvas(Modifier.size(5.dp)) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height / 2f)
                lineTo(size.width / 2f, size.height)
                lineTo(0f, size.height / 2f)
                close()
            }
            drawPath(path, color = color)
        }
        Spacer(Modifier.width(10.dp))
        HairlineRule(modifier = Modifier.weight(1f), color = color)
    }
}

// Chip: "On-device · Offline" with cloud-off icon
@Composable
fun OfflineBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .border(width = 1.dp, color = Line, shape = androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.CloudOff,
            contentDescription = null,
            tint = InkSoft,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "ON-DEVICE · OFFLINE",
            style = MaterialTheme.typography.labelSmall,
            color = InkSoft,
            letterSpacing = 0.16.em,
        )
    }
}

// All-caps small label (era, category headings)
@Composable
fun SmallLabel(text: String, color: Color = InkFaint, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        letterSpacing = 0.22.em,
        modifier = modifier,
    )
}
