package com.tok.medikan.data.disease

import com.google.gson.annotations.SerializedName

data class DiseaseItem(

    @field:SerializedName("reference")
    val reference: String,

    @field:SerializedName("treatment")
    val treatment: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("id")
    val id: Int
)