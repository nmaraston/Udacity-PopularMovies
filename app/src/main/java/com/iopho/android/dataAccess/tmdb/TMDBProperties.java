package com.iopho.android.dataAccess.tmdb;

import android.content.Context;

import com.iopho.android.popularmovies.R;
import com.iopho.android.util.ApplicationProperties;

import java.io.IOException;

class TMDBProperties {

    private enum Property {
        BASE_URL,
        REMOTE_CONFIG_CACHE_FILE_NAME,
        REMOTE_CONFIG_CACHE_TTL_DAYS,
        HTTP_CONNECT_TIMEOUT,
        HTTP_READ_TIMEOUT
    }

    private final ApplicationProperties mApplicationProperties;

    /**
     * Construct a new TMDBProperties instance.
     *
     * @param context the current {@link Context}
     * @throws IOException when failing to read the TMDB properties resource file.
     */
    public TMDBProperties(final Context context) throws IOException {
        mApplicationProperties = new ApplicationProperties(context, R.raw.tmdb_config);
    }

    /**
     * @return the TMDB Web Service base URL.
     */
    public String getBaseURL() {
        return mApplicationProperties.getStringPropertyValue(Property.BASE_URL.name());
    }

    /**
     * @return the TMDB remote configuration cache file name.
     */
    public String getRemoteConfigCacheFileName() {
        return mApplicationProperties.getStringPropertyValue(
                Property.REMOTE_CONFIG_CACHE_FILE_NAME.name());
    }

    /**
     * The cache TTL is an integer that specifies the number of days before the remote configuration
     * cache is considered stale.
     *
     * @return the TMDB remote configuration cache TTL.
     */
    public int getRemoteConfigCacheTTLDays() {
        return mApplicationProperties.getIntPropertyValue(
                Property.REMOTE_CONFIG_CACHE_TTL_DAYS.name());
    }

    /**
     * Timeout (in milliseconds) when attempting to open a connection to the TMDB Web Service.
     * 0 indicates an infinite timeout.
     *
     * @return HTTP connect timeout. 0 indicates an infinite timeout.
     */
    public int getHTTPConnectTimeout() {
        return mApplicationProperties.getIntPropertyValue(
                Property.HTTP_CONNECT_TIMEOUT.name());
    }

    /**
     * Timeout (in milliseconds) when reading data from the TMDB Web Service.
     * 0 indicates an infinite timeout.
     *
     * @return HTTP read timeout. 0 indicates an infinite timeout.
     */
    public int getHTTPReadTimeout() {
        return mApplicationProperties.getIntPropertyValue(
                Property.HTTP_READ_TIMEOUT.name());
    }
}
