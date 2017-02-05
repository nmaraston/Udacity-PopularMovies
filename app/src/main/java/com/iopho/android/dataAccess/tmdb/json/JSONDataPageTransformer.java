package com.iopho.android.dataAccess.tmdb.json;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.model.DataPage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

/**
 * A JSONDataPageTransformer is an implementation of a {@link JSONToObjectTransformer}. It
 * transforms a given JSONObject into a {@link DataPage}.
 *
 * @param <T> the type of Object contained in the DataPage being transformed to.
 */
public class JSONDataPageTransformer<T> implements JSONToObjectTransformer<DataPage<T>> {

    private static class JSON_KEY {
        public static final String PAGE          = "page";
        public static final String TOTAL_PAGES   = "total_pages";
        public static final String TOTAL_RESULTS = "total_results";
    }

    private final JSONResultListTransformer<T> resultsJSONToListTransformer;

    /**
     * Construct a new JSONDataPageTransformer.
     *
     * @param resultJSONToObjectTransformer a {@link JSONToObjectTransformer} that can transform a
     *                                      {@link JSONObject} into a instance of <b>T</b>. Used to
     *                                      transform the results of the JSON DataPage
     *                                      representation into instances of <b>T</b>.
     */
    public JSONDataPageTransformer(final JSONToObjectTransformer<T> resultJSONToObjectTransformer) {
        this.resultsJSONToListTransformer =
                new JSONResultListTransformer(resultJSONToObjectTransformer);
    }

    /**
     * @see {@link JSONToObjectTransformer#transform(JSONObject)}.
     */
    @Override
    public DataPage transform(final JSONObject jsonObject) throws JSONException, ParseException {

        Preconditions.checkNotNull(jsonObject, "jsonObject must not be null.");

        final int totalPageCount = jsonObject.getInt(JSON_KEY.TOTAL_PAGES);
        final int totalResultCount = jsonObject.getInt(JSON_KEY.TOTAL_RESULTS);

        // The TMDB API will simply return the page number that is passed in as an argument.
        // Then, in the case where no pages are present and we request the first page, TMDB will
        // respond with page=1, total_pages=0. To not allow this poor state, we manually set the
        // page number to 0 here.
        final int pageNumber = (totalPageCount == 0) ? 0 : jsonObject.getInt(JSON_KEY.PAGE);

        final List<T> resultList = resultsJSONToListTransformer.transform(jsonObject);

        return new DataPage(pageNumber, totalPageCount, totalResultCount, resultList);
    }
}
