package com.iopho.android.dataAccess.tmdb;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.model.Configuration;
import com.iopho.android.dataAccess.tmdb.model.ImageSize;
import com.iopho.android.dataAccess.tmdb.model.Movie;

/**
 * A TMDBAssetURLFactory is used to retrieve URL to requests assets (images) from the TMDB web
 * service.
 */
public class TMDBAssetURLFactory {

    private final TMDBConfigurationCacheManager mTMDBConfigurationCacheManager;

    /**
     * Construct a new TMDBAssetURLFactory.
     *
     * @param tmdbConfigurationCacheManager a {@link TMDBConfigurationCacheManager} to read
     *                                      configuration values. The endpoint to request images
     *                                      from TMDB is provided via configuration.
     */
    TMDBAssetURLFactory(final TMDBConfigurationCacheManager tmdbConfigurationCacheManager) {

        Preconditions.checkNotNull(tmdbConfigurationCacheManager,
                "tmdbConfigurationCacheManager must not be null.");

        this.mTMDBConfigurationCacheManager = tmdbConfigurationCacheManager;
    }

    /**
     * Builds and returns a URL to request a poster image hosted by TMDB.
     *
     * @param relativePath the relative path to the image asset. This is provided as part of the
     *                     response to other TMDB requests. For example, see
     *                     {@link Movie#getPosterPath()}.
     * @param imageSize size of image.
     * @return a URL to request a poster image hosted by TMDB.
     */
    public String getPosterImageURL(final String relativePath, final ImageSize imageSize) {

        Preconditions.checkNotNull(relativePath, "relativePath must not be null.");
        Preconditions.checkNotNull(imageSize, "imageSize must not be null.");

        final Configuration tmdbConfig = mTMDBConfigurationCacheManager.getTMDBConfiguration();

        return tmdbConfig.getAssetBaseURL() + imageSize.getTMDBKey() + relativePath;
    }
}
