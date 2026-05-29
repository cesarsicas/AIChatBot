package br.com.cesarsicas.aichatbot

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class ChatViewModel(
    application: Application,
    val character: Character
) : AndroidViewModel(application) {

    class Factory(
        private val application: Application,
        private val character: Character
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ChatViewModel(application, character) as T
    }

    companion object {
        private const val TAG = "ChatViewModel"
        private const val MAX_OUTPUT_TOKENS = 400
        // Update this URL to the exact .litertlm file on https://huggingface.co/litert-community
        private const val MODEL_URL =
            "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.litertlm"
        private const val MODEL_FILENAME = "gemma3-1b-it-int4.litertlm"
    }

    sealed class EngineState {
        data object Idle : EngineState()
        data class Downloading(val progress: Float, val label: String = "Downloading model…") : EngineState()
        data object Initializing : EngineState()
        data object Ready : EngineState()
        data class Error(val message: String) : EngineState()
    }

    private val _engineState = MutableStateFlow<EngineState>(EngineState.Idle)
    val engineState: StateFlow<EngineState> = _engineState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private var engine: Engine? = null
    private var conversation: Conversation? = null
    private var ragRepository: RagRepository? = null

    private val modelFile get() = File(getApplication<Application>().filesDir, MODEL_FILENAME)

    init {
        // Auto-init only if the model was already downloaded in a previous session
        if (modelFile.exists()) {
            viewModelScope.launch(Dispatchers.IO) { initializeEngine() }
        }
    }

    fun downloadModel() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadModelInternal()
            if (modelFile.exists()) initializeEngine()
        }
    }

    fun importModelFromUri(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileSize = contentResolver.query(
                    uri, arrayOf(OpenableColumns.SIZE), null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getLong(0) else -1L
                } ?: -1L

                _engineState.value = EngineState.Downloading(0f, "Copying model…")
                val tempFile = File(getApplication<Application>().filesDir, "$MODEL_FILENAME.tmp")
                contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        val buffer = ByteArray(32_768)
                        var bytesRead = 0L
                        var n: Int
                        while (input.read(buffer).also { n = it } != -1) {
                            output.write(buffer, 0, n)
                            bytesRead += n
                            if (fileSize > 0) {
                                _engineState.value = EngineState.Downloading(
                                    bytesRead.toFloat() / fileSize, "Copying model…"
                                )
                            }
                        }
                    }
                }
                tempFile.renameTo(modelFile)
                initializeEngine()
            } catch (e: Exception) {
                Log.e(TAG, "Import failed", e)
                _engineState.value = EngineState.Error("Import failed: ${e.message}")
            }
        }
    }

    private suspend fun downloadModelInternal() {
        try {
            _engineState.value = EngineState.Downloading(0f, "Downloading model…")
            val connection = URL(MODEL_URL).openConnection()
            connection.connect()
            val totalBytes = connection.contentLengthLong
            val tempFile = File(getApplication<Application>().filesDir, "$MODEL_FILENAME.tmp")
            connection.getInputStream().use { input ->
                tempFile.outputStream().use { output ->
                    val buffer = ByteArray(32_768)
                    var bytesRead = 0L
                    var n: Int
                    while (input.read(buffer).also { n = it } != -1) {
                        output.write(buffer, 0, n)
                        bytesRead += n
                        if (totalBytes > 0) {
                            _engineState.value = EngineState.Downloading(
                                bytesRead.toFloat() / totalBytes, "Downloading model…"
                            )
                        }
                    }
                }
            }
            tempFile.renameTo(modelFile)
        } catch (e: Exception) {
            Log.e(TAG, "Download failed", e)
            _engineState.value = EngineState.Error("Download failed: ${e.message}")
        }
    }

    private fun initializeEngine() {
        try {
            _engineState.value = EngineState.Initializing
            engine = Engine(EngineConfig(modelPath = modelFile.absolutePath, maxNumTokens = 2048))
            engine!!.initialize()
            conversation = engine!!.createConversation(ConversationConfig())
            ragRepository = RagRepository(getApplication())
            _engineState.value = EngineState.Ready
        } catch (e: Exception) {
            Log.e(TAG, "Engine init failed", e)
            _engineState.value = EngineState.Error("Initialization failed: ${e.message}")
        }
    }

    fun sendMessage(text: String) {
        val conv = conversation ?: return
        viewModelScope.launch {
            _messages.value += ChatMessage(ChatMessage.Role.USER, text)
            _isGenerating.value = true
            _messages.value += ChatMessage(ChatMessage.Role.ASSISTANT, "")

            val augmented = withContext(Dispatchers.IO) {
                val rag = ragRepository
                if (rag != null) {
                    val ctx = rag.buildContext(text, character)
                    "Use the following context to answer the question.\n\nContext:\n$ctx\n\nQuestion: $text"
                } else {
                    text
                }
            }

            try {
                var tokenCount = 0
                conv.sendMessageAsync(augmented).collect { response ->
                    if (tokenCount >= MAX_OUTPUT_TOKENS) return@collect
                    val token = response.contents.contents
                        .filterIsInstance<Content.Text>()
                        .joinToString("") { it.text }
                    tokenCount++
                    val updated = _messages.value.toMutableList()
                    val last = updated.last()
                    updated[updated.lastIndex] = last.copy(text = last.text + token)
                    _messages.value = updated
                }
            } catch (e: Exception) {
                val updated = _messages.value.toMutableList()
                updated[updated.lastIndex] = updated.last().copy(text = "Error: ${e.message}")
                _messages.value = updated
            } finally {
                _isGenerating.value = false
            }
        }
    }

    override fun onCleared() {
        ragRepository?.close()
        conversation?.close()
        engine?.close()
        super.onCleared()
    }
}
