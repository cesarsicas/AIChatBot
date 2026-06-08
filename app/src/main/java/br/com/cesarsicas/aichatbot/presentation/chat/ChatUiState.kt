package br.com.cesarsicas.aichatbot.presentation.chat

import br.com.cesarsicas.aichatbot.domain.model.Character
import br.com.cesarsicas.aichatbot.domain.model.ChatMessage
import br.com.cesarsicas.aichatbot.domain.model.ModelStatus

data class InferenceMetrics(
    val ragMs: Long,
    val timeToFirstTokenMs: Long,
    val generationMs: Long,
    val totalMs: Long,
)

data class ChatUiState(
    val modelStatus: ModelStatus = ModelStatus.Absent,
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val inputText: String = "",
    val character: Character? = null,
    val lastMetrics: InferenceMetrics? = null,
)
