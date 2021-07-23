package com.android.searchmovies.db

import androidx.room.*
import com.android.searchmovies.model.Movie
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)

    @Query("SELECT EXISTS(SELECT * FROM movie_table WHERE id = :id)")
    suspend fun isExist(id: Int): Boolean
}