package com.um.eventosmovil.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.um.eventosmovil.data.LoginRequest
import com.um.eventosmovil.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val authService = AuthService()
    private val _state = MutableStateFlow<LoginState>(LoginState.Initial)
    val state: StateFlow<LoginState> = _state

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            val result = authService.authenticate(LoginRequest(username, password))
            result.onSuccess { token ->
                _state.value = LoginState.Success(token)
            }.onFailure {
                _state.value = LoginState.Error("Error real: ${it.message}")
            }
        }
    }
}