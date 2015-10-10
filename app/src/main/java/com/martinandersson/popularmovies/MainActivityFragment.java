package com.martinandersson.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.martinandersson.popularmovies.api.RestClient;
import com.martinandersson.popularmovies.model.Movie;
import com.martinandersson.popularmovies.model.MoviesResponse;

import java.util.List;

import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivityFragment extends Fragment {
    public static final String TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        getMovies();

        return rootView;
    }

    private void getMovies() {

        RestClient.getMoviesApi().getMoviesByPopularity(new Callback<MoviesResponse>() {
            @Override
            public void success(MoviesResponse moviesResponse, Response response) {
                List<Movie> movieList = moviesResponse.getMovieList();
                Toast.makeText(getActivity(), "getMoviesByPopularity - success", Toast.LENGTH_SHORT).show();

                for (Movie movie : movieList) {
                    Log.d(TAG, movie.getId() + ", " + movie.getOriginalTitle());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "Failed to get movies: " + error.getMessage());
                Toast.makeText(getActivity(), "Failed to get movies", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
