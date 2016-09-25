package com.iopho.android.popularmovies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.TMDBMovieClient;
import com.iopho.android.dataAccess.tmdb.TMDBClientFactory;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieGalleryFragment extends Fragment {

    private static final String LOG_TAG = MovieGalleryFragment.class.getSimpleName();

    private static final String MOVIES_BUNDLE_KEY = "MOVIES";
    private static final String QUERY_TYPE_BUNDLE_KEY = "QUERY_TYPE";

    private enum TMDBQueryType {
        POPULARITY,
        RATING;

        public static TMDBQueryType getQueryTypeForName(final String name) {
            if (POPULARITY.name().equals(name)) {
                return POPULARITY;
            }
            return RATING;
        }
    }

    private TMDBClientFactory mTMDBClientFactory;
    private MovieGalleryArrayAdapter mMovieGalleryArrayAdapter;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;
    private TMDBQueryType mTMDBQueryType;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PopularMoviesApplication app =
                (PopularMoviesApplication)getActivity().getApplicationContext();

        mTMDBClientFactory = app.getTMDBClientFactory();

        // Create progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.movie_gallery_loading_dialog_title));
        mProgressDialog.setMessage(getString(R.string.movie_gallery_loading_dialog_description));

        // Create alert dialog
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.movie_gallery_alert_dialog_title))
                .setMessage(getString(R.string.movie_gallery_alert_dialog_message))
                .setPositiveButton(getString(R.string.movie_gallery_alert_dialog_retry_action),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final TMDBQueryType queryTypePref = getQueryTypeForSortOrderPreference();
                                updateMoviesList(queryTypePref);
                            }
                        })
                .setNegativeButton(getString(R.string.movie_gallery_alert_dialog_cancel_action),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Do nothing
                            }
                        });
        mAlertDialog = alertDialogBuilder.create();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Load saved Movies instance and query type state if exists
        List<Movie> movies;
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(MOVIES_BUNDLE_KEY);
            mTMDBQueryType = TMDBQueryType.getQueryTypeForName(
                    savedInstanceState.getString(QUERY_TYPE_BUNDLE_KEY));
        } else {
            movies = new ArrayList<>();
            mTMDBQueryType = getQueryTypeForSortOrderPreference();
        }

        // Create movie array adapter
        mMovieGalleryArrayAdapter = new MovieGalleryArrayAdapter(getActivity(), movies,
                mTMDBClientFactory.getTMDBAssetURLFactory());

        // Inflate fragment UI layout
        final View rootView = inflater.inflate(
                R.layout.fragment_movie_gallery, container, false);

        // Setup the Movie GridView
        final GridView gridView = (GridView)rootView.findViewById(R.id.movie_gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Movie selectedMovie = mMovieGalleryArrayAdapter.getItem(position);
                final Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                detailIntent.putExtra(MovieDetailActivity.MOVIE_INTENT_EXTRA, selectedMovie);
                startActivity(detailIntent);
            }
        });
        gridView.setAdapter(mMovieGalleryArrayAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        final ArrayList<Movie> movies = new ArrayList<>(mMovieGalleryArrayAdapter.getCount());
        for (int i = 0; i < mMovieGalleryArrayAdapter.getCount(); i++) {
            movies.add(mMovieGalleryArrayAdapter.getItem(i));
        }
        savedInstanceState.putParcelableArrayList(MOVIES_BUNDLE_KEY, movies);
        savedInstanceState.putString(QUERY_TYPE_BUNDLE_KEY, mTMDBQueryType.name());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        final TMDBQueryType queryTypePref = getQueryTypeForSortOrderPreference();

        // If the array adapter is empty or the current query type does not match the sort order
        // preference, update movie data.
        if (mMovieGalleryArrayAdapter.isEmpty() || queryTypePref != mTMDBQueryType) {
            updateMoviesList(queryTypePref);
        }
    }

    private void updateMoviesList(final TMDBQueryType tmdbQueryTypeFetchParam) {
        mProgressDialog.show();
        final FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(tmdbQueryTypeFetchParam);
        fetchMoviesTask.execute();
    }

    private TMDBQueryType getQueryTypeForSortOrderPreference() {

        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String sortOrderPref = sharedPreferences.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));

        if (getString(R.string.pref_sort_order_value_rating).equals(sortOrderPref)) {
            return TMDBQueryType.RATING;
        } else {
            return TMDBQueryType.POPULARITY;
        }
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, DataPage<Movie>> {

        private final TMDBQueryType mTMDBQueryTypeFetchParam;

        public FetchMoviesTask(final TMDBQueryType tmdbQueryTypeFetchParam) {

            Preconditions.checkNotNull(tmdbQueryTypeFetchParam, "queryTypes must not be null.");
            this.mTMDBQueryTypeFetchParam = tmdbQueryTypeFetchParam;
        }

        @Override
        protected void onPostExecute(final DataPage<Movie> moviesPage) {
            if (moviesPage != null) {
                mMovieGalleryArrayAdapter.clear();
                for (Movie movie : moviesPage.getResults()) {
                    mMovieGalleryArrayAdapter.add(movie);
                }
                mTMDBQueryType = mTMDBQueryTypeFetchParam;
            } else {
                mAlertDialog.show();
            }

            mProgressDialog.dismiss();
        }

        @Override
        protected DataPage<Movie> doInBackground(final Void... voids) {

            try {
                final TMDBMovieClient tmdbMovieClient = mTMDBClientFactory.getTMDBMovieClient();

                if (mTMDBQueryTypeFetchParam == TMDBQueryType.RATING) {
                    return tmdbMovieClient.getTopRatedMovies(1);
                } else {
                    return tmdbMovieClient.getPopularMovies(1);
                }
            } catch (DataAccessRequestException | DataAccessParsingException ex) {
                Log.e(LOG_TAG, "Failed to request top rated movies from TMDB.", ex);
            }

            return null;
        }
    }
}
