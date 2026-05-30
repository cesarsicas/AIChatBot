package br.com.cesarsicas.aichatbot.presentation.navigation

sealed class Screen(val route: String) {
    data object CharacterSelection : Screen("character_selection")
    data object Chat : Screen("chat/{characterId}") {
        fun createRoute(characterId: String) = "chat/$characterId"
    }
}
