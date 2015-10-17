package com.martinandersson.popularmovies;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.martinandersson.popularmovies.model.Movie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 10/16/15.
 */
public class FavoritesManager {

    public static final String TAG = FavoritesManager.class.getSimpleName();

    private static final String MOVIES_PREFERENCES = "com.martinandersson.popularmovies.preferences";
    private static final String KEY_FAVORITES = "com.martinandersson.popularmovies.favorites";

    public static void saveFavoriteMovie(Context context, Movie movie) {
        if (isMovieFavorite(context, movie)) {
            Toast.makeText(context, "Movie is already a favorite", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Movie added to favorites", Toast.LENGTH_SHORT).show();
            List<Movie> favoriteMovies = getFavoriteMovies(context);
            favoriteMovies.add(movie);
            String json = new Gson().toJson(favoriteMovies);
            context.getSharedPreferences(MOVIES_PREFERENCES, Context.MODE_PRIVATE).edit().putString(KEY_FAVORITES, json).apply();
        }
    }

    public static List<Movie> getFavoriteMovies(Context context) {
        // Get favorite movies from shared preferences
        String json = context.getSharedPreferences(MOVIES_PREFERENCES, Context.MODE_PRIVATE).getString(KEY_FAVORITES, null);
        Type type = new TypeToken<List<Movie>>() {
        }.getType();
        List<Movie> favoriteMovies = new Gson().fromJson(json, type);

        if (favoriteMovies == null) {
            favoriteMovies = new ArrayList<>();
        }
        return favoriteMovies;
    }

    public static boolean isMovieFavorite(Context context, Movie movie) {
        List<Movie> favoriteMovies = getFavoriteMovies(context);
        for (Movie favoriteMovie : favoriteMovies) {
            if (favoriteMovie.getId() == movie.getId()) {
                return true;
            }
        }
        return false;
    }

}
