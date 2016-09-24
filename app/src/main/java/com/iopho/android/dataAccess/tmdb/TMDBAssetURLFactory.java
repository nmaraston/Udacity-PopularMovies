package com.iopho.android.dataAccess.tmdb;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.model.Configuration;
import com.iopho.android.dataAccess.tmdb.model.ImageSize;
import com.iopho.android.dataAccess.tmdb.model.Movie;

import java.util.List;

/**
 * A TMDBAssetURLFactory is used to retrieve URL to requests assets (images) from the TMDB web
 * service.
 */
public class TMDBAssetURLFactory {

    private static final String LOG_TAG = TMDBAssetURLFactory.class.getSimpleName();

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
        final List<ImageSize> availablePosterImageSizes = tmdbConfig.getAvailablePosterImageSizes();

        ImageSize resultImageSize = imageSize;
        if (!availablePosterImageSizes.contains(imageSize)) {
            resultImageSize = availablePosterImageSizes.get(0);
            Log.w(LOG_TAG, String.format("Poster image size %s is not available." +
                            " Defaulting to lowest resolution poster image size %s.",
                    imageSize, resultImageSize));
        }

        final String result = String.format("%s%s%s",
                tmdbConfig.getAssetBaseURL(), resultImageSize.getTMDBKey(), relativePath);

        Log.d(LOG_TAG, "Constructed poster image URL: " + result);

        return result;
    }
}
