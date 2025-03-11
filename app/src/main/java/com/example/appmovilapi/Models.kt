package com.example.appmovilapi

import com.example.appmovilapi.register.Direccion

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

// Register Models
data class RegisterRequest(
    val username: String,
    val password: String,
    val passwordRepeat: String,
    val direccion: Direccion
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)

data class Task(
    val _id: String,
    val titulo: String,
    val descripcion: String,
    val usuario: String,
    val fechaCreacion: String, // Podría ser LocalDateTime si se usa un conversor adecuado
    val completada: Boolean
)

data class TaskInsert(
    val titulo: String,
    val descripcion: String,
    val usuario: String
)

// Solicitud para actualizar el estado de una tarea
data class TaskUpdateRequest(
    val estado: Boolean
)
