package com.example.feature.users.data.mapper

import com.example.feature.users.domain.model.User
import com.example.network.model.UserDto

/**
 * Mapper to convert between DTO and Domain models
 */
fun UserDto.toUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        website = website
    )
}
