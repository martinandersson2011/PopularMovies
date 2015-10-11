package com.martinandersson.popularmovies.api;

import com.martinandersson.popularmovies.Constants;
import com.martinandersson.popularmovies.model.MoviesResponse;

import retrofit.Callback;
import retrofit.http.GET;

public interface MoviesApi {

    @GET("/3/discover/movie?sort_by=popularity.desc&api_key=" + Constants.THE_MOVIE_DB_API_KEY)
    void getMoviesByPopularity(Callback<MoviesResponse> moviesResponseCallback);

    @GET("/3/discover/movie?sort_by=vote_average.desc&api_key=" + Constants.THE_MOVIE_DB_API_KEY)
    void getMoviesByRating(Callback<MoviesResponse> moviesResponseCallback);

}
