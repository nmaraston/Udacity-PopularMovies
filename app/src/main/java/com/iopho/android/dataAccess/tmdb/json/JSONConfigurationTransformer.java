package com.iopho.android.dataAccess.tmdb.json;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.model.Configuration;
import com.iopho.android.dataAccess.tmdb.model.ImageSize;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A JSONConfigurationTransformer is an implementation of {@link JSONToObjectTransformer} to
 * transform a given {@link JSONObject} into a {@link Configuration} and
 * {@link ObjectToJSONTransformer} to perform the inverse transformation.
 */
public class JSONConfigurationTransformer
        implements JSONToObjectTransformer<Configuration>, ObjectToJSONTransformer<Configuration> {

    private static final String LOG_TAG = JSONConfigurationTransformer.class.getSimpleName();

    private static class JSON_KEY {
        public static final String IMAGES          = "images";
        public static final String BASE_URL        = "base_url";
        public static final String SECURE_BASE_URL = "secure_base_url";
        public static final String BACKDROP_SIZES  = "backdrop_sizes";
        public static final String LOGO_SIZES      = "logo_sizes";
        public static final String POSTER_SIZES    = "poster_sizes";
        public static final String PROFILE_SIZES   = "profile_sizes";
        public static final String STILL_SIZES     = "still_sizes";
    }

    /**
     * @see {@link JSONToObjectTransformer#transform(JSONObject)}.
     */
    @Override
    public Configuration transform(final JSONObject jsonObject) throws JSONException, ParseException {

        Preconditions.checkNotNull(jsonObject, "jsonObject must not be null.");

        final JSONObject imageConfigJSON = jsonObject.getJSONObject(JSON_KEY.IMAGES);

        final String assetBaseURL = imageConfigJSON.getString(JSON_KEY.BASE_URL);
        final String assetSecureBaseURL = imageConfigJSON.getString(JSON_KEY.SECURE_BASE_URL);
        final List<ImageSize> availableBackdropImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.BACKDROP_SIZES);
        final List<ImageSize> availableLogoImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.LOGO_SIZES);
        final List<ImageSize> availablePosterImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.POSTER_SIZES);
        final List<ImageSize> availableProfileImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.PROFILE_SIZES);
        final List<ImageSize> availableStillImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.STILL_SIZES);

        return new Configuration(assetBaseURL, assetSecureBaseURL, availableBackdropImageSizes,
                availableLogoImageSizes, availablePosterImageSizes, availableProfileImageSizes,
                availableStillImageSizes);
    }

    /**
     * @see {@link ObjectToJSONTransformer#transform(Object)}.
     */
    @Override
    public JSONObject transform(final Configuration configuration) throws JSONException {

        Preconditions.checkNotNull(configuration, "configuration must not be null.");

        final JSONObject result = new JSONObject();
        final JSONObject imageConfigJSON = new JSONObject();

        imageConfigJSON.put(JSON_KEY.BASE_URL, configuration.getAssetBaseURL());
        imageConfigJSON.put(JSON_KEY.SECURE_BASE_URL, configuration.getAssetSecureBaseURL());
        imageConfigJSON.put(JSON_KEY.BACKDROP_SIZES,
                buildImageSizeJSONArray(configuration.getAvailableBackdropImageSizes()));
        imageConfigJSON.put(JSON_KEY.LOGO_SIZES,
                buildImageSizeJSONArray(configuration.getAvailableLogoImageSizes()));
        imageConfigJSON.put(JSON_KEY.POSTER_SIZES,
                buildImageSizeJSONArray(configuration.getAvailablePosterImageSizes()));
        imageConfigJSON.put(JSON_KEY.PROFILE_SIZES,
                buildImageSizeJSONArray(configuration.getAvailableProfileImageSizes()));
        imageConfigJSON.put(JSON_KEY.STILL_SIZES,
                buildImageSizeJSONArray(configuration.getAvailableStillImageSizes()));

        result.put(JSON_KEY.IMAGES, imageConfigJSON);

        return result;
    }

    private List<ImageSize> parseImageSizeList(final JSONObject jsonObject, final String jsonKey)
            throws JSONException {

        final List<ImageSize> imageSizes = new ArrayList<>();
        final JSONArray jsonArray = jsonObject.getJSONArray(jsonKey);

        for (int i = 0; i < jsonArray.length(); i++) {
            final String imageSizeStr = jsonArray.getString(i);
            final ImageSize imageSize = ImageSize.getImageSizeForTMDBKey(imageSizeStr);
            if (imageSize != null) {
                imageSizes.add(imageSize);
            } else {
                Log.w(LOG_TAG, "Unrecognized image size: " + imageSizeStr);
            }
        }

        return imageSizes;
    }

    private JSONArray buildImageSizeJSONArray(final List<ImageSize> imageSizes) {

        final JSONArray result = new JSONArray();
        for (ImageSize imageSize : imageSizes) {
            result.put(imageSize.getTMDBKey());
        }
        return result;
    }
}
