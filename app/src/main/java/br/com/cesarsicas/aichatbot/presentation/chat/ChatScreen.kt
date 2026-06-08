package br.com.cesarsicas.aichatbot.presentation.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.cesarsicas.aichatbot.domain.model.Character
import br.com.cesarsicas.aichatbot.domain.model.ChatMessage
import br.com.cesarsicas.aichatbot.domain.model.ModelStatus
import br.com.cesarsicas.aichatbot.presentation.theme.CharacterTheme
import br.com.cesarsicas.aichatbot.presentation.theme.DiamondRule
import br.com.cesarsicas.aichatbot.presentation.theme.HairlineRule
import br.com.cesarsicas.aichatbot.presentation.theme.SmallLabel
import br.com.cesarsicas.aichatbot.presentation.theme.theme
import br.com.cesarsicas.aichatbot.ui.theme.BubbleText
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
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val ct = uiState.character?.theme

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (uiState.modelStatus is ModelStatus.Absent) {
            val pickFileLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri ->
                uri?.let { viewModel.onIntent(ChatIntent.ImportModel(context.contentResolver, it)) }
            }
            ModelSetupScreen(
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
                characterTheme = ct,
                onDownload = { viewModel.onIntent(ChatIntent.DownloadModel) },
                onPickFile = { pickFileLauncher.launch(arrayOf("*/*")) },
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            ChatHeader(
                character = uiState.character,
                ct = ct,
                modelStatus = uiState.modelStatus,
                onNavigateBack = onNavigateBack,
                onNavigateToSettings = onNavigateToSettings,
            )
            HairlineRule(color = Line)

            when (val status = uiState.modelStatus) {
                is ModelStatus.Transferring -> {
                    LinearProgressIndicator(
                        progress = { status.progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = ct?.accent ?: InkSoft,
                        trackColor = LineSoft,
                    )
                    Text(
                        text = "${status.label} · ${(status.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    )
                }
                is ModelStatus.Initializing -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = ct?.accent ?: InkSoft,
                        trackColor = LineSoft,
                    )
                    Text(
                        text = "Loading model…",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    )
                }
                is ModelStatus.Failure -> {
                    Text(
                        text = status.message,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                }
                else -> {}
            }

            val character = uiState.character
            val showEmptyState = uiState.messages.isEmpty() && uiState.modelStatus is ModelStatus.Ready

            if (showEmptyState && ct != null && character != null) {
                EmptyState(
                    character = character,
                    ct = ct,
                    modifier = Modifier.weight(1f),
                    onSuggestPrompt = { prompt ->
                        viewModel.onIntent(ChatIntent.UpdateInput(prompt))
                    },
                )
            } else {
                val listState = rememberLazyListState()

                LaunchedEffect(uiState.messages.size, uiState.isGenerating) {
                    if (uiState.messages.isNotEmpty()) {
                        listState.animateScrollToItem(uiState.messages.size - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                ) {
                    itemsIndexed(uiState.messages, key = { index, _ -> index }) { _, msg ->
                        val isThinkingPlaceholder = msg.role == ChatMessage.Role.ASSISTANT &&
                            msg.text.isEmpty() && uiState.isGenerating

                        when {
                            isThinkingPlaceholder && ct != null -> ThinkingCard(ct = ct)
                            msg.role == ChatMessage.Role.USER -> UserBubble(
                                text = msg.text,
                                accent = ct?.accent ?: InkSoft,
                            )
                            else -> CharBubble(text = msg.text, ct = ct)
                        }
                    }
                }
            }

            HairlineRule(color = LineSoft)
            val metrics = uiState.lastMetrics
            if (metrics != null && !uiState.isGenerating) {
                MetricsBar(metrics = metrics, accent = ct?.accent ?: InkSoft)
                HairlineRule(color = LineSoft)
            }
            InputBar(
                text = uiState.inputText,
                enabled = uiState.modelStatus is ModelStatus.Ready && !uiState.isGenerating,
                accent = ct?.accent,
                onTextChange = { viewModel.onIntent(ChatIntent.UpdateInput(it)) },
                onSend = { viewModel.onIntent(ChatIntent.SendMessage) },
            )
        }
    }
}

@Composable
private fun ChatHeader(
    character: Character?,
    ct: CharacterTheme?,
    modelStatus: ModelStatus,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
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

        if (ct != null) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = ct.accent, shape = CircleShape),
            ) {
                Image(
                    painter = painterResource(ct.avatarRes),
                    contentDescription = character?.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(Modifier.width(10.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            if (character != null) {
                Text(
                    text = character.displayName,
                    fontFamily = SerifDisplay,
                    fontSize = 18.sp,
                    color = Ink,
                    lineHeight = 20.sp,
                )
            }
            val statusText = when (modelStatus) {
                is ModelStatus.Ready -> "On-device · Ready"
                is ModelStatus.Initializing -> "Loading model…"
                is ModelStatus.Transferring -> "${(modelStatus.progress * 100).toInt()}% transferred"
                else -> "On-device"
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.FiberManualRecord,
                    contentDescription = null,
                    tint = if (modelStatus is ModelStatus.Ready) OnDeviceGreen else InkFaint,
                    modifier = Modifier.size(8.dp),
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkFaint,
                    letterSpacing = 0.08.em,
                )
            }
        }

        IconButton(onClick = onNavigateToSettings) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = InkSoft,
            )
        }
    }
}

