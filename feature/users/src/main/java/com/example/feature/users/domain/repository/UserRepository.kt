package com.example.feature.users.domain.repository

import com.example.core.Resource
import com.example.feature.users.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User data operations
 * Part of domain layer - defines contract
 */
interface UserRepository {
    /**
     * Get all users
     */
    fun getUsers(): Flow<Resource<List<User>>>
    
    /**
     * Get user by ID
     */
    fun getUserById(userId: Int): Flow<Resource<User>>
}
