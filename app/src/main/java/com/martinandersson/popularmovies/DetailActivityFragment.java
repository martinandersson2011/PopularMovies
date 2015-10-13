package com.martinandersson.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private List<Review> mReviewList;
    private List<Video> mVideoList;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
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
                    for (Review review : mReviewList) {
                        Log.d(TAG, review.getId() + ", " + review.getContent());
                    }
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
                    for (Video video : mVideoList) {
                        Log.d(TAG, video.getType() + " " + video.getId());
                    }
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

}
