package br.com.cesarsicas.aichatbot.domain.model

data class ChatMessage(
    val role: Role,
    val text: String
) {
    enum class Role { USER, ASSISTANT }
}
