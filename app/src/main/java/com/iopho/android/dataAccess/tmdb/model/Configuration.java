package com.iopho.android.dataAccess.tmdb.model;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.List;

/**
 * Configuration specific to interacting with TMDB and it's hosted assets.
 *
 * This class is designed to be immutable.
 */
public class Configuration {

    private final String mAssetBaseURL;
    private final String mAssetSecureBaseURL;
    private final List<ImageSize> mAvailableBackdropImageSizes;
    private final List<ImageSize> mAvailableLogoImageSizes;
    private final List<ImageSize> mAvailablePosterImageSizes;
    private final List<ImageSize> mAvailableProfileImageSizes;
    private final List<ImageSize> mAvailableStillImageSizes;

    /**
     * Construct a new Configuration.
     *
     * @param assetBaseURL base URL to request assets from.
     * @param assetSecureBaseURL secure base URL to request assets from.
     * @param availableBackdropImageSizes available image sizes for backdrop images.
     * @param availableLogoImageSizes available image sizes for logo images.
     * @param availablePosterImageSizes available image sizes for poster images.
     * @param availableProfileImageSizes available image sizes for profile images.
     * @param availableStillImageSizes available image sizes for still images.
     */
    public Configuration(final String assetBaseURL,
                         final String assetSecureBaseURL,
                         final List<ImageSize> availableBackdropImageSizes,
                         final List<ImageSize> availableLogoImageSizes,
                         final List<ImageSize> availablePosterImageSizes,
                         final List<ImageSize> availableProfileImageSizes,
                         final List<ImageSize> availableStillImageSizes) {

        Preconditions.checkNotNull(assetBaseURL, "assetBaseURL must not be null.");
        Preconditions.checkNotNull(assetSecureBaseURL, "assetSecureBaseURL must not be null.");
        Preconditions.checkNotNull(availableBackdropImageSizes,
                "availableBackdropImageSizes must not be null.");
        Preconditions.checkNotNull(availableLogoImageSizes,
                "availableLogoImageSizes must not be null.");
        Preconditions.checkNotNull(availablePosterImageSizes,
                "availablePosterImageSizes must not be null.");
        Preconditions.checkNotNull(availableProfileImageSizes,
                "availableProfileImageSizes must not be null.");
        Preconditions.checkNotNull(availableStillImageSizes,
                "availableStillImageSizes must not be null.");

        this.mAssetBaseURL = assetBaseURL;
        this.mAssetSecureBaseURL = assetSecureBaseURL;
        this.mAvailableBackdropImageSizes = Collections.unmodifiableList(
                availableBackdropImageSizes);
        this.mAvailableLogoImageSizes = Collections.unmodifiableList(availableLogoImageSizes);
        this.mAvailablePosterImageSizes = Collections.unmodifiableList(availablePosterImageSizes);
        this.mAvailableProfileImageSizes = Collections.unmodifiableList(availableProfileImageSizes);
        this.mAvailableStillImageSizes = Collections.unmodifiableList(availableStillImageSizes);
    }

    /**
     * @return base URL to request assets from.
     */
    public String getAssetBaseURL() {
        return mAssetBaseURL;
    }

    /**
     * @return secure base URL to request assets from.
     */
    public String getAssetSecureBaseURL() {
        return mAssetSecureBaseURL;
    }

    /**
     * @return list of available image sizes for backdrop images.
     */
    public List<ImageSize> getAvailableBackdropImageSizes() {
        return mAvailableBackdropImageSizes;
    }

    /**
     * @return list of available image sizes for logo images.
     */
    public List<ImageSize> getAvailableLogoImageSizes() {
        return mAvailableLogoImageSizes;
    }

    /**
     * @return list of available image sizes for poster images.
     */
    public List<ImageSize> getAvailablePosterImageSizes() {
        return mAvailablePosterImageSizes;
    }

    /**
     * @return list of available image sizes for profile images.
     */
    public List<ImageSize> getAvailableProfileImageSizes() {
        return mAvailableProfileImageSizes;
    }

    /**
     * @return list of available image sizes for still images.
     */
    public List<ImageSize> getAvailableStillImageSizes() {
        return mAvailableStillImageSizes;
    }
}
