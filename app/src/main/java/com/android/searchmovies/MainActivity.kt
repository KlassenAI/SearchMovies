package com.android.searchmovies

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.searchmovies.adapters.MovieAdapter
import com.android.searchmovies.model.RequestStatus.*
import com.android.searchmovies.viewmodel.MainViewModel
import com.android.searchmovies.viewmodel.MainViewModelFactory
import com.google.android.material.progressindicator.CircularProgressIndicator

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this, MainViewModelFactory(application))
            .get(MainViewModel::class.java)
    }
    private val adapter by lazy {
        MovieAdapter(
            ArrayList(), viewModel, findViewById(R.id.main_view),
            resources.configuration.orientation
        )
    }

    private lateinit var searchEditText: EditText
    private lateinit var linearIndicator: ProgressBar
    private lateinit var circularIndicator: CircularProgressIndicator
    private lateinit var swipeRecycler: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView
    private lateinit var messageRequestEmpty: ConstraintLayout
    private lateinit var messageRequestEmptyText: TextView
    private lateinit var messageRequestFailed: ConstraintLayout
    private lateinit var btnRefresh: ImageButton

    private var linearProgress: Int = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initFields()
    }

    private fun initFields() {
        initViewModel()
        initEditText()
        initIndicators()
        initRecycler()
        initMessages()
    }

    private fun initViewModel() {
        viewModel.searchMovies.observe(this) {
            val animator = ObjectAnimator.ofInt(linearIndicator, "progress", linearProgress / 2)
                .setDuration(linearProgress.toLong())
            animator.doOnStart {
                linearIndicator.progress = 0
                showViews(linearIndicator)
            }
            animator.doOnEnd {
                hideViews(linearIndicator)
            }
            animator.start()
            adapter.setList(it)
            animator.setIntValues(linearProgress)
        }
        viewModel.requestStatus.observe(this) {
            circularIndicator.hide()
            when (it) {
                SUCCESS -> {
                    showViews(swipeRecycler)
                }
                EMPTY -> {
                    val message = resources.getString(R.string.message_request_empty)
                        .format(viewModel.lastQuery.value)
                    if (adapter.itemCount == 0) {
                        messageRequestEmptyText.text = message
                        showViews(messageRequestEmpty)
                    } else {
                        showViews(swipeRecycler)
                        showToast(message)
                    }
                }
                FAILED -> {
                    if (adapter.itemCount == 0) {
                        showViews(messageRequestFailed)
                    } else {
                        showViews(swipeRecycler)
                        showToast(resources.getString(R.string.message_request_failed))
                    }
                }
                else -> {}
            }
        }
    }

    private fun initIndicators() {
        linearIndicator = findViewById(R.id.linear_progress_indicator)
        circularIndicator = findViewById(R.id.circular_progress_indicator)
    }

    private fun initEditText() {
        searchEditText = findViewById(R.id.search_edit_text)
        searchEditText.setText(viewModel.lastQuery.value, TextView.BufferType.EDITABLE)
        searchEditText.setOnEditorActionListener { v, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                val query = v.text.toString()
                if (query != "") {
                    searchMovies(query)
                }
            }
            false
        }
    }

    private fun initRecycler() {
        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
        swipeRecycler = findViewById(R.id.swipe)
        swipeRecycler.setOnRefreshListener {
            swipeRecycler.isRefreshing = false
            searchMovies(viewModel.lastQuery.value!!)
        }
    }

    private fun initMessages() {
        messageRequestEmpty = findViewById(R.id.message_request_empty)
        messageRequestEmptyText = findViewById(R.id.message_request_empty_text)
        messageRequestFailed = findViewById(R.id.message_request_failed)
        btnRefresh = findViewById(R.id.btn_refresh)
        btnRefresh.setOnClickListener {
            searchMovies(viewModel.lastQuery.value!!)
        }
    }

    private fun searchMovies(query: String) {
        circularIndicator.show()
        hideViews(swipeRecycler, messageRequestEmpty, messageRequestFailed, linearIndicator)
        viewModel.searchMovies(query)
    }

    private fun showViews(vararg views: View) {
        views.forEach { it.visibility = VISIBLE }
    }

    private fun hideViews(vararg views: View) {
        views.forEach { it.visibility = INVISIBLE }
    }

    private fun showToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}