package br.com.cesarsicas.aichatbot.presentation.characterselection

import br.com.cesarsicas.aichatbot.domain.model.Character

data class CharacterSelectionUiState(
    val characters: List<Character> = Character.entries,
    val isLoading: Boolean = false
)
