package br.com.cesarsicas.aichatbot.domain.repository

import android.content.ContentResolver
import android.net.Uri
import br.com.cesarsicas.aichatbot.domain.model.ModelStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatRepository {
    val modelStatus: StateFlow<ModelStatus>
    fun modelFileExists(): Boolean
    suspend fun downloadModel()
    suspend fun importModel(contentResolver: ContentResolver, uri: Uri)
    suspend fun initializeEngine()
    fun streamResponse(augmentedPrompt: String): Flow<String>
    fun close()
}
