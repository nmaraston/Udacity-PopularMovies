package com.iopho.android.popularmovies;


import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.TMDBMovieClient;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A FetchMoviesAsyncTask is an implementation of {@link AsyncTask} that fetches Movie metadata from
 * TMDB.
 *
 * To allow pre and post execution handling on the UI thread (as {@link AsyncTask} makes possible),
 * a FetchMoviesAsyncTask requires a {@link FetchMoviesAsyncTaskListener}. Callback methods of the
 * FetchMoviesAsyncTaskListener are invoked to handle
 * {@link AsyncTask#onPreExecute()} and {@link AsyncTask#onPostExecute(Object)} callbacks.
 */
public class FetchMoviesAsyncTask extends AsyncTask<Integer, Void, DataPage<Movie>> {

    private static final String LOG_TAG = FetchMoviesAsyncTask.class.getSimpleName();

    /**
     * A {@link TMDBQueryType}. When passed to {@link #doInBackground}, movies are queried with a
     * sort order of rating.
     */
    public static final int RATING = 0;

    /**
     * A {@link TMDBQueryType}. When passed to {@link #doInBackground}, movies are queried with a
     * sort order of popularity.
     */
    public static final int POPULARITY = 1;

    /**
     * A type safe definition for possible TMDB movie query types.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POPULARITY, RATING})
    public @interface TMDBQueryType {}

    /**
     * @param queryType an integer representation of a {@link TMDBQueryType}.
     * @return a {@link TMDBQueryType} given its integer representation. Default to
     * {@link #POPULARITY} if the given integer does not map to a {@link TMDBQueryType}
     */
    public static @TMDBQueryType int getTMDBQueryTypeForInt(final int queryType) {
        return (queryType == RATING) ? RATING : POPULARITY;
    }

    /**
     * A FetchMoviesAsyncTaskListener provides callbacks to handle this AsyncTask's
     * {@link AsyncTask#onPreExecute()} and {@link AsyncTask#onPostExecute(Object)} on UI Thread
     * callbacks.
     *
     * This allows the FetchMoviesAsyncTask to be cleanly implemented without having to be nested
     * within its client class.
     */
    public interface FetchMoviesAsyncTaskListener {

        /**
         * Invoked on the UI thread before {@link #doInBackground(Integer...)} executes. More
         * specifically, called in this AsyncTask's {@link AsyncTask#onPreExecute()} callback.
         */
        void onPreExecute();

        /**
         * Invoked on the UI thread when {@link #doInBackground(Integer...)} is completed execution.
         * More specifically, called in this AsyncTasks {@link AsyncTask#onPostExecute(Object)}}
         * callback.
         * @param moviesPage the result of {@link #doInBackground(Integer...)}
         */
        void onPostExecute(final DataPage<Movie> moviesPage);
    }

    private final TMDBMovieClient tmdbMovieClient;
    private final FetchMoviesAsyncTaskListener listener;

    /**
     * Construct a new FetchMoviesAsyncTask.
     *
     * @param tmdbMovieClient used to fetch movie metadata from TMDB.
     * @param listener a {@link FetchMoviesAsyncTaskListener} to handle pre and post execution
     *                 callbacks.
     */
    public FetchMoviesAsyncTask(final TMDBMovieClient tmdbMovieClient,
                                final FetchMoviesAsyncTaskListener listener) {

        Preconditions.checkNotNull(tmdbMovieClient, "tmdbMovieClient must not be null.");
        Preconditions.checkNotNull(listener, "listener must not be null.");

        this.tmdbMovieClient = tmdbMovieClient;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.onPreExecute();
    }

    @Override
    protected void onPostExecute(final DataPage<Movie> moviesPage) {
        listener.onPostExecute(moviesPage);
    }

    /**
     * See {@link AsyncTask#doInBackground(Object[])}
     *
     * @param queryTypeFetchParams the {@link TMDBQueryType} sort order to query TMDB by. This
     *                             method accepts an array of @TMDBQueryTypes to be compatible with
     *                             parent method {@link AsyncTask#doInBackground(Object[])}. However
     *                             a single {@link TMDBQueryType} is expected. If zero or more than
     *                             one is provided a IllegalArgumentException is thrown.
     * @return the result set from TMDB stored as a {@link DataPage} of {@link Movie}s
     */
    @Override
    protected DataPage<Movie> doInBackground(
            final @TMDBQueryType Integer... queryTypeFetchParams) {

        Preconditions.checkNotNull(queryTypeFetchParams,
                "tmdbQueryTypeFetchParam must not be null.");
        Preconditions.checkArgument(queryTypeFetchParams.length == 1,
                "a single TMDBQueryType param is expected.");

        final @TMDBQueryType int queryType = queryTypeFetchParams[0];

        try {
            if (queryType == RATING) {
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
