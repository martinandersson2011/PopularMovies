package com.martinandersson.popularmovies;

public final class Constants {

    private Constants() {
    }

    // Replace with your own key. You can get it from https://www.themoviedb.org/
    public static final String THE_MOVIE_DB_API_KEY = "REPLACE_WITH_YOUR_API_KEY";

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";

    public static final int SORT_ORDER_MOST_POPULAR = 0;
    public static final int SORT_ORDER_HIGHEST_RATED = 1;
    public static final int SORT_ORDER_FAVORITES = 2;

}
