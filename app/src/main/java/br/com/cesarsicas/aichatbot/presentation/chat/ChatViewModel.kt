package br.com.cesarsicas.aichatbot.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cesarsicas.aichatbot.domain.model.ChatMessage
import br.com.cesarsicas.aichatbot.domain.repository.ChatRepository
import br.com.cesarsicas.aichatbot.domain.usecase.DownloadModelUseCase
import br.com.cesarsicas.aichatbot.domain.usecase.ImportModelUseCase
import br.com.cesarsicas.aichatbot.domain.usecase.InitializeEngineUseCase
import br.com.cesarsicas.aichatbot.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val downloadModelUseCase: DownloadModelUseCase,
    private val importModelUseCase: ImportModelUseCase,
    private val initializeEngineUseCase: InitializeEngineUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    companion object {
        private const val MAX_OUTPUT_TOKENS = 400
    }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.modelStatus.collect { status ->
                _uiState.update { it.copy(modelStatus = status) }
            }
        }
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.Initialize -> initialize(intent.character)
            is ChatIntent.DownloadModel -> downloadModel()
            is ChatIntent.ImportModel -> importModel(intent.contentResolver, intent.uri)
            is ChatIntent.UpdateInput -> _uiState.update { it.copy(inputText = intent.text) }
            is ChatIntent.SendMessage -> sendMessage()
        }
    }

    private fun initialize(character: br.com.cesarsicas.aichatbot.domain.model.Character) {
        _uiState.update { it.copy(character = character) }
        if (chatRepository.modelFileExists()) {
            viewModelScope.launch { initializeEngineUseCase() }
        }
    }

    private fun downloadModel() {
        viewModelScope.launch {
            downloadModelUseCase()
            if (chatRepository.modelFileExists()) {
                initializeEngineUseCase()
            }
        }
    }

    private fun importModel(
        contentResolver: android.content.ContentResolver,
        uri: android.net.Uri
    ) {
        viewModelScope.launch {
            importModelUseCase(contentResolver, uri)
            if (chatRepository.modelFileExists()) {
                initializeEngineUseCase()
            }
        }
    }

    private fun sendMessage() {
        val state = _uiState.value
        val text = state.inputText.trim()
        val character = state.character ?: return
        if (text.isBlank() || state.isGenerating) return

        viewModelScope.launch {
            _uiState.update { it.copy(
                messages = it.messages +
                    ChatMessage(ChatMessage.Role.USER, text) +
                    ChatMessage(ChatMessage.Role.ASSISTANT, ""),
                inputText = "",
                isGenerating = true
            ) }

            val augmented = sendMessageUseCase(text, character)

            var tokenCount = 0
            chatRepository.streamResponse(augmented).collect { token ->
                if (tokenCount >= MAX_OUTPUT_TOKENS) return@collect
                tokenCount++
                _uiState.update { s ->
                    val msgs = s.messages.toMutableList()
                    val last = msgs.last()
                    msgs[msgs.lastIndex] = last.copy(text = last.text + token)
                    s.copy(messages = msgs)
                }
            }
            _uiState.update { it.copy(isGenerating = false) }
        }
    }

    override fun onCleared() {
        chatRepository.close()
        super.onCleared()
    }
}
