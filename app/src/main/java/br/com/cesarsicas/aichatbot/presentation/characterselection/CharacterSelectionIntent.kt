package br.com.cesarsicas.aichatbot.presentation.characterselection

import br.com.cesarsicas.aichatbot.domain.model.Character

sealed interface CharacterSelectionIntent {
    data class SelectCharacter(val character: Character) : CharacterSelectionIntent
}
