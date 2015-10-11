package com.martinandersson.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martinandersson.popularmovies.events.SelectedMovieEvent;
import com.martinandersson.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

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
        } else {
            Log.w(TAG, "No selected movie");
        }

        return rootView;
    }

}
