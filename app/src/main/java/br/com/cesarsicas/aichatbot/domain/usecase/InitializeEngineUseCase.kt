package br.com.cesarsicas.aichatbot.domain.usecase

import br.com.cesarsicas.aichatbot.domain.repository.ChatRepository
import javax.inject.Inject

class InitializeEngineUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke() = chatRepository.initializeEngine()
}
