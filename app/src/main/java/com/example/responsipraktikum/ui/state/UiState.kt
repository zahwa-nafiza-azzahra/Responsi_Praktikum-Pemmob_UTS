package com.example.responsipraktikum.ui.state

/**
 * Sealed interface representing the UI state for state-driven Compose layout.
 */
sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
