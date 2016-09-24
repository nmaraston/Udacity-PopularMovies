package com.iopho.android.dataAccess.tmdb.json;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * A JSONToObjectTransformer transforms a {@link JSONObject} into an instance of <b>T</b>
 *
 * @param <T> the type of instance to construct a given {@link JSONObject} from.
 */
public interface JSONToObjectTransformer<T> {

    /**
     * Given a {@link JSONObject}, use its serialized data to construct an instance of <b>T</b>.
     *
     * @param jsonObject the {@link JSONObject} to transform.
     * @return an instance of <b>T</b>.
     * @throws JSONException if JSON parsing fails or if a required JSON field is missing from the
     *                       given {@link JSONObject}.
     * @throws ParseException if a non JSON related fatal error occurs when parsing the payload
     *                        (e.g. unexpected serialized Date format, etc).
     */
    T transform(JSONObject jsonObject) throws JSONException, ParseException;
}
