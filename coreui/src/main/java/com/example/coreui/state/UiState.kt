package com.example.coreui.state

/**
 * UI State wrapper for managing screen states
 */
sealed class UiState<out T> {
    /**
     * Initial/Idle state
     */
    object Idle : UiState<Nothing>()
    
    /**
     * Loading state
     */
    object Loading : UiState<Nothing>()
    
    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Error state with message
     */
    data class Error(val message: String) : UiState<Nothing>()
}

/**
 * Extension functions for easier state checking
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error
fun <T> UiState<T>.isIdle(): Boolean = this is UiState.Idle

/**
 * Get data from success state or null
 */
fun <T> UiState<T>.getDataOrNull(): T? {
    return when (this) {
        is UiState.Success -> data
        else -> null
    }
}
