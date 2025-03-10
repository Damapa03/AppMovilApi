package com.example.appmovilapi.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovilapi.utils.obtenerRolesDesdeToken
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    username: String,
    onNavigateBack: () -> Unit,
    token: String,
    taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(token))
) {
    val uiState by taskViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val roles = obtenerRolesDesdeToken(token)


    // Efecto para cargar las tareas al entrar a la pantalla
    LaunchedEffect(username) {
        if("ROLE_ADMIN" in roles){
            ejecutarFuncionAdmin(taskViewModel)
        }
        taskViewModel.loadTasks(username)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas de $username") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Icono de flecha atrás
                        Text("←")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                // Mostrar indicador de carga centrado
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.errorMessage.isNotEmpty()) {
                // Mostrar mensaje de error
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar las tareas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                if ("ROLE_ADMIN" in roles){
                                    ejecutarFuncionAdmin(taskViewModel)
                                }
                                taskViewModel.loadTasks(username)
                            }
                        }
                    ) {
                        Text("Reintentar")
                    }
                }
            } else if (uiState.tasks.isEmpty()) {
                // Mostrar mensaje cuando no hay tareas
                Text(
                    text = "No hay tareas disponibles",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                // Lista de tareas
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.tasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskCheckedChange = { taskId, isCompleted ->
                                scope.launch {
                                    taskViewModel.updateTaskStatus(taskId, isCompleted)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
suspend fun ejecutarFuncionAdmin(taskViewModel: TaskViewModel) {
    println("🔹 Usuario con ROLE_ADMIN detectado. Ejecutando función especial...")
    // Aquí puedes realizar alguna acción especial, como cargar todas las tareas
    taskViewModel.loadAdminTasks()
}
