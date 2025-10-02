package com.example.network.api

import com.example.network.model.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API Service interface defining all network endpoints
 */
interface ApiService {
    
    @GET("users")
    suspend fun getUsers(): List<UserDto>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: Int): UserDto
}
