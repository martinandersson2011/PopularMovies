package com.martinandersson.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.martinandersson.popularmovies.api.RestClient;
import com.martinandersson.popularmovies.events.SortOrderEvent;
import com.martinandersson.popularmovies.model.Movie;
import com.martinandersson.popularmovies.model.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivityFragment extends Fragment {
    public static final String TAG = MainActivityFragment.class.getSimpleName();

    public static final String KEY_MOVIES_RESPONSE = "com.martinandersson.popularmovies.moviesresponse";

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.no_results)
    TextView mNoResults;

    private GridLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private MoviesResponse mMoviesResponse;
    private List<Movie> mMovies = new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_columns));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoviesAdapter(getActivity(), mMovies);
        mRecyclerView.setAdapter(mAdapter);

        // Check if we have data to display (after rotation)
        if (savedInstanceState != null) {
            mMoviesResponse = (MoviesResponse) savedInstanceState.getSerializable(KEY_MOVIES_RESPONSE);
            if (mMoviesResponse != null) {
                mMovies = mMoviesResponse.getMovieList();
                mAdapter.updateData(mMovies);
            } else {
                getMovies();
            }
        } else {
            getMovies();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(SortOrderEvent event) {
        getMovies();
    }

    private void getMovies() {
        mNoResults.setVisibility(View.GONE);
        Callback<MoviesResponse> moviesResponseCallback = new Callback<MoviesResponse>() {
            @Override
            public void success(MoviesResponse moviesResponse, Response response) {
                mMoviesResponse = moviesResponse;
                mMovies = moviesResponse.getMovieList();
                mAdapter.updateData(mMovies);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "Failed to get movies: " + error.getMessage());
                mNoResults.setVisibility(View.VISIBLE);
            }
        };

        SortOrderEvent event = EventBus.getDefault().getStickyEvent(SortOrderEvent.class);
        if (event == null || event.getSortOrder() == Constants.SORT_ORDER_MOST_POPULAR) {
            // Default to most popular
            RestClient.getMoviesApi().getMoviesByPopularity(moviesResponseCallback);
        } else if (event.getSortOrder() == Constants.SORT_ORDER_HIGHEST_RATED) {
            // Highest rated
            RestClient.getMoviesApi().getMoviesByRating(moviesResponseCallback);
        } else {
            // Favorites from shared preferences
            mMovies = FavoritesManager.getFavoriteMovies(getActivity());
            mMoviesResponse = new MoviesResponse();
            mMoviesResponse.setMovieList(mMovies);
            mAdapter.updateData(mMovies);
            mNoResults.setVisibility(mMovies.size() == 0 ? View.VISIBLE : View.GONE);
            if (mMovies.size() == 0) {
                Toast.makeText(getActivity(), "No favorites found", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_MOVIES_RESPONSE, mMoviesResponse);
    }

}
