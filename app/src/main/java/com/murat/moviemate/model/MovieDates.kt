package com.murat.moviemate.model

import com.google.gson.annotations.SerializedName

data class MovieDates (
    @SerializedName("maximum") val maximum: String,
    @SerializedName("minimum") val minimum: String
)