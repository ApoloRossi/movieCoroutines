/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.kotlin.coroutines.ui.movies

import android.util.Log
import com.raywenderlich.kotlin.coroutines.domain.repository.MovieRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Handles the business logic calls, reacting to UI events.
 */
class MoviesPresenterImpl(private val movieRepository: MovieRepository) : MoviesPresenter, CoroutineScope {

    private lateinit var moviesView: MoviesView

    private var parentJob = Job()

    override fun setView(moviesView: MoviesView) {
        this.moviesView = moviesView
    }

    override fun getData() {
        launch {
            delay(500)
            val result = movieRepository.getMovies()

            Log.d("TestCoroutine", "Still Alive")
            if (result.value != null && result.value.isNotEmpty()) {
                moviesView.showMovies(result.value)
            } else if (result.throwable!=null) {
                handleError(result.throwable)
            }
        }
    }

    override fun start() {
        if (!parentJob.isActive) {
            parentJob = Job()
        }
    }

    override fun stop() {
        parentJob.cancel()
    }

    private fun handleError(throwable: Throwable) {
        moviesView.showError(throwable)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob
}