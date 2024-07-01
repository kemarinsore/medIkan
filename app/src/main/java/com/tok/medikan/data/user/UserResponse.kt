package com.tok.medikan.data.user

import com.google.gson.annotations.SerializedName

data class UserResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("user")
    val userItem: UserItem,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("token")
    val token: String
)