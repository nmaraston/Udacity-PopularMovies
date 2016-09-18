package com.iopho.android.popularmovies;

import android.app.Application;

import com.iopho.android.dataAccess.tmdb.TMDBClientFactory;

/**
 * The PopularMoviesApplication class maintains global application state for the PopularMovies app.
 */
public class PopularMoviesApplication extends Application {

    private static final String API_KEY = "b70e10dcfb049ab5616c62edb2946e9e";

    private TMDBClientFactory mTMDBClientFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        mTMDBClientFactory = new TMDBClientFactory(API_KEY);
    }

    /**
     * @return the global {@link TMDBClientFactory} instance.
     */
    public TMDBClientFactory getTMDBClientFactory() {
        return mTMDBClientFactory;
    }
}
