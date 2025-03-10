package com.example.appmovilapi

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appmovilapi.login.LoginScreen
import com.example.appmovilapi.register.RegisterScreen
import com.example.appmovilapi.task.TaskListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToTask = { token, username ->
                    navController.navigate("tasks/$username/$token")
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        // Pop up to login so pressing back won't take us back to register
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "tasks/{username}/{token}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val token = backStackEntry.arguments?.getString("token") ?: ""
            TaskListScreen(
                username = username,
                token = token,
                onNavigateBack = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
    }
}