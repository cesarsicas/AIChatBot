package br.com.cesarsicas.aichatbot.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.cesarsicas.aichatbot.domain.model.Character
import br.com.cesarsicas.aichatbot.presentation.characterselection.CharacterSelectionScreen
import br.com.cesarsicas.aichatbot.presentation.characterselection.CharacterSelectionViewModel
import br.com.cesarsicas.aichatbot.presentation.chat.ChatIntent
import br.com.cesarsicas.aichatbot.presentation.chat.ChatScreen
import br.com.cesarsicas.aichatbot.presentation.chat.ChatViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CharacterSelection.route,
        modifier = modifier
    ) {
        composable(Screen.CharacterSelection.route) {
            val viewModel: CharacterSelectionViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { character ->
                    navController.navigate(Screen.Chat.createRoute(character.characterId))
                }
            }
            CharacterSelectionScreen(viewModel = viewModel)
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: return@composable
            val character = Character.entries.firstOrNull { it.characterId == characterId }
                ?: return@composable
            val viewModel: ChatViewModel = hiltViewModel()
            LaunchedEffect(character) {
                viewModel.onIntent(ChatIntent.Initialize(character))
            }
            ChatScreen(viewModel = viewModel)
        }
    }
}