@Composable
private fun EmptyState(
    character: Character,
    ct: CharacterTheme,
    modifier: Modifier = Modifier,
    onSuggestPrompt: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 136.dp, height = 170.dp)
                .background(ct.tint)
                .border(1.dp, Line, RoundedCornerShape(2.dp))
                .padding(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(1.dp)),
            ) {
                Image(
                    painter = painterResource(ct.bustRes),
                    contentDescription = character.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.TopCenter,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = character.displayName,
            fontFamily = SerifDisplay,
            fontSize = 24.sp,
            color = Ink,
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.CloudOff,
                contentDescription = null,
                tint = InkFaint,
                modifier = Modifier.size(13.dp),
            )
            Text(
                text = "Answers stay entirely on this device",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
                letterSpacing = 0.08.em,
            )
        }

        Spacer(Modifier.height(28.dp))

        SmallLabel(
            text = "Try asking",
            color = InkFaint,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        ct.suggestedPrompts.forEach { prompt ->
            SuggestChip(text = prompt, accent = ct.accent, onClick = { onSuggestPrompt(prompt) })
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SuggestChip(text: String, accent: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Line, RoundedCornerShape(24.dp))
            .background(PaperLt)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.ElectricBolt,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink,
        )
    }
}

@Composable
private fun ThinkingCard(ct: CharacterTheme) {
    val transition = rememberInfiniteTransition(label = "thinking")
    val dotAlphas = List(3) { index ->
        transition.animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.25f at 0
                    1f at 300
                    0.25f at 600
                    0.25f at 1200
                },
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(index * 200),
            ),
            label = "dot$index",
        )
    }
    val progressAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "progress",
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(30.dp)
                .clip(CircleShape)
                .border(1.5.dp, ct.accent, CircleShape),
        ) {
            Image(
                painter = painterResource(ct.avatarRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .background(PaperLt)
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .fillMaxHeight()
                    .background(ct.accent),
            )
            Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoStories,
                        contentDescription = null,
                        tint = ct.accent,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "Consulting the archive",
                        fontFamily = SerifDisplay,
                        fontSize = 14.sp,
                        color = Ink,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                        val dotAlphaValues = dotAlphas.map { it.value }
                        dotAlphaValues.forEach { alpha ->
                            Text(text = "·", fontSize = 16.sp, color = ct.accent.copy(alpha = alpha))
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                val fakeScores = listOf("0.87", "0.83", "0.79")
                ct.sourceWorks.forEachIndexed { i, work ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoStories,
                                contentDescription = null,
                                tint = ct.accent.copy(alpha = 0.55f),
                                modifier = Modifier.size(11.dp),
                            )
                            Text(
                                text = work,
                                style = MaterialTheme.typography.labelSmall,
                                color = InkSoft,
                            )
                        }
                        Text(
                            text = fakeScores.getOrElse(i) { "0.75" },
                            style = MaterialTheme.typography.labelSmall,
                            color = ct.accent.copy(alpha = 0.65f),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progressAnim },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp)),
                    color = ct.accent,
                    trackColor = ct.tint,
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudOff,
                        contentDescription = null,
                        tint = InkFaint,
                        modifier = Modifier.size(11.dp),
                    )
                    Text(
                        text = "Generating locally · no connection used",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint,
                    )
                }
            }
        }
    }
}

@Composable
private fun UserBubble(text: String, accent: Color) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 5.dp, bottomStart = 16.dp))
                .background(accent)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = BubbleText,
            )
        }
    }
}

@Composable
private fun CharBubble(text: String, ct: CharacterTheme?) {
    val accent = ct?.accent ?: InkSoft
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (ct != null) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, accent, CircleShape),
            ) {
                Image(
                    painter = painterResource(ct.avatarRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .background(PaperLt)
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .fillMaxHeight()
                    .background(accent),
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 10.dp)) {
                val showDropCap = text.length > 30 && text.first().isLetter()
                if (showDropCap) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = text.first().toString(),
                            fontFamily = SerifDisplay,
                            fontSize = 42.sp,
                            lineHeight = 36.sp,
                            color = accent,
                        )
                        Text(
                            text = text.drop(1),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink,
                        lineHeight = 24.sp,
                    )
                }
            }
        }

        Spacer(Modifier.width(30.dp))
    }
}

@Composable
private fun MetricsBar(metrics: InferenceMetrics, accent: Color) {
    fun Long.fmt() = if (this < 1000) "${this}ms" else "${"%.1f".format(this / 1000.0)}s"

    val items = listOf(
        "RAG" to metrics.ragMs.fmt(),
        "1st token" to metrics.timeToFirstTokenMs.fmt(),
        "Gen" to metrics.generationMs.fmt(),
        "Total" to metrics.totalMs.fmt(),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Paper)
            .padding(horizontal = 14.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        items.forEachIndexed { index, (label, value) ->
            if (index > 0) {
                Text(
                    text = " · ",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkFaint.copy(alpha = 0.4f),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
                letterSpacing = 0.06.em,
            )
            Text(
                text = " $value",
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                letterSpacing = 0.04.em,
            )
        }
    }
}

@Composable
private fun InputBar(
    text: String,
    enabled: Boolean,
    accent: Color?,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val effectiveAccent = accent ?: InkSoft
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Paper)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Ask a question…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkFaint,
                )
            },
            enabled = enabled,
            maxLines = 4,
            shape = RoundedCornerShape(24.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = effectiveAccent,
                unfocusedBorderColor = Line,
                focusedTextColor = Ink,
                unfocusedTextColor = Ink,
                cursorColor = effectiveAccent,
                focusedContainerColor = PaperLt,
                unfocusedContainerColor = PaperLt,
                disabledContainerColor = PaperLt,
                disabledBorderColor = LineSoft,
                disabledTextColor = InkFaint,
            ),
        )

        val canSend = enabled && text.isNotBlank()
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (canSend) effectiveAccent else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (canSend) effectiveAccent else Line,
                    shape = CircleShape,
                )
                .clickable(enabled = canSend, onClick = onSend),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (canSend) BubbleText else InkFaint,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
