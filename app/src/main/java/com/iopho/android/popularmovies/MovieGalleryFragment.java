package com.iopho.android.popularmovies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.TMDBMovieClient;
import com.iopho.android.dataAccess.tmdb.TMDBClientFactory;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;

import java.util.ArrayList;

public class MovieGalleryFragment extends Fragment {

    private static final String LOG_TAG = MovieGalleryFragment.class.getSimpleName();

    private TMDBClientFactory mTMDBClientFactory;
    private MovieGalleryArrayAdapter mMovieGalleryArrayAdapter;
    private GridView mMovieGridView;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

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
                R.layout.fragment_movie_gallery, container, false);
        mMovieGridView = (GridView)rootView.findViewById(R.id.movie_gridview);
        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Movie selectedMovie = mMovieGalleryArrayAdapter.getItem(position);
                final Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                detailIntent.putExtra(MovieDetailActivity.MOVIE_INTENT_EXTRA, selectedMovie);
                startActivity(detailIntent);
            }
        });

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
                // Initialize the TMDB client factory on first request
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
