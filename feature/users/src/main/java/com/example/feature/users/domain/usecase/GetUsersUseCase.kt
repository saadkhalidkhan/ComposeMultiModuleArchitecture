package com.example.feature.users.domain.usecase

import com.example.core.FlowUseCase
import com.example.core.Resource
import com.example.feature.users.domain.model.User
import com.example.feature.users.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting list of users
 * Domain layer - contains business logic
 */
class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository
) : FlowUseCase<Unit, List<User>>() {
    
    override fun execute(parameters: Unit): Flow<Resource<List<User>>> {
        return repository.getUsers()
    }
}
