package com.example.newsappeim.repositories

import com.example.newsappeim.services.MovieService

class MovieRepository constructor(private val movieService: MovieService) {
    fun getAllMovies() = movieService.getAllMovies()
}