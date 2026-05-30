package br.com.cesarsicas.aichatbot.presentation.characterselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cesarsicas.aichatbot.domain.model.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterSelectionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterSelectionUiState())
    val uiState: StateFlow<CharacterSelectionUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Character>()
    val navigationEvent: SharedFlow<Character> = _navigationEvent.asSharedFlow()

    fun onIntent(intent: CharacterSelectionIntent) {
        when (intent) {
            is CharacterSelectionIntent.SelectCharacter ->
                viewModelScope.launch { _navigationEvent.emit(intent.character) }
        }
    }
}
