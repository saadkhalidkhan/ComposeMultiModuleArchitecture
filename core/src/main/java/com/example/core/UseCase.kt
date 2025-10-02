package com.example.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

/**
 * Base UseCase class that handles common logic for use cases
 * @param P: Parameter type for the use case
 * @param R: Return type for the use case
 */
abstract class UseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Execute the use case
     */
    suspend operator fun invoke(parameters: P): R {
        return execute(parameters)
    }
    
    /**
     * Override this to implement the actual logic
     */
    protected abstract suspend fun execute(parameters: P): R
}

/**
 * Base FlowUseCase for use cases that return Flow with Resource wrapper
 */
abstract class FlowUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Execute the use case returning a Flow
     */
    operator fun invoke(parameters: P): Flow<Resource<R>> {
        return execute(parameters)
            .catch { e ->
                emit(Resource.Error(e.message ?: "An unexpected error occurred"))
            }
            .flowOn(coroutineDispatcher)
    }
    
    /**
     * Override this to implement the actual logic
     */
    protected abstract fun execute(parameters: P): Flow<Resource<R>>
}
