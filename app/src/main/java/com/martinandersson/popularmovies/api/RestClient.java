package com.martinandersson.popularmovies.api;

import com.martinandersson.popularmovies.BuildConfig;

import retrofit.RestAdapter;

public class RestClient {

    public static final String BASE_URL = "http://api.themoviedb.org";
    public static final boolean ENABLE_LOGGING = true;

    private static MoviesApi moviesApi;

    private RestClient() {
        // Hidden constructor
    }

    static {
        // Setup our rest adapter and all APIs
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG && ENABLE_LOGGING ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setEndpoint(BASE_URL)
                .build();
        moviesApi = restAdapter.create(MoviesApi.class);
    }

    public static MoviesApi getMoviesApi() {
        return moviesApi;
    }
}
