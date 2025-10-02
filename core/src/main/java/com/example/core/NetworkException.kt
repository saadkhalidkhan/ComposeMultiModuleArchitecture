package com.example.core

/**
 * Custom exceptions for network operations
 */
sealed class NetworkException(message: String) : Exception(message) {
    class NoInternetException : NetworkException("No internet connection")
    class ServerException(message: String) : NetworkException(message)
    class TimeoutException : NetworkException("Request timeout")
    class UnknownException(message: String) : NetworkException(message)
}
