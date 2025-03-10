package com.example.appmovilapi.utils

import com.auth0.jwt.JWT

fun obtenerRolesDesdeToken(token: String): List<String> {
    return try {
        val jwt = JWT.decode(token)
        val rolesClaim = jwt.getClaim("roles").asList(String::class.java) // Extrae el claim "roles"
        rolesClaim ?: emptyList()
    } catch (e: Exception) {
        emptyList() // Si hay un error, retorna una lista vacía
    }
}
