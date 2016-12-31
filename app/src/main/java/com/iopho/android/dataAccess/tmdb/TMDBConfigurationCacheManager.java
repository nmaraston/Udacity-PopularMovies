package com.iopho.android.dataAccess.tmdb;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.json.JSONConfigurationTransformer;
import com.iopho.android.dataAccess.tmdb.model.Configuration;
import com.iopho.android.popularmovies.R;
import com.iopho.android.util.IOHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

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
 */
class TMDBConfigurationCacheManager {

    private static final String LOG_TAG = TMDBConfigurationCacheManager.class.getSimpleName();

    private final Context mContext;
    private final TMDBConfigurationClient mTMDBConfigurationClient;
    private final JSONConfigurationTransformer mJSONConfigurationTransformer;
    private final String mConfigurationCacheFileName;
    private final int mConfigurationCacheTTLDays;
    private final long mConfigurationCacheAgeThreshold;

    private Configuration mConfiguration;
    private boolean mIsInitialized;

    /**
     * Construct a new TMDBConfigurationCacheManager.
     *
     * @param context the current {@link Context}
     * @param tmdbConfigurationClient to retrieve TMDB configuration values.
     * @param jsonConfigurationTransformer to parse TMDB configuration JSON
     * @param configurationCacheFileName the name of the cache file to read/write from.
     * @param configurationCacheTTLDays allowed number of days that a cache file can be used before
     *                                  being considered stale.
     */
    public TMDBConfigurationCacheManager(final Context context,
                                         final TMDBConfigurationClient tmdbConfigurationClient,
                                         final JSONConfigurationTransformer jsonConfigurationTransformer,
                                         final String configurationCacheFileName,
                                         final int configurationCacheTTLDays) {

        Preconditions.checkNotNull(context, "context must not be null.");
        Preconditions.checkNotNull(tmdbConfigurationClient,
                "tmdbConfigurationClient must not be null.");
        Preconditions.checkNotNull(jsonConfigurationTransformer,
                "jsonConfigurationTransformer must not be null.");
        Preconditions.checkNotNull(configurationCacheFileName,
                "configurationCacheFileName must not be null.");
        Preconditions.checkArgument(configurationCacheTTLDays > 0,
                "configurationCacheTTLDays must be positive.");

        this.mContext = context;
        this.mTMDBConfigurationClient = tmdbConfigurationClient;
        this.mJSONConfigurationTransformer = jsonConfigurationTransformer;
        this.mConfigurationCacheFileName = configurationCacheFileName;
        this.mConfigurationCacheTTLDays = configurationCacheTTLDays;

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1 * configurationCacheTTLDays);
        this.mConfigurationCacheAgeThreshold = calendar.getTimeInMillis();

        this.mIsInitialized = false;
    }

    /**
     * Initialize the configuration cache. Must be called before calls to
     * {@link #getTMDBConfiguration()} to retrieve the cache.
     *
     * Configuration is provided by TMDB Web Service and cached. On init, we attempt to load
     * configuration from the local file cache. If it does not exist or is stale we load
     * configuration from a default static file and kick off an async worker to fetch and cache the
     * latest config. Temporarily providing default config from a local file ensures this method
     * does not require blocking network IO on the current thread and can be called from the UI
     * thread.
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
            // Cache miss. Fall back on default static config.
            Log.d(LOG_TAG, "Temporarily falling back on static default config while fetching the" +
                    " latest config and re-populating the cache.");
            mConfiguration = getDefaultStaticConfiguration();

            // Trigger an async worker thread to fetch the latest config and populate the file
            // cache.
            Thread thread = new Thread(new FetchAndCacheConfigurationRunnable());
            thread.start();
        } else {
            Log.d(LOG_TAG, "Configuration loaded from cache file.");
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
                mContext, R.raw.tmdb_default_remote_config);
        return parseConfiguration(configurationContents);
    }

    /**
     * Retrieves and returns TMDB configuration via a local file cache. Returns null if no cache
     * file exists or the cache file is stale.
     *
     * @return {@link Configuration} constructed by reading the TMDB config cache.
     * @throws IOException when failing to read the TMDB configuration cache file.
     * @throws DataAccessParsingException when failing to parse the TMDB configuration contained in
     *                                    the cache file.
     */
    private Configuration getCachedConfiguration() throws IOException, DataAccessParsingException {

        final File cacheFile = new File(mContext.getCacheDir(), mConfigurationCacheFileName);

        if (cacheFile.exists()) {

            final long cacheFileAge = cacheFile.lastModified();

            if (cacheFileAge < mConfigurationCacheAgeThreshold) {
                Log.d(LOG_TAG, String.format(
                        "Configuration cache is stale (more than %d day(s) old)",
                        mConfigurationCacheTTLDays));
                return null;
            }

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
            return mJSONConfigurationTransformer.transform(configurationJSON);
        } catch (JSONException | ParseException ex) {
            throw new DataAccessParsingException("Failed to parse configuration JSON.", ex);
        }
    }

    /**
     * A runnable to fetch the latest configuration from the TMDB web service and write it to the
     * cache file.
     */
    private class FetchAndCacheConfigurationRunnable implements Runnable {

        private final String RUNNABLE_LOG_TAG = LOG_TAG + "." +
                FetchAndCacheConfigurationRunnable.class.getSimpleName();

        @Override
        public void run() {

            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            try {
                // Request latest TMDB configuration from TMDB Web Service.
                final Configuration configuration = mTMDBConfigurationClient.getConfiguration();

                // Cache latest TMDB configuration in cache file.
                final JSONObject configurationJSON = mJSONConfigurationTransformer.transform(configuration);
                final File cacheFile =
                        new File(mContext.getCacheDir(), mConfigurationCacheFileName);
                IOHelper.writeStringToFile(configurationJSON.toString(), cacheFile, false);

                // Set current instance of TMDB configuration to latest.
                mConfiguration = configuration;
            } catch (DataAccessRequestException | DataAccessParsingException ex) {
                Log.e(RUNNABLE_LOG_TAG,
                        "Failed to fetch latest configuration from the TMDB Web Service.");
            } catch (JSONException ex) {
                Log.e(RUNNABLE_LOG_TAG, "Failed to construct TMDB configuration JSON.");
            } catch (IOException ex) {
                Log.e(RUNNABLE_LOG_TAG, "Failed to write TMDB configuration to cache file.");
            }
        }
    }
}
