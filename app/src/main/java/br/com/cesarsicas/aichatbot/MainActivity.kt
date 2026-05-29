
package br.com.cesarsicas.aichatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.cesarsicas.aichatbot.ui.theme.AIChatbotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIChatbotTheme {
                var selectedCharacter by remember { mutableStateOf<Character?>(null) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val character = selectedCharacter
                    if (character == null) {
                        CharacterSelectionScreen(
                            modifier = Modifier.padding(innerPadding),
                            onCharacterSelected = { selectedCharacter = it }
                        )
                    } else {
                        ChatScreen(
                            character = character,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
