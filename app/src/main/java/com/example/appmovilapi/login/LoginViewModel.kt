package com.example.appmovilapi.login

import androidx.lifecycle.ViewModel
import com.example.appmovilapi.ApiService
import com.example.appmovilapi.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val apiService = ApiService.create()

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    suspend fun login() {
        try {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            val loginRequest = LoginRequest(
                username = uiState.value.username,
                password = uiState.value.password
            )

            val response = apiService.login(loginRequest)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    token = response.token,
                    errorMessage = ""
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.localizedMessage}"
                )
            }
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val token: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)