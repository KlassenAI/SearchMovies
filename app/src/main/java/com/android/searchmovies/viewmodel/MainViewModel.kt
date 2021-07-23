package com.android.searchmovies.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.searchmovies.db.MovieDao
import com.android.searchmovies.db.MovieDatabase
import com.android.searchmovies.model.Movie
import com.android.searchmovies.model.RequestStatus
import com.android.searchmovies.model.RequestStatus.*
import com.android.searchmovies.repository.MovieRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository

    companion object {
        const val API_KEY = "bbc469558822b67257a0bd5a0534463e"
        const val LANGUAGE = "ru"
    }

    init {
        val dao: MovieDao = MovieDatabase.getInstance(application).dao()
        repository = MovieRepository(dao)
    }

    val searchMovies = MutableLiveData<List<Movie>>()
    val requestStatus = MutableLiveData<RequestStatus>()
    val lastQuery = MutableLiveData<String>()

    fun insert(movie: Movie) {
        viewModelScope.launch(IO) {
            repository.insert(movie)
        }
    }

    fun delete(movie: Movie) {
        viewModelScope.launch(IO) {
            repository.delete(movie)
        }
    }

    suspend fun isExist(id: Int): Boolean {
        return repository.isExist(id)
    }

    fun searchMovies(query: String) {
        lastQuery.postValue(query)
        viewModelScope.launch(IO) {
            try {
                val movies = repository.searchMovies(API_KEY, LANGUAGE, query)
                if (movies?.size != 0) {
                    requestStatus.postValue(SUCCESS)
                    launch {
                        searchMovies.postValue(movies!!)
                    }
                } else {
                    requestStatus.postValue(EMPTY)
                }
            } catch (io: IOException) {
                requestStatus.postValue(FAILED)
            }
        }
    }
}
