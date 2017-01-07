package com.iopho.android.popularmovies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.iopho.android.dataAccess.tmdb.TMDBClientFactory;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieGalleryFragment extends Fragment {

    private static final String LOG_TAG = MovieGalleryFragment.class.getSimpleName();

    private static final String MOVIES_BUNDLE_KEY = "MOVIES";
    private static final String QUERY_TYPE_BUNDLE_KEY = "QUERY_TYPE";

    private TMDBClientFactory mTMDBClientFactory;
    private MovieGalleryArrayAdapter mMovieGalleryArrayAdapter;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;
    private @FetchMoviesAsyncTask.TMDBQueryType int mTMDBQueryType;

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
        mAlertDialog = createNetworkErrorAlertDialog();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Load saved Movies instance and query type state if exists
        List<Movie> movies;
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(MOVIES_BUNDLE_KEY);
            mTMDBQueryType = FetchMoviesAsyncTask.getTMDBQueryTypeForInt(
                    savedInstanceState.getInt(QUERY_TYPE_BUNDLE_KEY));
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
        savedInstanceState.putInt(QUERY_TYPE_BUNDLE_KEY, mTMDBQueryType);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        final @FetchMoviesAsyncTask.TMDBQueryType int queryTypePref =
                getQueryTypeForSortOrderPreference();

        // If the array adapter is empty or the current query type does not match the sort order
        // preference, update movie data.
        if (mMovieGalleryArrayAdapter.isEmpty() || queryTypePref != mTMDBQueryType) {
            updateMoviesList(queryTypePref);
        }
    }

    private void updateMoviesList(final @FetchMoviesAsyncTask.TMDBQueryType
                                  int tmdbQueryTypeFetchParam) {

        final FetchMoviesAsyncTask fetchMoviesAsyncTask = new FetchMoviesAsyncTask(
                mTMDBClientFactory.getTMDBMovieClient(),
                new FetchMoviesAsyncTask.FetchMoviesAsyncTaskListener() {
                    @Override
                    public void onPreExecute() {
                        mProgressDialog.show();
                    }

                    @Override
                    public void onPostExecute(DataPage<Movie> moviesPage) {
                        if (moviesPage != null) {
                            mMovieGalleryArrayAdapter.clear();
                            for (Movie movie : moviesPage.getResults()) {
                                mMovieGalleryArrayAdapter.add(movie);
                            }
                            mTMDBQueryType = tmdbQueryTypeFetchParam;
                        } else {
                            mAlertDialog.show();
                        }

                        mProgressDialog.dismiss();
                    }
                });
        fetchMoviesAsyncTask.execute(tmdbQueryTypeFetchParam);
    }

    private AlertDialog createNetworkErrorAlertDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.movie_gallery_alert_dialog_title))
                .setMessage(getString(R.string.movie_gallery_alert_dialog_message))
                .setPositiveButton(getString(R.string.movie_gallery_alert_dialog_retry_action),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Re-fetch movie data
                                final @FetchMoviesAsyncTask.TMDBQueryType int queryTypePref =
                                        getQueryTypeForSortOrderPreference();
                                updateMoviesList(queryTypePref);
                            }
                        })
                .setNegativeButton(getString(R.string.movie_gallery_alert_dialog_cancel_action),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Re-direct to home screen
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
        return alertDialogBuilder.create();
    }

    private @FetchMoviesAsyncTask.TMDBQueryType int getQueryTypeForSortOrderPreference() {

        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String sortOrderPref = sharedPreferences.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));

        if (getString(R.string.pref_sort_order_value_rating).equals(sortOrderPref)) {
            return FetchMoviesAsyncTask.RATING;
        } else {
            return FetchMoviesAsyncTask.POPULARITY;
        }
    }
}
