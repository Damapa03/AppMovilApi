package com.example.appmovilapi.register

import androidx.lifecycle.ViewModel
import com.example.appmovilapi.ApiService
import com.example.appmovilapi.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val apiService = ApiService.create()

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = validatePasswords(password, it.passwordRepeat)
            )
        }
    }

    fun updatePasswordRepeat(passwordRepeat: String) {
        _uiState.update {
            it.copy(
                passwordRepeat = passwordRepeat,
                passwordError = validatePasswords(it.password, passwordRepeat)
            )
        }
    }

    fun updateDireccionCalle(calle: String) {
        _uiState.update {
            val updatedDireccion = it.direccion.copy(calle = calle)
            it.copy(direccion = updatedDireccion)
        }
    }

    fun updateDireccionNumero(numero: String) {
        _uiState.update {
            val updatedDireccion = it.direccion.copy(num = numero)
            it.copy(direccion = updatedDireccion)
        }
    }

    fun updateDireccionCP(cp: String) {
        _uiState.update {
            val updatedDireccion = it.direccion.copy(cp = cp)
            it.copy(direccion = updatedDireccion)
        }
    }

    fun updateDireccionProvincia(provincia: String) {
        _uiState.update {
            val updatedDireccion = it.direccion.copy(provincia = provincia)
            it.copy(direccion = updatedDireccion)
        }
    }

    fun updateDireccionMunicipio(municipio: String) {
        _uiState.update {
            val updatedDireccion = it.direccion.copy(municipio = municipio)
            it.copy(direccion = updatedDireccion)
        }
    }

    private fun validatePasswords(password: String, passwordRepeat: String): String? {
        return if (password.isNotEmpty() && passwordRepeat.isNotEmpty() && password != passwordRepeat) {
            "Las contraseñas no coinciden"
        } else {
            null
        }
    }

    suspend fun register() {
        try {
            if (uiState.value.passwordError != null) {
                return
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            val registerRequest = RegisterRequest(
                username = uiState.value.username,
                password = uiState.value.password,
                passwordRepeat = uiState.value.passwordRepeat,
                direccion = uiState.value.direccion
            )

            val response = apiService.register(registerRequest)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    registrationSuccess = true,
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

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val passwordRepeat: String = "",
    val passwordError: String? = null,
    val direccion: Direccion = Direccion(),
    val isLoading: Boolean = false,
    val registrationSuccess: Boolean = false,
    val errorMessage: String = ""
)

data class Direccion(
    val calle: String = "",
    val num: String = "",
    val cp: String = "",
    val provincia: String = "",
    val municipio: String = ""
)