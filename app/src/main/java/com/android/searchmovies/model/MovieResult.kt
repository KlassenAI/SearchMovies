package com.android.searchmovies.model

import com.google.gson.annotations.SerializedName

data class MovieResult (
    @SerializedName("results") val movies: List<Movie>?
)