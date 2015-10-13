package com.martinandersson.popularmovies.api;

import com.martinandersson.popularmovies.Constants;
import com.martinandersson.popularmovies.model.MoviesResponse;
import com.martinandersson.popularmovies.model.ReviewsResponse;
import com.martinandersson.popularmovies.model.VideosResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MoviesApi {

    @GET("/3/discover/movie?sort_by=popularity.desc&api_key=" + Constants.THE_MOVIE_DB_API_KEY)
    void getMoviesByPopularity(Callback<MoviesResponse> moviesResponseCallback);

    @GET("/3/discover/movie?sort_by=vote_average.desc&api_key=" + Constants.THE_MOVIE_DB_API_KEY)
    void getMoviesByRating(Callback<MoviesResponse> moviesResponseCallback);

    @GET("/3/movie/{userId}/reviews?api_key=" + Constants.THE_MOVIE_DB_API_KEY)
    void getReviews(@Path("userId") int userId, Callback<ReviewsResponse> reviewsResponseCallback);

    @GET("/3/movie/{userId}/videos?api_key=" + Constants.THE_MOVIE_DB_API_KEY)
    void getVideos(@Path("userId") int userId, Callback<VideosResponse> videosResponseCallback);

}
