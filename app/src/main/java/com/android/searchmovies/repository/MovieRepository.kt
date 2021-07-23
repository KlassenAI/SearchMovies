package com.android.searchmovies.repository

import com.android.searchmovies.service.MovieApiService
import com.android.searchmovies.service.RetrofitInstance.service
import com.android.searchmovies.db.MovieDao
import com.android.searchmovies.model.Movie
import com.android.searchmovies.model.MovieResult
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Response

class MovieRepository(private val dao: MovieDao) {

    private val apiService: MovieApiService = service

    suspend fun insert(movie: Movie) {
        dao.insert(movie)
    }

    suspend fun delete(movie: Movie) {
        dao.delete(movie)
    }

    suspend fun isExist(id: Int): Boolean {
        return dao.isExist(id)
    }

    suspend fun searchMovies(apiKey: String, language: String, query: String): List<Movie>? {
        val response = apiService.searchMovies(apiKey, language, query)
        val result = response.body()
        return result?.movies
    }
}