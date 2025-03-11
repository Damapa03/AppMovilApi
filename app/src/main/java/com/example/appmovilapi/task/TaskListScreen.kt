package com.example.appmovilapi.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovilapi.Task
import com.example.appmovilapi.utils.decodeJwt
import com.example.appmovilapi.utils.getRolesFromJwt
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateTaskDialog by remember { mutableStateOf(false) }

    val errorShown = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        if (username.isNotEmpty() && token.isNotEmpty()) {
            taskViewModel.loadTasks(username)
        }
    }
    LaunchedEffect(key1 = uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty() && !errorShown.value) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            errorShown.value = true
        }
    }

    CreateTaskDialog(
        showDialog = showCreateTaskDialog,
        onDismiss = { showCreateTaskDialog = false },
        onCreateTask = { title, description ->
            scope.launch {
                taskViewModel.createTask(title, description, username)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas de $username") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                },
                actions = {
                    // Añadimos un botón de recarga opcional
                    IconButton(onClick = {
                        scope.launch {
                            errorShown.value = false // Resetear el estado del error
                            taskViewModel.refreshTasks(username)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar"
                        )
                    }
                }

            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateTaskDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar tarea"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.tasks.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.errorMessage.isNotEmpty()) {
                            // Mostrar mensaje específico para error de conexión
                            Text(
                                text = "No se pudieron cargar las tareas",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pulsa el botón de recarga para intentarlo de nuevo",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            // Mensaje para lista vacía sin error
                            Text(
                                text = "No tienes tareas pendientes",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pulsa el botón + para crear una nueva tarea",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    TaskList(
                        tasks = uiState.tasks,
                        onTaskCheckedChange = { taskId, isCompleted ->
                            scope.launch {
                                taskViewModel.updateTaskStatus(taskId, isCompleted)
                            }
                        },
                        onLongPress = { taskId ->
                            scope.launch {
                                taskViewModel.deleteTask(taskId)
                            }

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskCheckedChange: (String, Boolean) -> Unit,
    onLongPress: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        println("Lista tareas: $tasks")
        items(tasks) { task ->
            TaskItem(
                task = task,
                onTaskCheckedChange = onTaskCheckedChange,
                onLongPress = onLongPress
            )
        }
    }
}

