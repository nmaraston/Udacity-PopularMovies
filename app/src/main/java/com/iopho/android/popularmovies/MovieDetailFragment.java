package com.iopho.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iopho.android.dataAccess.tmdb.TMDBAssetURLFactory;
import com.iopho.android.dataAccess.tmdb.model.ImageSize;
import com.iopho.android.dataAccess.tmdb.model.Movie;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(
                R.layout.fragment_movie_detail, container, false);

        final Intent intent = getActivity().getIntent();

        if (intent == null || !intent.hasExtra(MovieDetailActivity.MOVIE_INTENT_EXTRA)) {
            return rootView;
        }

        final PopularMoviesApplication app =
                (PopularMoviesApplication) getActivity().getApplicationContext();
        final TMDBAssetURLFactory tmdbAssetURLFactory =
                app.getTMDBClientFactory().getTMDBAssetURLFactory();

        final Movie movie = intent.getParcelableExtra(MovieDetailActivity.MOVIE_INTENT_EXTRA);

        final TextView movieTitleTextView =
                (TextView)rootView.findViewById(R.id.textview_movie_detail_title);
        final TextView movieReleaseDateTextView =
                (TextView)rootView.findViewById(R.id.textview_movie_detail_release_date);
        final TextView movieRatingTextView =
                (TextView)rootView.findViewById(R.id.textview_movie_detail_rating);
        final TextView movieOverviewTextView =
                (TextView)rootView.findViewById(R.id.textview_movie_detail_overview);
        final ImageView moviePosterImageView =
                (ImageView)rootView.findViewById(R.id.imageview_movie_detail_poster);

        movieTitleTextView.setText(movie.getTitle());
        movieReleaseDateTextView.setText(getMovieReleaseDateDisplayString(movie.getReleaseDate()));
        movieRatingTextView.setText(getMovieRatingDisplayString(movie.getAverageVote()));
        movieOverviewTextView.setText(movie.getOverview());

        Picasso.with(getContext())
                .load(tmdbAssetURLFactory.getPosterImageURL(movie.getPosterPath(), ImageSize.W_342))
                .into(moviePosterImageView);

        return rootView;
    }

    private String getMovieRatingDisplayString(final double rating) {

        final String ratingFormatStr = "%.1f/10";

        return String.format(ratingFormatStr, rating);
    }

    private String getMovieReleaseDateDisplayString(final Date releaseDate) {

        final String dateFormatStr = "yyyy";

        final DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        return dateFormat.format(releaseDate);
    }
}
