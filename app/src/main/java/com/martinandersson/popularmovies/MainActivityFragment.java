package com.martinandersson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.martinandersson.popularmovies.api.RestClient;
import com.martinandersson.popularmovies.events.SelectedMovieEvent;
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

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private GridLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private List<Movie> mMovies = new ArrayList<Movie>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoviesAdapter(getActivity(), mMovies);
        mRecyclerView.setAdapter(mAdapter);

        getMovies();

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
        Callback<MoviesResponse> moviesResponseCallback = new Callback<MoviesResponse>() {
            @Override
            public void success(MoviesResponse moviesResponse, Response response) {
                mMovies = moviesResponse.getMovieList();
                mAdapter.updateData(mMovies);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "Failed to get movies: " + error.getMessage());
                Toast.makeText(getActivity(), "Failed to get movies", Toast.LENGTH_SHORT).show();
            }
        };

        SortOrderEvent event = EventBus.getDefault().getStickyEvent(SortOrderEvent.class);
        if (event == null || event.getSortOrder() == Constants.SORT_ORDER_MOST_POPULAR) {
            RestClient.getMoviesApi().getMoviesByPopularity(moviesResponseCallback);
        } else {
            RestClient.getMoviesApi().getMoviesByRating(moviesResponseCallback);
        }

    }
}
