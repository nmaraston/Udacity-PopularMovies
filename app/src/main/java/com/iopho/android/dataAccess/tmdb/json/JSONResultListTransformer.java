package com.iopho.android.dataAccess.tmdb.json;

import com.google.common.base.Preconditions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A JSONResultListTransformer is an implementation of a {@link JSONToObjectTransformer}. It
 * transforms a given JSONObject containing a JSONArray with key "results"
 * into a List of type T.
 *
 * @param <T> the type of Object to transform each entry of the "results" JSONArray to.
 */
public class JSONResultListTransformer<T> implements JSONToObjectTransformer<List<T>> {

    private static class JSON_KEY {
        public static final String RESULTS = "results";
    }

    private final JSONToObjectTransformer<T> resultJSONToObjectTransformer;

    /**
     * Constructs a new JSONResultListTransformer.
     *
     * @param resultJSONToObjectTransformer a {@link JSONToObjectTransformer} that can transform a
     *                                      {@link JSONObject} into a instance of <b>T</b>. Used to
     *                                      transform the entries of the "results" JSONArray.
     */
    public JSONResultListTransformer(final JSONToObjectTransformer<T> resultJSONToObjectTransformer) {
        this.resultJSONToObjectTransformer = resultJSONToObjectTransformer;
    }

    /**
     * @see {@link JSONToObjectTransformer#transform(JSONObject)}.
     */
    @Override
    public List<T> transform(final JSONObject jsonObject) throws JSONException, ParseException {

        Preconditions.checkNotNull(jsonObject, "jsonObject must not be null.");

        final JSONArray resultsJSONArray = jsonObject.getJSONArray(JSON_KEY.RESULTS);
        final List<T> resultList = new ArrayList<>(resultsJSONArray.length());

        for (int i = 0; i < resultsJSONArray.length(); i++) {
            resultList.add(resultJSONToObjectTransformer.transform(
                    resultsJSONArray.getJSONObject(i)));
        }

        return resultList;
    }
}
