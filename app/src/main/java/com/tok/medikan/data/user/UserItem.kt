package com.tok.medikan.data.user

import com.google.gson.annotations.SerializedName

data class UserItem(

    @field:SerializedName("profilePicture")
    val profilePicture: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("username")
    val username: String
)