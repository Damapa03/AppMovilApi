package com.example.appmovilapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/usuario/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/usuario/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @GET("/tarea/{username}")
    suspend fun getUserTasks(@Path("username") username: String, @Header("Authorization") token: String): List<Task>

    @GET("/tarea")
    suspend fun getAllTasks(@Header("Authorization") token: String): List<Task>

    @POST("/tarea")
    suspend fun postTask(@Body tareaInsert: TaskInsert, @Header("Authorization") token: String)

    @PUT("/tasks/{taskId}")
    suspend fun updateTaskStatus(
        @Path("taskId") taskId: Int,
        @Body updateRequest: TaskUpdateRequest,
        @Header("Authorization") token: String
    )

    @DELETE("/tarea/{id}")
    suspend fun deleteTask(@Path("id") id:String, @Header("Authorization") token: String)

    companion object {
        private const val BASE_URL = "https://api-rest-segura2-xres.onrender.com"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}