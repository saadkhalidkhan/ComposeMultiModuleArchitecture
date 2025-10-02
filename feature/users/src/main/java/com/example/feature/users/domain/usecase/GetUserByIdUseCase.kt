package com.example.feature.users.domain.usecase

import com.example.core.FlowUseCase
import com.example.core.Resource
import com.example.feature.users.domain.model.User
import com.example.feature.users.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a single user by ID
 * Domain layer - contains business logic
 */
class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository
) : FlowUseCase<Int, User>() {
    
    override fun execute(parameters: Int): Flow<Resource<User>> {
        return repository.getUserById(parameters)
    }
}
