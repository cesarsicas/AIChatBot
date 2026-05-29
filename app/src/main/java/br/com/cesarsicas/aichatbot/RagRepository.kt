package br.com.cesarsicas.aichatbot

import android.content.Context

class RagRepository(context: Context) {

    private val embeddingModel = EmbeddingModel(context)
    private val vectorDatabase = VectorDatabase(context)

    fun buildContext(query: String, character: Character): String {
        val embedding = embeddingModel.embed(query)
        val chunks = vectorDatabase.search(embedding, character.characterId)
        return chunks.joinToString("\n\n") { "[${character.characterId}]: $it" }
    }

    fun close() {
        embeddingModel.close()
    }
}
