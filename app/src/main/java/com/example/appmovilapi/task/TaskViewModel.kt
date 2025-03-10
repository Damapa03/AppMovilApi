package com.example.appmovilapi.task

import androidx.lifecycle.ViewModel
import com.example.appmovilapi.ApiService
import com.example.appmovilapi.Task
import com.example.appmovilapi.TaskUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel(private val token: String) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private val apiService = ApiService.create()

    suspend fun loadAdminTasks(){
        try {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            val tasks = apiService.getAllTasks(token)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    tasks = tasks,
                    errorMessage = ""
                )
            }

        }catch (e:Exception){
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.localizedMessage}"
                )
            }
        }
    }
    suspend fun loadTasks(username: String) {
        try {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            val tasks = apiService.getUserTasks(username, token)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    tasks = tasks,
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

    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        try {
            val updateRequest = TaskUpdateRequest(null,null,null,completada = isCompleted)
            apiService.updateTaskStatus(taskId, updateRequest,token)

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
}

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)