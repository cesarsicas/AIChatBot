package br.com.cesarsicas.aichatbot.presentation.chat

import android.content.ContentResolver
import android.net.Uri
import br.com.cesarsicas.aichatbot.domain.model.Character

sealed interface ChatIntent {
    data class Initialize(val character: Character) : ChatIntent
    data object DownloadModel : ChatIntent
    data class ImportModel(val contentResolver: ContentResolver, val uri: Uri) : ChatIntent
    data class UpdateInput(val text: String) : ChatIntent
    data object SendMessage : ChatIntent
}
