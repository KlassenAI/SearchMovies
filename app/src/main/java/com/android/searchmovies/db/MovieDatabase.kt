package com.android.searchmovies.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.searchmovies.model.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun dao(): MovieDao

    companion object {
        private var instance: MovieDatabase? = null

        fun getInstance(application: Application): MovieDatabase {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val inst = Room.databaseBuilder(application.applicationContext,
                    MovieDatabase::class.java, "movie_database")
                    .build()
                instance = inst
                return instance as MovieDatabase
            }
        }
    }
}