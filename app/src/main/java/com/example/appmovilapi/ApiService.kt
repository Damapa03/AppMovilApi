package com.example.appmovilapi

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface ApiService {
    @POST("/usuario/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/usuario/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @GET("/tarea/{username}")
    suspend fun getUserTasks(@Path("username") username: String, @Header("Authorization") token: String): List<Task>

    @POST("/tarea")
    suspend fun postTask(@Body tareaInsert: TaskInsert, @Header("Authorization") token: String)

    @PUT("/tarea/{taskId}")
    suspend fun updateTaskStatus(
        @Path("taskId") taskId: String,
        @Body updateRequest: TaskUpdateRequest,
        @Header("Authorization") token: String
    )

    @DELETE("/tarea/{id}")
    suspend fun deleteTask(@Path("id") id:String, @Header("Authorization") token: String)

    companion object {
        private const val BASE_URL = "https://api-rest-segura2-xres.onrender.com"

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}