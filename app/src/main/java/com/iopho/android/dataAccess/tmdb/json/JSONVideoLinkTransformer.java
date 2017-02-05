package com.iopho.android.dataAccess.tmdb.json;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.model.VideoLink;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Implements a {@link JSONToObjectTransformer} to transform a given {@link JSONObject} into a
 * {@link VideoLink}.
 */
public class JSONVideoLinkTransformer implements JSONToObjectTransformer<VideoLink> {

    private static class JSON_KEY {
        public static final String ID   = "id";
        public static final String KEY  = "key";
        public static final String NAME = "name";
        public static final String SITE = "site";
        public static final String SIZE = "size";
        public static final String TYPE = "type";
    }

    /**
     * @see {@link JSONToObjectTransformer#transform(JSONObject)}
     */
    @Override
    public VideoLink transform(final JSONObject jsonObject) throws JSONException, ParseException {

        Preconditions.checkNotNull(jsonObject, "jsonObject must not be null.");

        final String id = jsonObject.getString(JSON_KEY.ID);
        final String key = jsonObject.getString(JSON_KEY.KEY);
        final String name = jsonObject.getString(JSON_KEY.NAME);
        final String site = jsonObject.getString(JSON_KEY.SITE);
        final int size = jsonObject.getInt(JSON_KEY.SIZE);
        final String type = jsonObject.getString(JSON_KEY.TYPE);

        return new VideoLink(id, key, name, site, size, VideoLink.Type.transformToType(type));
    }
}
