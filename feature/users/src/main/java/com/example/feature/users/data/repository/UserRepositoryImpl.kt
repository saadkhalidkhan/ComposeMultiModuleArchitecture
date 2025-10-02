package com.example.feature.users.data.repository

import com.example.core.Resource
import com.example.feature.users.data.mapper.toUser
import com.example.feature.users.domain.model.User
import com.example.feature.users.domain.repository.UserRepository
import com.example.network.datasource.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of UserRepository
 * Data layer - handles data operations
 */
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : UserRepository {
    
    override fun getUsers(): Flow<Resource<List<User>>> = flow {
        try {
            emit(Resource.Loading())
            
            val usersDto = remoteDataSource.getUsers()
            val users = usersDto.map { it.toUser() }
            
            emit(Resource.Success(users))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
    
    override fun getUserById(userId: Int): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            
            val userDto = remoteDataSource.getUserById(userId)
            val user = userDto.toUser()
            
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}
