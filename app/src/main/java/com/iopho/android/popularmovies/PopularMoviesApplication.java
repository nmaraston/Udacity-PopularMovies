package com.iopho.android.popularmovies;

import android.app.Application;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.tmdb.TMDBClientFactory;
import com.iopho.android.util.ApplicationProperties;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * The PopularMoviesApplication class maintains global application state for the PopularMovies app.
 */
public class PopularMoviesApplication extends Application {

    private static final boolean DEBUG_MODE = true;

    private PopularMoviesAppProperties mApplicationProperties;
    private TMDBClientFactory mTMDBClientFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mApplicationProperties = new PopularMoviesAppProperties(this, R.raw.config);
        } catch (IOException ex) {
            // TODO: What is a better way of handling an unrecoverable event during application initialization?
            throw new RuntimeException(
                    "Failed to construct application properties from resource ID " + R.raw.config,
                    ex);
        }

        final String tmdbAPIKey = mApplicationProperties.getStringPropertyValue(
                PopularMoviesAppProperties.PropertyKey.TMDB_API_KEY);

        mTMDBClientFactory = new TMDBClientFactory(this, tmdbAPIKey);

        try {
            mTMDBClientFactory.init();
        } catch (IOException | DataAccessParsingException ex) {
            // TODO: What is a better way of handling an unrecoverable event during application initialization?
            throw new RuntimeException("Failed to initialize TMDB Client library.", ex);
        }

        // TODO: What is a better way of managing application environments?
        if (DEBUG_MODE) {
            Picasso.with(this).setIndicatorsEnabled(true);
        }
    }

    /**
     * @return the global {@link TMDBClientFactory} instance.
     */
    public TMDBClientFactory getTMDBClientFactory() {
        return mTMDBClientFactory;
    }

    /**
     * An extension of {@link ApplicationProperties} that enforces (via static typing) that only
     * keys of type {@link PropertyKey} can be read.
     */
    private static class PopularMoviesAppProperties extends ApplicationProperties {

        /**
         * All possible property key names for the Popular Movies app.
         */
        public enum PropertyKey {
            TMDB_API_KEY;
        }

        public PopularMoviesAppProperties(final Context context, final int configFileId)
                throws IOException {
            super(context, configFileId);
        }

        public String getStringPropertyValue(final PropertyKey key) {

            Preconditions.checkNotNull(key, "key must not be null.");
            return super.getStringPropertyValue(key.name());
        }
    }
}
