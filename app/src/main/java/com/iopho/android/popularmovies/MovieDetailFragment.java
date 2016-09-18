package com.iopho.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iopho.android.dataAccess.tmdb.model.Movie;

/**
 *
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private Movie mMovie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Intent intent = getActivity().getIntent();

        if (intent == null || intent.hasExtra(MovieDetailActivity.MOVIE_INTENT_EXTRA)) {
            mMovie = intent.getParcelableExtra(MovieDetailActivity.MOVIE_INTENT_EXTRA);
        } else {
            // TODO: handle this case
        }

        final View rootView = inflater.inflate(
                R.layout.fragment_movie_detail, container, false);

        final TextView movieTitleTextView =
                (TextView)rootView.findViewById(R.id.movie_detail_title_text_view);
        movieTitleTextView.setText(mMovie.getTitle());

        return rootView;
    }
}
