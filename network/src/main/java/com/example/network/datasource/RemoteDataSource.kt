package com.example.network.datasource

import com.example.core.NetworkException
import com.example.network.api.ApiService
import com.example.network.model.UserDto
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * Remote data source that handles API calls
 */
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    
    /**
     * Fetch users from API with error handling
     */
    suspend fun getUsers(): List<UserDto> {
        return try {
            apiService.getUsers()
        } catch (e: SocketTimeoutException) {
            throw NetworkException.TimeoutException()
        } catch (e: IOException) {
            throw NetworkException.NoInternetException()
        } catch (e: Exception) {
            throw NetworkException.UnknownException(e.message ?: "Unknown error occurred")
        }
    }
    
    /**
     * Fetch user by ID from API with error handling
     */
    suspend fun getUserById(userId: Int): UserDto {
        return try {
            apiService.getUserById(userId)
        } catch (e: SocketTimeoutException) {
            throw NetworkException.TimeoutException()
        } catch (e: IOException) {
            throw NetworkException.NoInternetException()
        } catch (e: Exception) {
            throw NetworkException.UnknownException(e.message ?: "Unknown error occurred")
        }
    }
}
