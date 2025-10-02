package com.example.core

/**
 * A generic wrapper class to handle different states of data loading
 * Supports: Success, Error, and Loading states
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Success state with data
     */
    class Success<T>(data: T) : Resource<T>(data)
    
    /**
     * Error state with optional message and data
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    
    /**
     * Loading state with optional cached data
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

/**
 * Extension function to check if resource is loading
 */
fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading

/**
 * Extension function to check if resource is success
 */
fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success

/**
 * Extension function to check if resource is error
 */
fun <T> Resource<T>.isError(): Boolean = this is Resource.Error
