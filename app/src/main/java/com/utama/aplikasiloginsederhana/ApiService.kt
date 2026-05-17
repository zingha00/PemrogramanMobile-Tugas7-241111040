package com.utama.aplikasiloginsederhana

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── EVENT ENDPOINTS ─────────────────────────────────────────

    @GET("events.php")
    suspend fun getAllEvents(): Response<ApiResponse<List<EventApiModel>>>

    @GET("events.php")
    suspend fun getEventById(
        @Query("id") id: Int
    ): Response<ApiResponse<EventApiModel>>

    @POST("events.php")
    suspend fun addEvent(
        @Body event: EventRequest
    ): Response<ApiResponse<Map<String, Int>>>

    @PUT("events.php")
    suspend fun updateEvent(
        @Query("id") id: Int,
        @Body event: EventRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("events.php")
    suspend fun deleteEvent(
        @Query("id") id: Int
    ): Response<ApiResponse<Unit>>

    // ── AUTH ENDPOINTS ──────────────────────────────────────────

    @POST("auth.php")
    suspend fun login(
        @Query("action") action: String = "login",
        @Body request: LoginRequest
    ): Response<ApiResponse<UserApiModel>>

    @POST("auth.php")
    suspend fun register(
        @Query("action") action: String = "register",
        @Body request: RegisterRequest
    ): Response<ApiResponse<Map<String, Int>>>
}