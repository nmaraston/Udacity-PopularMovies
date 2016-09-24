package com.iopho.android.dataAccess.tmdb.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A ObjectToJSONTransformer serializes a Java {@link Object} into a {@link JSONObject}.
 *
 * @param <T> the type of instance to serialize into a {@link JSONObject}.
 */
public interface ObjectToJSONTransformer<T> {

    /**
     * Serialize a {@link Object} into a {@link JSONObject}.
     *
     * @param object the object to serialize into a {@link JSONObject}.
     * @return a serialized version of the object as a {@link JSONObject}.
     * @throws JSONException when failing to serialize the given object.
     */
    JSONObject transform(T object) throws JSONException;
}
