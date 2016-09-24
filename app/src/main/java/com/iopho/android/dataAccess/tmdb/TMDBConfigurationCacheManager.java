package com.iopho.android.dataAccess.tmdb;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.tmdb.json.JSONConfigurationTransformer;
import com.iopho.android.dataAccess.tmdb.model.Configuration;
import com.iopho.android.popularmovies.R;
import com.iopho.android.util.IOHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * A TMDBConfigurationCacheManager retrieves/caches TMDB configuration and makes it accessible.
 *
 * TMDB documentation recommends refreshing any cached config "every few days". With the assumption
 * that the application will not be running for multiple days we therefore do not invalidate/refresh
 * the cache at runtime. The cache is only invalidated on {@link #init()}.
 *
 * Invoking the {@link #init()} method is required to initialize the config cache before reading it.
 * {@link #getTMDBConfiguration()} will throw a IllegalStateException if the cache has not
 * initialized.
 *
 * NOTE: Currently the cache management solution is simple; on initialization the configuration is
 * read from a local file. Cache management will be improved by making non-blocking calls to the
 * TMDB API to get an up to date configuration payload and cache it.
 */
class TMDBConfigurationCacheManager {

    private static final String LOG_TAG = TMDBConfigurationCacheManager.class.getSimpleName();

    private static final String CONFIGURATION_CACHE_FILE_NAME = "tmdb_remote_config";

    private final TMDBConfigurationClient mTMDBConfigurationClient;

    private final Context mContext;
    private Configuration mConfiguration;
    private boolean mIsInitialized;

    /**
     * Construct a new TMDBConfigurationCacheManager.
     *
     * @param context the current {@link Context}
     * @param tmdbConfigurationClient to retrieve TMDB configuration values.
     */
    public TMDBConfigurationCacheManager(final Context context,
                                         final TMDBConfigurationClient tmdbConfigurationClient) {

        Preconditions.checkNotNull(context, "context must not be null.");
        Preconditions.checkNotNull(tmdbConfigurationClient,
                "tmdbConfigurationClient must not be null.");

        this.mContext = context;
        this.mTMDBConfigurationClient = tmdbConfigurationClient;
        this.mIsInitialized = false;
    }

    /**
     * Initialize the configuration cache. Must be called before calls to
     * {@link #getTMDBConfiguration()} to retrieve the cache.
     *
     * Current we only read configuration from a local hard-coded file.
     *
     * @throws DataAccessParsingException when failing to parse a TMDB configuration payload
     * @throws IOException when failing to read configuration from a locally persisted file cache
     */
    public synchronized void init() throws DataAccessParsingException, IOException {
        if (mIsInitialized) {
            return;
        }
        mConfiguration = getCachedConfiguration();
        if (mConfiguration == null) {
            // Cache miss. This is a WIP. On cache miss we should fall back on our default config
            // but also make a asynchronous call to get updated config from the TMDB Web Service.
            mConfiguration = getDefaultStaticConfiguration();
        }
        mIsInitialized = true;
    }

    /**
     * Get instance of {@link Configuration} containing TMDB config values. Must call
     * {@link #init()} before attempting to retrieve configuration or an IllegalStateException is
     * thrown.
     *
     * @return an instance of {@link Configuration} containing TMDB config values.
     */
    public Configuration getTMDBConfiguration() {
        if (!mIsInitialized) {
            throw new IllegalStateException("Configuration cache has not been initialized." +
                    " #init() must be called first to initialized the configuration cache.");
        }
        return mConfiguration;
    }

    /**
     * Retrieves the default TMDB configuration by reading a static hardcoded Resource file.
     *
     * @return {@link Configuration} constructed by reading the TMDB default config file.
     * @throws IOException when failing to read the TMDB default configuration file.
     * @throws DataAccessParsingException when failing to parse the TMDB default configuration.
     */
    private Configuration getDefaultStaticConfiguration()
            throws IOException, DataAccessParsingException {

        final String configurationContents = IOHelper.readResourceToString(
                mContext, R.raw.tmdb_default_config);
        return parseConfiguration(configurationContents);
    }

    /**
     * Retrieves and returns TMDB configuration via a local file cache or null if no cache file
     * exists.
     *
     * @return {@link Configuration} constructed by reading the TMDB config cache.
     * @throws IOException when failing to read the TMDB configuration cache file.
     * @throws DataAccessParsingException when failing to parse the TMDB configuration contained in
     *                                    the cache file.
     */
    private Configuration getCachedConfiguration() throws IOException, DataAccessParsingException {

        final File cacheFile = new File(mContext.getCacheDir(), CONFIGURATION_CACHE_FILE_NAME);

        if (cacheFile.exists()) {
            final String configurationContents = IOHelper.readFileToString(cacheFile);
            return parseConfiguration(configurationContents);
        }

        return null;
    }

    /**
     * Given string-ified TMDB configuration JSON, construct a {@link Configuration} model object.
     *
     * @param configurationJSONStr {@link String} containing TMDB configuration JSON.
     * @return {@link Configuration} constructed from the given config JSON String.
     * @throws DataAccessParsingException when failing to parse the given config JSON String.
     */
    private Configuration parseConfiguration(final String configurationJSONStr)
            throws DataAccessParsingException {

        try {
            final JSONObject configurationJSON = new JSONObject(configurationJSONStr);
            final JSONConfigurationTransformer jsonConfigurationTransformer =
                    new JSONConfigurationTransformer();
            return jsonConfigurationTransformer.transform(configurationJSON);
        } catch (JSONException | ParseException ex) {
            throw new DataAccessParsingException("Failed to parse configuration JSON.", ex);
        }
    }
}
