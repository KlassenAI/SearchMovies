package com.android.searchmovies.adapters

import android.content.res.Resources
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.android.searchmovies.R
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class BindingAdapters {

    companion object {

        var width = Resources.getSystem().displayMetrics.widthPixels

        @BindingAdapter("posterPath")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            val imagePath: String =
                if (imageUrl != null) "https://image.tmdb.org/t/p/w500$imageUrl" else ""
            Glide.with(imageView.context)
                .load(imagePath)
                .placeholder(R.color.white)
                .error(R.color.white)
                .into(imageView)
        }

        @BindingAdapter("releaseDate")
        @JvmStatic
        fun formatDate(textView: TextView, releaseDate: String?) {
            if (releaseDate != null && releaseDate.isNotEmpty()) {
                var df = SimpleDateFormat("y-MM-dd", Locale("ru"))
                val date = df.parse(releaseDate)!!
                df = SimpleDateFormat("d MMMM y", Locale("ru"))
                if (width <= 540) df = SimpleDateFormat("d MMM y", Locale("ru"))
                textView.text = df.format(date)
            } else {
                textView.text = "Нет даты"
            }
        }

        @BindingAdapter("overview")
        @JvmStatic
        fun formatOverview(textView: TextView, overview: String?) {
            textView.text =
                if (overview != null && overview.isNotEmpty()) overview else "Описание отсутствует"
        }
    }
}