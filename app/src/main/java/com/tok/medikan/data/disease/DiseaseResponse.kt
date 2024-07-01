package com.tok.medikan.data.disease

import com.google.gson.annotations.SerializedName

data class DiseaseResponse(

    @field:SerializedName("data")
    val diseaseItem: DiseaseItem,

    @field:SerializedName("image_url")
    val imageUrl: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: String
)