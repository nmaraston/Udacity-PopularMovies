package com.iopho.android.popularmovies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.TMDBMovieClient;
import com.iopho.android.dataAccess.tmdb.TMDBClientFactory;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;

import java.util.ArrayList;

public class MovieGalleryActivityFragment extends Fragment {

    private static final String LOG_TAG = MovieGalleryActivityFragment.class.getSimpleName();

    private static final String API_KEY = "b70e10dcfb049ab5616c62edb2946e9e";

    private TMDBClientFactory mTMDBClientFactory;
    private MovieGalleryArrayAdapter mMovieGalleryArrayAdapter;
    private GridView mMovieGridView;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    public MovieGalleryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTMDBClientFactory = new TMDBClientFactory(API_KEY);

        // Create progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.movie_gallery_loading_dialog_title));
        mProgressDialog.setMessage(getString(R.string.movie_gallery_loading_dialog_description));

        // Create alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.movie_gallery_alert_dialog_title))
                .setMessage(getString(R.string.movie_gallery_alert_dialog_message))
                .setPositiveButton(getString(R.string.movie_gallery_alert_dialog_retry_action),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateMoviesList();
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

        // Inflate fragment UI layout
        final View rootView = inflater.inflate(
                R.layout.movie_gallery_activity_fragment, container, false);
        mMovieGridView = (GridView)rootView.findViewById(R.id.movie_gridview);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesList();
    }

    private void updateMoviesList() {
        mProgressDialog.show();
        final FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, DataPage<Movie>> {

        @Override
        protected void onPostExecute(final DataPage<Movie> moviesPage) {
            if (moviesPage != null) {
                if (mMovieGalleryArrayAdapter == null) {
                    mMovieGalleryArrayAdapter = new MovieGalleryArrayAdapter(getActivity(),
                            new ArrayList<Movie>(), mTMDBClientFactory.getTMDBAssetURLFactory());
                    mMovieGridView.setAdapter(mMovieGalleryArrayAdapter);
                }
                mMovieGalleryArrayAdapter.clear();
                for (Movie movie : moviesPage.getResults()) {
                    mMovieGalleryArrayAdapter.add(movie);
                }
            } else {
                mAlertDialog.show();
            }

            mProgressDialog.hide();
        }

        @Override
        protected DataPage<Movie> doInBackground(final Void... voids) {

            try {
                if (!mTMDBClientFactory.isInitialized()) {
                    mTMDBClientFactory.init();
                }
                TMDBMovieClient tmdbMovieClient = mTMDBClientFactory.getTMDBMovieClient();
                return tmdbMovieClient.getTopRatedMovies(1);
            } catch (DataAccessRequestException | DataAccessParsingException ex) {
                Log.e(LOG_TAG, "Failed to request top rated movies from TMDB.", ex);
            }

            return null;
        }
    }
}
