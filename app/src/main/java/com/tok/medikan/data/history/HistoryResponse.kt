package com.tok.medikan.data.history

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

    @field:SerializedName("data")
    val data: List<HistoryItem>,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: String
)