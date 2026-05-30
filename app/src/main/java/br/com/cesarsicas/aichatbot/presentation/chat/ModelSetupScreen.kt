package br.com.cesarsicas.aichatbot.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ModelSetupScreen(
    modifier: Modifier = Modifier,
    onDownload: () -> Unit,
    onPickFile: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("AI Chatbot", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "A local model (~1 GB) is required to start chatting.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onDownload, modifier = Modifier.fillMaxWidth()) {
            Text("Download model (Wi-Fi recommended)")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onPickFile, modifier = Modifier.fillMaxWidth()) {
            Text("Pick model from storage")
        }
    }
}
