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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailActivityFragment extends Fragment {
    public static final String TAG = DetailActivityFragment.class.getSimpleName();

    @Bind(R.id.detail_title)
    TextView mDetailTitle;

    @Bind(R.id.detail_image)
    ImageView mDetailImage;

    @Bind(R.id.detail_release_date)
    TextView mDetailReleaseDate;

    @Bind(R.id.detail_vote_average)
    TextView mDetailVoteAverage;

    @Bind(R.id.detail_overview)
    TextView mDetailOverview;

    @Bind(R.id.reviews_layout)
    LinearLayout mReviewsLayout;

    @Bind(R.id.trailers_layout)
    LinearLayout mTrailersLayout;

    private List<Review> mReviewList;
    private List<Video> mVideoList;

    private LayoutInflater mInflater;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View rootView = mInflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        SelectedMovieEvent event = EventBus.getDefault().getStickyEvent(SelectedMovieEvent.class);
        Movie movie = event.getMovie();

        if (movie != null) {
            String title = movie.getTitle();
            String releaseDate = movie.getReleaseDate();
            String voteAverage = movie.getVoteAverage() + " / 10.0";
            String overView = movie.getOverview();

            mDetailTitle.setText(title);
            mDetailReleaseDate.setText(releaseDate);
            mDetailVoteAverage.setText(voteAverage);
            mDetailOverview.setText(overView);
            String url = Constants.BASE_IMAGE_URL + movie.getPosterPath();
            Picasso.with(getActivity()).load(url).fit().centerCrop().into(mDetailImage);

            RestClient.getMoviesApi().getReviews(movie.getId(), new Callback<ReviewsResponse>() {
                @Override
                public void success(ReviewsResponse reviewsResponse, Response response) {
                    mReviewList = reviewsResponse.getReviewList();
                    handleReviews();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.w(TAG, "Failed to get reviews: " + error.getMessage());
                    Toast.makeText(getActivity(), "Failed to get reviews", Toast.LENGTH_SHORT).show();
                }
            });

            RestClient.getMoviesApi().getVideos(movie.getId(), new Callback<VideosResponse>() {
                @Override
                public void success(VideosResponse videosResponse, Response response) {
                    mVideoList = videosResponse.getVideoList();
                    handleVideos();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.w(TAG, "Failed to get videos: " + error.getMessage());
                    Toast.makeText(getActivity(), "Failed to get videos", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Log.w(TAG, "No selected movie");
        }

        return rootView;
    }

    private void handleReviews() {
        mReviewsLayout.removeAllViews();
        for (Review review : mReviewList) {
            View row = mInflater.inflate(R.layout.row_review, mReviewsLayout, false);
            TextView authorTextView = (TextView) row.findViewById(R.id.row_author);
            TextView contentTextView = (TextView) row.findViewById(R.id.row_content);
            authorTextView.setText(review.getAuthor());
            contentTextView.setText(review.getContent());
            mReviewsLayout.addView(row);
        }
    }

    private void handleVideos() {
        mTrailersLayout.removeAllViews();
        for (final Video video : mVideoList) {
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

    public void watchYoutubeVideo(String videoKey) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoKey));
            startActivity(intent);
        }
    }
}
