package com.example.feature.users.domain.model

/**
 * Domain model for User
 * Clean domain entity independent of data layer
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val website: String?
)
