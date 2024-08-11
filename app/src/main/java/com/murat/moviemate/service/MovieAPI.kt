package com.murat.moviemate.service

import com.murat.moviemate.model.MovieData
import com.murat.moviemate.model.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieAPI {

    @GET("movie/{id}?api_key=")
    fun getMovieDetails(@Path("id") id: String): Call<MovieData>

    @GET("movie/now_playing?api_key=")
    fun getNowPlayingMovies(): Call<MovieResponse>

    @GET("movie/upcoming?api_key=")
    fun getUpcomingMovies(): Call<MovieResponse>
}