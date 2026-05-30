package br.com.cesarsicas.aichatbot.presentation.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.cesarsicas.aichatbot.domain.model.ChatMessage
import br.com.cesarsicas.aichatbot.domain.model.ModelStatus

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState.modelStatus is ModelStatus.Absent) {
        val pickFileLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                viewModel.onIntent(ChatIntent.ImportModel(context.contentResolver, it))
            }
        }
        ModelSetupScreen(
            modifier = modifier,
            onDownload = { viewModel.onIntent(ChatIntent.DownloadModel) },
            onPickFile = { pickFileLauncher.launch(arrayOf("*/*")) }
        )
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        StatusBar(uiState.modelStatus)

        val listState = rememberLazyListState()
        LaunchedEffect(uiState.messages.size) {
            if (uiState.messages.isNotEmpty()) {
                listState.animateScrollToItem(uiState.messages.lastIndex)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(uiState.messages) { msg -> MessageBubble(msg) }
        }

        InputBar(
            text = uiState.inputText,
            enabled = uiState.modelStatus is ModelStatus.Ready && !uiState.isGenerating,
            onTextChange = { viewModel.onIntent(ChatIntent.UpdateInput(it)) },
            onSend = { viewModel.onIntent(ChatIntent.SendMessage) }
        )
    }
}

@Composable
private fun StatusBar(status: ModelStatus) {
    when (status) {
        is ModelStatus.Transferring -> {
            LinearProgressIndicator(
                progress = { status.progress },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "${status.label} ${(status.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        is ModelStatus.Initializing -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text(
                "Loading model…",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        is ModelStatus.Failure -> {
            Text(
                status.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
        }
        else -> {}
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == ChatMessage.Role.USER
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .align(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                .widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text.ifEmpty { "…" },
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun InputBar(
    text: String,
    enabled: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message…") },
            enabled = enabled,
            maxLines = 4
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onSend,
            enabled = enabled && text.isNotBlank()
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
        }
    }
}
