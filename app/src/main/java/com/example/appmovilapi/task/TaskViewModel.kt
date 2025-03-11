package com.example.appmovilapi.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilapi.ApiService
import com.example.appmovilapi.Task
import com.example.appmovilapi.TaskInsert
import com.example.appmovilapi.TaskUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(private val token: String) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private var initialLoadAttempted = false

    private val bearerToken = "Bearer $token"

    private val apiService = ApiService.create()

    suspend fun loadTasks(username: String, forceReload: Boolean = false) {
        if (initialLoadAttempted && !forceReload) {
            return
        }
        try {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            try {
                val tasks = apiService.getUserTasks(username, bearerToken)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = tasks,
                        errorMessage = ""
                    )
                }
            }catch (e: Exception){
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = emptyList(),
                        errorMessage = "Error de conexión: ${e.localizedMessage}"
                    )
                }
                println("Error de conexión: ${e.message}")
            }

            initialLoadAttempted = true

        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.localizedMessage}"
                )
            }
            initialLoadAttempted = true
        }
    }

    fun refreshTasks(username: String) {
        // Lanzamos una coroutine en el viewModelScope
        viewModelScope.launch {
            loadTasks(username, forceReload = true)
        }
    }

    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        try {
            val updateRequest = TaskUpdateRequest(estado = isCompleted)
            apiService.updateTaskStatus(taskId, updateRequest,bearerToken)

            // Actualizar la lista local después de actualizar en el servidor
            _uiState.update { currentState ->
                val updatedTasks = currentState.tasks.map { task ->
                    if (task.id == taskId) {
                        task.copy(completada = isCompleted)
                    } else {
                        task
                    }
                }
                currentState.copy(tasks = updatedTasks)
            }
        } catch (e: Exception) {
            // En caso de error, no cambiamos el estado local
            _uiState.update {
                it.copy(
                    errorMessage = "Error al actualizar: ${e.localizedMessage}"
                )
            }
        }
    }

    suspend fun createTask(title: String, description: String, username: String) {
        try {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            val taskInsert = TaskInsert(
                titulo = title,
                descripcion = description,
                usuario = username
            )

            apiService.postTask(taskInsert, bearerToken)

            // Recargar la lista de tareas para mostrar la nueva tarea
            loadTasks(username, forceReload = true)

        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Error al crear tarea: ${e.localizedMessage}"
                )
            }
        }
    }

}

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)