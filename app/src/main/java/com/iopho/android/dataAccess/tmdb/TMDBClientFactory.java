package com.iopho.android.dataAccess.tmdb;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.json.JSONConfigurationTransformer;
import com.iopho.android.util.HttpURLDownloader;

import java.io.IOException;

/**
 * The TMDBClientFactory is intended to be the main point of integration for an Android App.
 *
 * The TMDBClientFactory must be initialized with a call to {@link #init()} before attempting to
 * obtain client components to interact with TMDB.
 */
public class TMDBClientFactory {

    private final Context mContext;
    private final String mAPIKey;

    private TMDBProperties mTMDBProperties;
    private HttpURLDownloader mHTTPURLDownloader;
    private TMDBConfigurationClient mTMDBConfigurationClient;
    private TMDBMovieClient mTMDBMovieClient;
    private TMDBConfigurationCacheManager mTMDBConfigurationCacheManager;
    private TMDBAssetURLFactory mTMDBAssetURLFactory;

    private boolean mIsInitialized;

    /**
     * Construct a new TMDBClientFactory.
     *
     * @param context the current {@link Context}
     * @param apiKey TMDB API key. Required to interact with the TMDB Web Service. See
     *               <a href="https://www.themoviedb.org/documentation/api">
     *                   https://www.themoviedb.org/documentation/api</a>
     */
    public TMDBClientFactory(final Context context, final String apiKey) {

        Preconditions.checkNotNull(context, "context must not be null.");
        Preconditions.checkNotNull(apiKey, "apiKey must not be null.");

        this.mContext = context;
        this.mAPIKey = apiKey;
        this.mIsInitialized = false;
    }

    /**
     * Initialize the TMDBClientFactory.
     *
     * This has the potential to trigger a network call to the TMDB web service and thus must not be
     * invoked when executing on the UI thread.
     *
     * @throws DataAccessParsingException when failing to parse responses from the TMDB web service.
     * @throws IOException when failing to read local configuration data
     */
    public synchronized void init() throws DataAccessParsingException, IOException {
        if (mIsInitialized) {
            return;
        }

        mTMDBProperties = new TMDBProperties(mContext);

        mHTTPURLDownloader = new HttpURLDownloader(
                mTMDBProperties.getHTTPConnectTimeout(), mTMDBProperties.getHTTPReadTimeout());

        this.mTMDBConfigurationClient = new TMDBConfigurationClient(
                mTMDBProperties.getBaseURL(), mAPIKey, mHTTPURLDownloader);

        this.mTMDBMovieClient = new TMDBMovieClientImpl(
                mTMDBProperties.getBaseURL(), mAPIKey, mHTTPURLDownloader);

        this.mTMDBConfigurationCacheManager = new TMDBConfigurationCacheManager(
                mContext, mTMDBConfigurationClient, new JSONConfigurationTransformer(),
                mTMDBProperties.getRemoteConfigCacheFileName(),
                mTMDBProperties.getRemoteConfigCacheTTLDays());

        this.mTMDBAssetURLFactory = new TMDBAssetURLFactory(mTMDBConfigurationCacheManager);

        mTMDBConfigurationCacheManager.init();
        mIsInitialized = true;
    }

    /**
     * Get a {@link TMDBMovieClient}. The TMDBClientFactory must be initialized (via a call to
     * {@link #init()}) prior to calling this method.
     *
     * @return a {@link TMDBMovieClient}
     */
    public TMDBMovieClient getTMDBMovieClient() {
        if (!mIsInitialized) {
            throw new IllegalStateException(
                    "TMDBClientFactory is uninitialized. Must call #init() first.");
        }
        return mTMDBMovieClient;
    }

    /**
     * Get a {@link TMDBAssetURLFactory}. The TMDBClientFactory must be initialized (via a call to
     * {@link #init()}) prior to calling this method.
     *
     * @return a {@link TMDBAssetURLFactory}
     */
    public TMDBAssetURLFactory getTMDBAssetURLFactory() {
        if (!mIsInitialized) {
            throw new IllegalStateException(
                    "TMDBClientFactory is uninitialized. Must call #init() first.");
        }
        return mTMDBAssetURLFactory;
    }

    /**
     * @return true iff this TMDBClientFactory has been initialized.
     */
    public boolean isInitialized() {
        return mIsInitialized;
    }
}
