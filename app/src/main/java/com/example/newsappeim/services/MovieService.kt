package com.example.newsappeim.services

import com.example.newsappeim.models.Movie
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MovieService {
    @GET("movielist.json")
    fun getAllMovies(): Call<List<Movie>>

    companion object {
        var movieService: MovieService? = null

        fun getInstance() : MovieService {
            if (movieService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://howtodoandroid.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                movieService = retrofit.create(MovieService::class.java)
            }
            return movieService!!
        }
    }
}