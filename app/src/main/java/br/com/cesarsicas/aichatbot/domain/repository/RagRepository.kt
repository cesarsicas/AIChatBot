package br.com.cesarsicas.aichatbot.domain.repository

import br.com.cesarsicas.aichatbot.domain.model.Character

interface RagRepository {
    suspend fun buildContext(query: String, character: Character): String
    fun close()
}
