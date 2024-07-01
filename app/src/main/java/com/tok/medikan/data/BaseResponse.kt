package com.tok.medikan.data

import com.google.gson.annotations.SerializedName

data class BaseResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("token")
    val token: String

)