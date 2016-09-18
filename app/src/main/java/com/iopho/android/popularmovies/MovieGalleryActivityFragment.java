package com.iopho.android.popularmovies;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.iopho.android.dataAccess.tmdb.TMDBMovieClient;
import com.iopho.android.dataAccess.tmdb.TMDBClientFactory;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGalleryActivityFragment extends Fragment {

    private static final String LOG_TAG = MovieGalleryActivityFragment.class.getSimpleName();

    private static final String API_KEY = "b70e10dcfb049ab5616c62edb2946e9e";

    private TMDBClientFactory mTMDBClientFactory;
    private MovieGalleryArrayAdapter mMovieGalleryArrayAdapter;
    private GridView mMovieGridView;
    private ProgressDialog mProgressDialog;

    public MovieGalleryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTMDBClientFactory = new TMDBClientFactory(API_KEY);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Loading movies...");
        mProgressDialog.show();

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
        final FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, DataPage<Movie>> {

        @Override
        protected void onPostExecute(final DataPage<Movie> moviesPage) {
            if (mMovieGalleryArrayAdapter == null) {
                mMovieGalleryArrayAdapter = new MovieGalleryArrayAdapter(getActivity(),
                        new ArrayList<Movie>(), mTMDBClientFactory.getTMDBAssetURLFactory());
                mMovieGridView.setAdapter(mMovieGalleryArrayAdapter);
            }
            if (moviesPage != null) {
                mMovieGalleryArrayAdapter.clear();
                for (Movie movie : moviesPage.getResults()) {
                    mMovieGalleryArrayAdapter.add(movie);
                }
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
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error", ex);
            }

            return null;
        }
    }
}
