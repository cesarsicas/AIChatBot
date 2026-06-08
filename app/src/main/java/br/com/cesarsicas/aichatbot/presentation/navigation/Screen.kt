package br.com.cesarsicas.aichatbot.presentation.navigation

sealed class Screen(val route: String) {
    data object CharacterSelection : Screen("character_selection")
    data object CharacterDetail : Screen("character_detail/{characterId}") {
        fun createRoute(characterId: String) = "character_detail/$characterId"
    }
    data object Chat : Screen("chat/{characterId}") {
        fun createRoute(characterId: String) = "chat/$characterId"
    }
    data object Settings : Screen("settings")
}
