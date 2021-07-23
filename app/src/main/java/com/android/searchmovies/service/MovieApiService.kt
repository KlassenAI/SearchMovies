package com.android.searchmovies.service

import retrofit2.http.GET
import retrofit2.http.Query
import com.android.searchmovies.model.MovieResult
import retrofit2.Response

interface MovieApiService {

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String
    ): Response<MovieResult?>
}