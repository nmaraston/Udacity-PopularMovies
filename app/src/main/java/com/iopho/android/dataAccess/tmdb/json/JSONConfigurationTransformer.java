package com.iopho.android.dataAccess.tmdb.json;

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
 * A JSONConfigurationTransformer is an implementation of {@link JSONObjectTransformer} to transform
 * a given {@link JSONObject} into a {@link Configuration}.
 */
public class JSONConfigurationTransformer implements JSONObjectTransformer<Configuration> {

    private enum JSON_KEY {

        IMAGES           ("images"),
        BASE_URL         ("base_url"),
        SECURE_BASE_URL  ("secure_base_url"),
        BACKDROP_SIZES   ("backdrop_sizes"),
        LOGO_SIZES       ("logo_sizes"),
        POSTER_SIZES     ("poster_sizes"),
        PROFILE_SIZES    ("profile_sizes"),
        STILL_SIZES      ("still_sizes");

        private final String mKeyName;

        JSON_KEY(final String keyName) {
            this.mKeyName = keyName;
        }

        public String getKeyName() {
            return mKeyName;
        }
    }

    /**
     * @see {@link JSONObjectTransformer#transform(JSONObject)}.
     */
    @Override
    public Configuration transform(final JSONObject jsonObject) throws JSONException, ParseException {

        Preconditions.checkNotNull(jsonObject, "jsonObject must not be null.");

        final JSONObject imageConfigJSON = jsonObject.getJSONObject(JSON_KEY.IMAGES.getKeyName());

        final String assetBaseURL = imageConfigJSON.getString(JSON_KEY.BASE_URL.getKeyName());
        final String assetSecureBaseURL = imageConfigJSON.getString(
                JSON_KEY.SECURE_BASE_URL.getKeyName());
        final List<ImageSize> availableBackdropImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.BACKDROP_SIZES.getKeyName());
        final List<ImageSize> availableLogoImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.LOGO_SIZES.getKeyName());
        final List<ImageSize> availablePosterImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.POSTER_SIZES.getKeyName());
        final List<ImageSize> availableProfileImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.PROFILE_SIZES.getKeyName());
        final List<ImageSize> availableStillImageSizes = parseImageSizeList(
                imageConfigJSON, JSON_KEY.STILL_SIZES.getKeyName());

        return new Configuration(assetBaseURL, assetSecureBaseURL, availableBackdropImageSizes,
                availableLogoImageSizes, availablePosterImageSizes, availableProfileImageSizes,
                availableStillImageSizes);
    }

    private List<ImageSize> parseImageSizeList(final JSONObject jsonObject, final String jsonKey)
            throws JSONException {

        final List<ImageSize> imageSizes = new ArrayList<>();
        final JSONArray jsonArray = jsonObject.getJSONArray(jsonKey);

        for (int i = 0; i < jsonArray.length(); i++) {
            imageSizes.add(ImageSize.getImageSizeForTMDBKey(jsonArray.getString(i)));
        }

        return imageSizes;
    }
}
