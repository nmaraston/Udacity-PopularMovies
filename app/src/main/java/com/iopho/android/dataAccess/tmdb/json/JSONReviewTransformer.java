package com.iopho.android.dataAccess.tmdb.json;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.model.Review;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Implements a {@link JSONToObjectTransformer} to transform a given {@link JSONObject} into a
 * {@link Review}.
 */
public class JSONReviewTransformer implements JSONToObjectTransformer<Review> {

    private static class JSON_KEY {
        public static final String ID = "id";
        public static final String AUTHOR = "author";
        public static final String CONTENT = "content";
    }

    /**
     * @see {@link JSONToObjectTransformer#transform(JSONObject)}
     */
    @Override
    public Review transform(final JSONObject jsonObject) throws JSONException, ParseException {

        Preconditions.checkNotNull(jsonObject, "jsonObject must not be null.");

        final String id = jsonObject.getString(JSON_KEY.ID);
        final String author = jsonObject.getString(JSON_KEY.AUTHOR);
        final String content = jsonObject.getString(JSON_KEY.CONTENT);

        return new Review(id, author, content);
    }
}
