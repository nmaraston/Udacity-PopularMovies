package com.iopho.android.dataAccess.tmdb.model;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum of all possible movie image sizes made available via TMDB.
 */
public enum ImageSize {

    W_45     ("w45"),
    W_92     ("w92"),
    W_154    ("w154"),
    W_185    ("w185"),
    W_300    ("w300"),
    W_342    ("w342"),
    W_500    ("w500"),
    W_780    ("w780"),
    W_1280   ("w1280"),

    H_632    ("h632"),

    ORIGINAL ("original");

    private static Map<String, ImageSize> keyMap = new HashMap<>();
    static {
        for (ImageSize imageSize : ImageSize.values()) {
            keyMap.put(imageSize.getTMDBKey(), imageSize);
        }
    }

    private String mTMDBKey;

    ImageSize(final String tmdbKey) {
        this.mTMDBKey = tmdbKey;
    }

    /**
     * @return the string value used by TMDB to represent this key.
     */
    public String getTMDBKey() {
        return mTMDBKey;
    }

    /**
     * @return the ImageSize enum value associated with the given tmdbKey. Or null if one does not
     * exist.
     */
    public static ImageSize getImageSizeForTMDBKey(final String tmdbKey) {
        Preconditions.checkNotNull(tmdbKey, "tmdbKey must not be null.");
        return keyMap.get(tmdbKey);
    }
}
