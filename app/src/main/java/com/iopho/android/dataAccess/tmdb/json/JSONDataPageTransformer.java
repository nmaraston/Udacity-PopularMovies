package com.iopho.android.dataAccess.tmdb.json;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.model.DataPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A JSONDataPageTransformer is an implementation of a {@link JSONObjectTransformer}. It transforms
 * a given JSONObject into a {@link DataPage}.
 *
 * @param <T> the type of Object contained in the DataPage being transformed to.
 */
public class JSONDataPageTransformer<T> implements JSONObjectTransformer<DataPage<T>> {

    private enum JSON_KEY {

        PAGE           ("page"),
        TOTAL_PAGES    ("total_pages"),
        RESULTS        ("results"),
        TOTAL_RESULTS  ("total_results");

        private final String mKeyName;

        JSON_KEY(final String keyName) {
            this.mKeyName = keyName;
        }

        public String getKeyName() {
            return mKeyName;
        }
    }

    private final JSONObjectTransformer<T> resultJSONObjectTransformer;

    /**
     * Construct a new JSONDataPageTransformer.
     *
     * @param resultJSONObjectTransformer a {@link JSONObjectTransformer} that can transform a
     *                                    {@link JSONObject} into a instance of <b>T</b>. Used to
     *                                    transform the results of the JSON DataPage representation
     *                                    into instances of <b>T</b>.
     */
    public JSONDataPageTransformer(final JSONObjectTransformer<T> resultJSONObjectTransformer) {
        this.resultJSONObjectTransformer = resultJSONObjectTransformer;
    }

    /**
     * @see {@link JSONObjectTransformer#transform(JSONObject)}.
     */
    @Override
    public DataPage transform(final JSONObject jsonObject) throws JSONException, ParseException {

        Preconditions.checkNotNull(jsonObject, "jsonObject must not be null.");

        final int pageNumber = jsonObject.getInt(JSON_KEY.PAGE.getKeyName());
        final int totalPageCount = jsonObject.getInt(JSON_KEY.TOTAL_PAGES.getKeyName());
        final int totalResultCount = jsonObject.getInt(JSON_KEY.TOTAL_RESULTS.getKeyName());

        final JSONArray resultsJSONArray = jsonObject.getJSONArray(JSON_KEY.RESULTS.getKeyName());
        final List<T> resultList = new ArrayList<>(resultsJSONArray.length());

        for (int i = 0; i < resultsJSONArray.length(); i++) {
            resultList.add(resultJSONObjectTransformer.transform(
                    resultsJSONArray.getJSONObject(i)));
        }

        return new DataPage(pageNumber, totalPageCount, totalResultCount, resultList);
    }
}
