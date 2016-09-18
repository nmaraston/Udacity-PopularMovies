package com.iopho.android.dataAccess.tmdb;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.util.HttpURLDownloader;

/**
 * The TMDBClientFactory is intended to be the main point of integration for an Android App.
 *
 * The TMDBClientFactory must be initialized with a call to {@link #init()} before attempting to
 * obtain client components to interact with TMDB.
 */
public class TMDBClientFactory {

    private final String TMDB_BASE_URL = "http://api.themoviedb.org/3/";

    private final HttpURLDownloader mHTTPURLDownloader;

    private final TMDBConfigurationClient mTMDBConfigurationClient;
    private final TMDBMovieClient mTMDBMovieClient;

    private final TMDBConfigurationCacheManager mTMDBConfigurationCacheManager;
    private final TMDBAssetURLFactory mTMDBAssetURLFactory;

    private boolean mIsInitialized;

    /**
     * Construct a new TMDBClientFactory.
     *
     * @param apiKey TMDB API key. Required to interact with the TMDB Web Service. See
     *               <a href="https://www.themoviedb.org/documentation/api">
     *                   https://www.themoviedb.org/documentation/api</a>
     */
    public TMDBClientFactory(final String apiKey) {
        this(apiKey, HttpURLDownloader.INFINITE_TIMEOUT, HttpURLDownloader.INFINITE_TIMEOUT);
    }

    /**
     * Construct a new TMDBClientFactory.
     *
     * @param apiKey TMDB API key. Required to interact with the TMDB Web Service. See
     *               <a href="https://www.themoviedb.org/documentation/api">
     *                   https://www.themoviedb.org/documentation/api</a>
     * @param readTimeout timeout (in milliseconds) when reading data from the TMDB Web Service.
     *                    Most be a non-negative value. 0 indicates an infinite timeout.
     * @param connectTimeout timeout (in milliseconds) when attempting to open a connection to the
     *                       TMDB Web Service. Most be a non-negative value. 0 indicates an infinite
     *                       timeout.
     */
    public TMDBClientFactory(final String apiKey, final int readTimeout, final int connectTimeout) {

        Preconditions.checkNotNull(apiKey, "apiKey must not be null.");
        Preconditions.checkArgument(readTimeout >= 0, "readTimeout must be non-negative.");
        Preconditions.checkArgument(connectTimeout >= 0, "connectTimeout must be non-negative.");

        this.mHTTPURLDownloader = new HttpURLDownloader(readTimeout, connectTimeout);

        this.mTMDBConfigurationClient = new TMDBConfigurationClient(
                TMDB_BASE_URL, apiKey, mHTTPURLDownloader);
        this.mTMDBMovieClient = new TMDBMovieClientImpl(TMDB_BASE_URL, apiKey, mHTTPURLDownloader);

        this.mTMDBConfigurationCacheManager = new TMDBConfigurationCacheManager(
                mTMDBConfigurationClient);

        this.mTMDBAssetURLFactory = new TMDBAssetURLFactory(mTMDBConfigurationCacheManager);

        this.mIsInitialized = false;
    }

    /**
     * Initialize the TMDBClientFactory.
     *
     * This has the potential to trigger a network call to the TMDB web service and thus must not be
     * invoked when executing on the UI thread.
     *
     * @throws DataAccessRequestException when network calls to the TMDB web service fail.
     * @throws DataAccessParsingException when failing to parse responses from the TMDB web service.
     */
    public void init() throws DataAccessRequestException, DataAccessParsingException {
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
        if (!isInitialized()) {
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
        if (!isInitialized()) {
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
