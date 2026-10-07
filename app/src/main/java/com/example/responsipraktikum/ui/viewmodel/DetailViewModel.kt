package com.example.responsipraktikum.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.responsipraktikum.data.api.ApiClient
import com.example.responsipraktikum.data.model.GameItem
import com.example.responsipraktikum.data.repository.GameRepository
import com.example.responsipraktikum.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Game Detail Screen.
 * Fetches and holds detail state for a single video game.
 */
class DetailViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<GameItem>>(UiState.Loading)
    val uiState: StateFlow<UiState<GameItem>> = _uiState.asStateFlow()

    fun loadGameDetail(gameId: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getGameDetail(gameId = gameId, apiKey = ApiClient.currentApiKey)
            result.fold(
                onSuccess = { game ->
                    _uiState.value = UiState.Success(game)
                },
                onFailure = { throwable ->
                    _uiState.value = UiState.Error(throwable.localizedMessage ?: "Failed to load game details.")
                }
            )
        }
    }

    class Factory(private val repository: GameRepository = GameRepository()) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(repository) as T
        }
    }
}
