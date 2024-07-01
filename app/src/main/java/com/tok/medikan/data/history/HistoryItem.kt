package com.tok.medikan.data.history

import com.google.gson.annotations.SerializedName

data class HistoryItem(

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("disease")
    val diseaseItem: DiseaseItem,

    @field:SerializedName("historyName")
    val historyName: String,

    @field:SerializedName("id")
    val id: Int
)