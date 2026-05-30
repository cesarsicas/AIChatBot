package br.com.cesarsicas.aichatbot.domain.usecase

import android.content.ContentResolver
import android.net.Uri
import br.com.cesarsicas.aichatbot.domain.repository.ChatRepository
import javax.inject.Inject

class ImportModelUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(contentResolver: ContentResolver, uri: Uri) =
        chatRepository.importModel(contentResolver, uri)
}
