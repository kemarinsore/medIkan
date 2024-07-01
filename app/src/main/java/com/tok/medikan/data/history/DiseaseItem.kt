package com.tok.medikan.data.history

import com.google.gson.annotations.SerializedName

data class DiseaseItem(

    @field:SerializedName("treatment")
    val treatment: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String
)