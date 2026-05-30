package br.com.cesarsicas.aichatbot.domain.usecase

import br.com.cesarsicas.aichatbot.domain.model.Character
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val buildRagContextUseCase: BuildRagContextUseCase
) {
    suspend operator fun invoke(text: String, character: Character): String {
        val context = buildRagContextUseCase(text, character)
        return "Use the following context to answer the question.\n\nContext:\n$context\n\nQuestion: $text"
    }
}
