package com.martinandersson.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.martinandersson.popularmovies.api.RestClient;
import com.martinandersson.popularmovies.events.SelectedMovieEvent;
import com.martinandersson.popularmovies.model.Movie;
import com.martinandersson.popularmovies.model.Review;
import com.martinandersson.popularmovies.model.ReviewsResponse;
import com.martinandersson.popularmovies.model.Video;
import com.martinandersson.popularmovies.model.VideosResponse;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailFragment extends Fragment {
    public static final String TAG = MovieDetailFragment.class.getSimpleName();

    public static final String KEY_REVIEWS_RESPONSE = "com.martinandersson.popularmovies.reviewsresponse";
    public static final String KEY_VIDEOS_RESPONSE = "com.martinandersson.popularmovies.videosresponse";

    @Bind(R.id.detail_title)
    TextView mDetailTitle;

    @Bind(R.id.detail_image)
    ImageView mDetailImage;

    @Bind(R.id.detail_release_date)
    TextView mDetailReleaseDate;

    @Bind(R.id.detail_vote_average)
    TextView mDetailVoteAverage;

    @Bind(R.id.detail_favorite)
    ImageView mDetailFavorite;

    @Bind(R.id.detail_overview)
    TextView mDetailOverview;

    @Bind(R.id.reviews_layout)
    LinearLayout mReviewsLayout;

    @Bind(R.id.trailers_layout)
    LinearLayout mTrailersLayout;

    private ReviewsResponse mReviewsResponse;
    private VideosResponse mVideosResponse;

    private LayoutInflater mInflater;
    private Movie mMovie;
    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View rootView = mInflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        SelectedMovieEvent event = EventBus.getDefault().getStickyEvent(SelectedMovieEvent.class);
        handleSelectedMovieEvent(event);

        // Check if we have data to display (after rotation)
        if (savedInstanceState != null) {
            // Reviews
            mReviewsResponse = (ReviewsResponse) savedInstanceState.getSerializable(KEY_REVIEWS_RESPONSE);
            if (mReviewsResponse != null) {
                handleReviews();
            } else {
                getReviews();
            }

            // Videos
            mVideosResponse = (VideosResponse) savedInstanceState.getSerializable(KEY_VIDEOS_RESPONSE);
            if (mVideosResponse != null) {
                handleVideos();
            } else {
                handleVideos();
            }
        } else {
            // We have no data to display so get both reviews and videos
            getReviews();
            getVideos();
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

    public void onEvent(SelectedMovieEvent event) {
        // Clear out old reviews and trailers
        mReviewsLayout.removeAllViews();
        mTrailersLayout.removeAllViews();
        mReviewsResponse = null;
        mReviewsResponse = null;

        // Handle selected movie
        handleSelectedMovieEvent(event);
    }

    private void handleSelectedMovieEvent(SelectedMovieEvent event) {
        if (event != null && event.getMovie() != null) {
            mMovie = event.getMovie();
            String title = mMovie.getTitle();
            String releaseDate = mMovie.getReleaseDate();
            String voteAverage = mMovie.getVoteAverage() + " / 10.0";
            String overView = mMovie.getOverview();

            mDetailTitle.setText(title);
            mDetailReleaseDate.setText(releaseDate);
            mDetailVoteAverage.setText(voteAverage);
            mDetailOverview.setText(overView);
            String url = Constants.BASE_IMAGE_URL + mMovie.getPosterPath();
            Picasso.with(getActivity()).load(url).fit().centerCrop().into(mDetailImage);

            boolean favorite = FavoritesManager.isMovieFavorite(getActivity(), mMovie);
            mDetailFavorite.setImageResource(favorite ? R.drawable.ic_star_black_48dp : R.drawable.ic_star_border_black_48dp);

            getReviews();
            getVideos();
        } else {
            Log.w(TAG, "No selected movie");
        }

    }

    private void getReviews() {
        if (mMovie == null) {
            Log.d(TAG, "getReviews ignored since we have no movie");
            return;
        }

        RestClient.getMoviesApi().getReviews(mMovie.getId(), new Callback<ReviewsResponse>() {
            @Override
            public void success(ReviewsResponse reviewsResponse, Response response) {
                mReviewsResponse = reviewsResponse;
                handleReviews();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "Failed to get reviews: " + error.getMessage());
            }
        });
    }

    private void handleReviews() {
        if (mReviewsResponse != null) {
            mReviewsLayout.removeAllViews();
            for (Review review : mReviewsResponse.getReviewList()) {
                View row = mInflater.inflate(R.layout.row_review, mReviewsLayout, false);
                TextView authorTextView = (TextView) row.findViewById(R.id.row_author);
                TextView contentTextView = (TextView) row.findViewById(R.id.row_content);
                authorTextView.setText(review.getAuthor());
                contentTextView.setText(review.getContent());
                mReviewsLayout.addView(row);
            }
        }
    }

    private void getVideos() {
        if (mMovie == null) {
            Log.d(TAG, "getVideos ignored since we have no movie");
            return;
        }

        RestClient.getMoviesApi().getVideos(mMovie.getId(), new Callback<VideosResponse>() {
            @Override
            public void success(VideosResponse videosResponse, Response response) {
                mVideosResponse = videosResponse;
                handleVideos();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "Failed to get videos: " + error.getMessage());
            }
        });
    }

    private void handleVideos() {
        if (mVideosResponse != null) {
            mTrailersLayout.removeAllViews();
            for (final Video video : mVideosResponse.getVideoList()) {
                View row = mInflater.inflate(R.layout.row_trailer, mTrailersLayout, false);
                TextView contentTextView = (TextView) row.findViewById(R.id.row_content);
                contentTextView.setText(video.getName());
                mTrailersLayout.addView(row);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        watchYoutubeVideo(video.getKey());
                    }
                });
            }
        }
    }

    public void watchYoutubeVideo(String videoKey) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoKey));
            startActivity(intent);
        }
    }

    @OnClick(R.id.detail_favorite)
    public void onFavoriteClicked() {
        FavoritesManager.saveFavoriteMovie(getActivity(), mMovie);
        mDetailFavorite.setImageResource(R.drawable.ic_star_black_48dp);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_REVIEWS_RESPONSE, mReviewsResponse);
        outState.putSerializable(KEY_VIDEOS_RESPONSE, mVideosResponse);
    }
}
