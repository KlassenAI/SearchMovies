package com.android.searchmovies.adapters

import android.content.res.Configuration
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.searchmovies.R
import com.android.searchmovies.adapters.MovieAdapter.MovieViewHolder
import com.android.searchmovies.databinding.CardItemBinding
import com.android.searchmovies.model.Movie
import com.android.searchmovies.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MovieAdapter(
    private var movies: List<Movie>,
    private var viewModel: MainViewModel,
    private var view: View,
    private var orientation: Int
) : RecyclerView.Adapter<MovieViewHolder>() {

    companion object {
        const val messageLoadingError = "Проверьте соединение с интернетом и попробуйте еще раз"
        const val messageAddMovie = "%s был добавлен в избранные"
        const val messageDeleteMovie = "%s был удален из избранных"
        var width = Resources.getSystem().displayMetrics.widthPixels
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemBinding: CardItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_item,
                parent,
                false
            )
        return MovieViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.movie = movie
        holder.binding.cardDescription.maxLines =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (width <= 540) {
                    if (movie.title.length > 13) 4 else 5
                } else if (width <= 720) {
                    if (movie.title.length > 17) 4 else 5
                } else {
                    if (movie.title.length > 21) 4 else 5
                }
            } else 5
        CoroutineScope(IO).launch {
            val favorite = viewModel.isExist(movie.id)
            withContext(Main) {
                holder.binding.cardLike.setImageResource(
                    if (favorite) R.drawable.ic_heart_fill
                    else R.drawable.ic_heart
                )
            }
        }
        holder.binding.cardLike.setOnClickListener {
            CoroutineScope(IO).launch {
                val favorite = viewModel.isExist(movie.id)
                withContext(Main) {
                    if (favorite) {
                        viewModel.delete(movie)
                        showToast(messageDeleteMovie.format(movie.title))
                    } else {
                        viewModel.insert(movie)
                        showToast(messageAddMovie.format(movie.title))
                    }
                    notifyItemChanged(position)
                }
            }
        }
        holder.binding.card.setOnClickListener {
            showToast(movie.title)
        }
        if (hasEmptyElement(movie.title, movie.overview, movie.posterPath, movie.releaseDate)) {
            Snackbar.make(view, messageLoadingError, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class MovieViewHolder(val binding: CardItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun hasEmptyElement(vararg strings: String): Boolean {
        strings.forEach { if (it.isEmpty()) return true }
        return false
    }

    private fun showToast(text: String) {
        Toast.makeText(view.context, text, LENGTH_SHORT).show()
    }

    fun setList(list: List<Movie>) {
        movies = list
        notifyDataSetChanged()
    }
}
