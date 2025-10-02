package com.example.feature.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.Resource
import com.example.coreui.state.UiState
import com.example.feature.users.domain.model.User
import com.example.feature.users.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel for Users screen
 * Presentation layer - manages UI state
 */
@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {
    
    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()
    
    init {
        getUsers()
    }
    
    /**
     * Fetch users and update UI state based on Resource states
     */
    fun getUsers() {
        getUsersUseCase(Unit).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _usersState.value = UiState.Loading
                }
                is Resource.Success -> {
                    _usersState.value = UiState.Success(resource.data ?: emptyList())
                }
                is Resource.Error -> {
                    _usersState.value = UiState.Error(
                        resource.message ?: "An unexpected error occurred"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
    
    /**
     * Retry loading users
     */
    fun retry() {
        getUsers()
    }
}
