package com.iopho.android.dataAccess.tmdb;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.model.Configuration;

/**
 * A TMDBConfigurationCacheManager retrieves and caches TMDB configuration.
 *
 * TMDB documentation recommends refreshing any cached config "every few days". With the assumption
 * that the application will not be running for multiple days we therefore do not invalidate/refresh
 * the cache at runtime.
 *
 * Invoking the {@link #init()} method is required to initialize the config cache before reading it.
 * This has the potential to trigger a network call and should thus not be invoked via the UI
 * thread. {@link #getTMDBConfiguration()} will throw a IllegalStateException if the cache has not initialized.
 *
 * NOTE: Currently the cache management solution is simple; on initialization the configuration is
 * downloaded from TMDB and we store a local reference. Cache management could be improved by
 * persisting an instance of the configuration to a local data-store and only requesting updated
 * config if the local copy is deemed out of date (some number of days old).
 */
class TMDBConfigurationCacheManager {

    private final TMDBConfigurationClient mTMDBConfigurationClient;

    private Configuration mConfiguration;

    /**
     * Construct a new TMDBConfigurationCacheManager.
     *
     * @param tmdbConfigurationClient to retrieve TMDB configuration values.
     */
    public TMDBConfigurationCacheManager(final TMDBConfigurationClient tmdbConfigurationClient) {

        Preconditions.checkNotNull(tmdbConfigurationClient,
                "tmdbConfigurationClient must not be null.");
        mTMDBConfigurationClient = tmdbConfigurationClient;
    }

    /**
     * Initialize the configuration cache. Must be called before calls to
     * {@link #getTMDBConfiguration()} to retrieve the cache.
     *
     * @throws DataAccessRequestException when failing to retrieve configuration values from the
     * TMDB web service.
     * @throws DataAccessParsingException when failing to parse the TMDB web service response that
     * is expected to contain service configuration.
     */
    public void init() throws DataAccessRequestException, DataAccessParsingException {
        mConfiguration = mTMDBConfigurationClient.getConfiguration();
    }

    /**
     * Get instance of {@link Configuration} containing TMDB config values. Must call
     * {@link #init()} before attempting to retrieve configuration or an IllegalStateException is
     * thrown.
     *
     * @return an instance of {@link Configuration} containing TMDB config values.
     */
    public Configuration getTMDBConfiguration() {
        if (mConfiguration == null) {
            throw new IllegalStateException("Configuration cache has not been initialized." +
                    " #init() must be called first to initialized the configuration cache.");
        }
        return mConfiguration;
    }
}
