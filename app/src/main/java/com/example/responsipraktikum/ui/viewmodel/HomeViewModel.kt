package com.example.responsipraktikum.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.responsipraktikum.data.api.ApiClient
import com.example.responsipraktikum.data.model.GameItem
import com.example.responsipraktikum.data.repository.GameRepository
import com.example.responsipraktikum.ui.state.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * ViewModel for Home Screen.
 * Manages search query state, API key state, and UI state for games list.
 */
@OptIn(FlowPreview::class)
class HomeViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _apiKey = MutableStateFlow(ApiClient.currentApiKey)
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<GameItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<GameItem>>> = _uiState.asStateFlow()

    init {
        fetchGames()

        // Observe search query with debounce for smooth user experience
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    fetchGames(query)
                }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onApiKeyChanged(newKey: String) {
        _apiKey.value = newKey
        ApiClient.currentApiKey = newKey
        fetchGames(_searchQuery.value)
    }

    fun fetchGames(query: String = _searchQuery.value) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getGames(query = query, apiKey = _apiKey.value)
            result.fold(
                onSuccess = { games ->
                    _uiState.value = UiState.Success(games)
                },
                onFailure = { throwable ->
                    _uiState.value = UiState.Error(throwable.localizedMessage ?: "An error occurred while loading games.")
                }
            )
        }
    }

    class Factory(private val repository: GameRepository = GameRepository()) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
