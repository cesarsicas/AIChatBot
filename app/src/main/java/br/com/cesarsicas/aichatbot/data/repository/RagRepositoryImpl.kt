package br.com.cesarsicas.aichatbot.data.repository

import br.com.cesarsicas.aichatbot.data.local.EmbeddingModel
import br.com.cesarsicas.aichatbot.data.local.VectorDatabase
import br.com.cesarsicas.aichatbot.domain.model.Character
import br.com.cesarsicas.aichatbot.domain.repository.RagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RagRepositoryImpl @Inject constructor(
    private val embeddingModel: EmbeddingModel,
    private val vectorDatabase: VectorDatabase
) : RagRepository {

    override suspend fun buildContext(query: String, character: Character): String =
        withContext(Dispatchers.Default) {
            val embedding = embeddingModel.embed(query)
            val chunks = vectorDatabase.search(embedding, character.characterId)
            chunks.joinToString("\n\n") { "[${character.characterId}]: $it" }
        }

    override fun close() {
        embeddingModel.close()
    }
}
