package com.iopho.android.popularmovies;

import android.app.Application;
import android.content.Context;
import android.support.annotation.StringDef;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.tmdb.TMDBClientFactory;
import com.iopho.android.util.ApplicationProperties;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
            mApplicationProperties = new PopularMoviesAppProperties(this);
        } catch (IOException ex) {
            // TODO: What is a better way of handling an unrecoverable event during application initialization?
            throw new RuntimeException(
                    "Failed to construct application properties.", ex);
        }

        final String tmdbAPIKey = mApplicationProperties.getStringPropertyValue(
                PopularMoviesAppProperties.TMDB_API_KEY);

        mTMDBClientFactory = new TMDBClientFactory(this, tmdbAPIKey);

        try {
            mTMDBClientFactory.init();
        } catch (IOException | DataAccessParsingException ex) {
            // TODO: What is a better way of handling an unrecoverable event during application initialization?
            throw new RuntimeException("Failed to initialize TMDB client library.", ex);
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
     * keys of annotated type @AppProperty can be read.
     */
    private static class PopularMoviesAppProperties {

        /**
         * All possible property key names for the Popular Movies app.
         */
        public static final String TMDB_API_KEY = "TMDB_API_KEY";

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({TMDB_API_KEY})
        public @interface AppProperty {}

        private final ApplicationProperties mApplicationProperties;

        public PopularMoviesAppProperties(final Context context) throws IOException {
            mApplicationProperties = new ApplicationProperties(context, R.raw.app_config);
        }

        public String getStringPropertyValue(final @AppProperty String key) {

            Preconditions.checkNotNull(key, "key must not be null.");
            return mApplicationProperties.getStringPropertyValue(key);
        }
    }
}
