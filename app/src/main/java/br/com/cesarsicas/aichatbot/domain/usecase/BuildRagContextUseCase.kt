package br.com.cesarsicas.aichatbot.domain.usecase

import br.com.cesarsicas.aichatbot.domain.model.Character
import br.com.cesarsicas.aichatbot.domain.repository.RagRepository
import javax.inject.Inject

class BuildRagContextUseCase @Inject constructor(
    private val ragRepository: RagRepository
) {
    suspend operator fun invoke(query: String, character: Character): String =
        ragRepository.buildContext(query, character)
}
