package br.com.cesarsicas.aichatbot

enum class Character(
    val characterId: String,
    val displayName: String,
    val description: String
) {
    SHERLOCK_HOLMES(
        characterId = "sherlock_holmes",
        displayName = "Sherlock Holmes",
        description = "World's greatest consulting detective"
    ),
    MARCUS_AURELIUS(
        characterId = "marcus_aurelius",
        displayName = "Marcus Aurelius",
        description = "Roman Emperor & Stoic philosopher"
    )
}
