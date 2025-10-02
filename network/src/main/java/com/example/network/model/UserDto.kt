package com.example.network.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for User from network
 */
data class UserDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String? = null,
    
    @SerializedName("website")
    val website: String? = null
)
