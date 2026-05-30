package br.com.cesarsicas.aichatbot.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import br.com.cesarsicas.aichatbot.domain.model.ModelStatus
import br.com.cesarsicas.aichatbot.domain.repository.ChatRepository
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.SamplerConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ChatRepository {

    companion object {
        private const val TAG = "ChatRepositoryImpl"
        private const val MAX_CONTEXT_TOKENS = 2048
        private const val MODEL_URL =
            "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.litertlm"
        private const val MODEL_FILENAME = "gemma3-1b-it-int4.litertlm"
    }

    private val modelFile get() = File(context.filesDir, MODEL_FILENAME)

    private val _modelStatus = MutableStateFlow<ModelStatus>(
        if (File(context.filesDir, MODEL_FILENAME).exists()) ModelStatus.Initializing
        else ModelStatus.Absent
    )
    override val modelStatus: StateFlow<ModelStatus> = _modelStatus.asStateFlow()

    private var engine: Engine? = null
    private var conversation: Conversation? = null

    override fun modelFileExists(): Boolean = modelFile.exists()

    override suspend fun downloadModel() {
        withContext(Dispatchers.IO) {
            try {
                _modelStatus.value = ModelStatus.Transferring(0f, "Downloading model…")
                val connection = URL(MODEL_URL).openConnection()
                connection.connect()
                val totalBytes = connection.contentLengthLong
                val tempFile = File(context.filesDir, "$MODEL_FILENAME.tmp")
                connection.getInputStream().use { input ->
                    tempFile.outputStream().use { output ->
                        val buffer = ByteArray(32_768)
                        var bytesRead = 0L
                        var n: Int
                        while (input.read(buffer).also { n = it } != -1) {
                            output.write(buffer, 0, n)
                            bytesRead += n
                            if (totalBytes > 0) {
                                _modelStatus.value = ModelStatus.Transferring(
                                    bytesRead.toFloat() / totalBytes, "Downloading model…"
                                )
                            }
                        }
                    }
                }
                tempFile.renameTo(modelFile)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Download failed", e)
                _modelStatus.value = ModelStatus.Failure("Download failed: ${e.message}")
            }
        }
    }

    override suspend fun importModel(contentResolver: ContentResolver, uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                val fileSize = contentResolver.query(
                    uri, arrayOf(OpenableColumns.SIZE), null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getLong(0) else -1L
                } ?: -1L

                _modelStatus.value = ModelStatus.Transferring(0f, "Copying model…")
                val tempFile = File(context.filesDir, "$MODEL_FILENAME.tmp")
                contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        val buffer = ByteArray(32_768)
                        var bytesRead = 0L
                        var n: Int
                        while (input.read(buffer).also { n = it } != -1) {
                            output.write(buffer, 0, n)
                            bytesRead += n
                            if (fileSize > 0) {
                                _modelStatus.value = ModelStatus.Transferring(
                                    bytesRead.toFloat() / fileSize, "Copying model…"
                                )
                            }
                        }
                    }
                }
                tempFile.renameTo(modelFile)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Import failed", e)
                _modelStatus.value = ModelStatus.Failure("Import failed: ${e.message}")
            }
        }
    }

    override suspend fun initializeEngine() {
        withContext(Dispatchers.IO) {
            try {
                _modelStatus.value = ModelStatus.Initializing
                val sampler = SamplerConfig(topK = 40, topP = 0.9, temperature = 0.4, seed = 0)
                engine = Engine(EngineConfig(modelPath = modelFile.absolutePath, maxNumTokens = MAX_CONTEXT_TOKENS))
                engine!!.initialize()
                conversation = engine!!.createConversation(ConversationConfig(samplerConfig = sampler))
                _modelStatus.value = ModelStatus.Ready
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Engine init failed", e)
                _modelStatus.value = ModelStatus.Failure("Initialization failed: ${e.message}")
            }
        }
    }

    override fun streamResponse(augmentedPrompt: String): Flow<String> {
        val conv = conversation ?: return flow { emit("Error: engine not initialized") }
        return flow {
            conv.sendMessageAsync(augmentedPrompt).collect { response ->
                val token = response.contents.contents
                    .filterIsInstance<Content.Text>()
                    .joinToString("") { it.text }
                if (token.isNotEmpty()) emit(token)
            }
        }.flowOn(Dispatchers.Default)
            .catch { e -> emit("Error: ${e.message}") }
    }

    override fun close() {
        conversation?.close()
        engine?.close()
        conversation = null
        engine = null
    }
}
