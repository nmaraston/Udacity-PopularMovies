package com.iopho.android.dataAccess.tmdb;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.json.JSONConfigurationTransformer;
import com.iopho.android.dataAccess.tmdb.model.Configuration;
import com.iopho.android.popularmovies.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * read from a local file. Cache management will be triggering non-blocking calls to the TMDB API
 * to get an up to date configuration payload and cache it.
 */
class TMDBConfigurationCacheManager {

    private static final String LOG_TAG = TMDBConfigurationCacheManager.class.getSimpleName();

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
        mConfiguration = getDefaultTMDBConfiguration();
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
     * Reads the default TMDB configuration from a local resource file.
     */
    private Configuration getDefaultTMDBConfiguration()
            throws IOException, DataAccessParsingException {

        final String defaultConfigurationFileContents = readResourceFile(R.raw.tmdb_default_config);

        try {
            final JSONObject configurationJSON = new JSONObject(defaultConfigurationFileContents);
            final JSONConfigurationTransformer jsonConfigurationTransformer =
                    new JSONConfigurationTransformer();
            return jsonConfigurationTransformer.transform(configurationJSON);
        } catch (JSONException | ParseException ex) {
            throw new DataAccessParsingException("Failed to parse default configuration resource" +
                    " file.", ex);
        }
    }

    /**
     * Reads the contents of a resource file into a {@link String} and returns it.
     */
    private String readResourceFile(final int resourceFileId) throws IOException {

        final Resources resources = mContext.getResources();

        final InputStream resourceIn;
        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();

        try {
            resourceIn = resources.openRawResource(resourceFileId);
            reader = new BufferedReader(new InputStreamReader(resourceIn, "UTF-8"));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Log.e(LOG_TAG, "Failed to close buffered reader", ex);
                }
            }
        }

        return result.toString();
    }
}
