package com.iopho.android.dataAccess.tmdb;

import android.net.Uri;
import android.support.annotation.StringDef;

import com.google.common.base.Preconditions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A TMDBURLBuilder is a Builder to assist in the construction of URLs for requests to the TMDB API.
 */
class TMDBURLBuilder {

    /**
     * Type safe constants of possible TMDB API endpoints used to interact with different resources.
     */
    public static class Endpoint {

        public static final String CONFIGURATION    = "configuration";

        public static final String MOVIES_POPULAR   = "movie/popular";
        public static final String MOVIES_TOP_RATED = "movie/top_rated";

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({CONFIGURATION, MOVIES_POPULAR, MOVIES_TOP_RATED})
        public @interface Def {}
    }

    /**
     * Type safe constants for possible query param keys.
     */
    public static class QueryParamKey {

        public static final String PAGE = "page";

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({PAGE})
        public @interface Def {}
    }

    // Do not include this query param as a constant above. The interface enforces that it be
    // provided via the constructor.
    private static final String API_KEY_QUERY_PARAM = "api_key";

    private final String mTMDBBaseURL;
    private final String mAPIKey;
    private final @Endpoint.Def String mEndpoint;
    private final Map<String, String> mQueryParams;

    /**
     * Construct a new TMDBURLBuilder.
     *
     * @param tmdbBaseURL the base URL for the TMDB API.
     * @param apiKey a TMDB API key (required query param for all requests).
     */
    public TMDBURLBuilder(final String tmdbBaseURL,
                          final String apiKey,
                          final @Endpoint.Def String endpoint) {

        Preconditions.checkNotNull(tmdbBaseURL, "tmdbBaseURL must not be null.");
        Preconditions.checkNotNull(apiKey, "apiKey must not be null.");
        Preconditions.checkNotNull(endpoint, "endpoint must not be null.");

        this.mTMDBBaseURL = tmdbBaseURL;
        this.mAPIKey = apiKey;
        this.mEndpoint = endpoint;
        this.mQueryParams = new HashMap<>();
    }

    /**
     * Adds a query param to this TMDB request URL.
     *
     * @param key the key name of the query param.
     * @param value the value of the query param.
     * @return this TMDBURLBuilder instance.
     */
    public TMDBURLBuilder withQueryParam(final @QueryParamKey.Def String key, final String value) {

        Preconditions.checkNotNull(key, "key must not be null.");
        Preconditions.checkNotNull(value, "value must not be null.");

        mQueryParams.put(key, value);
        return this;
    }

    /**
     * @return a {@link URL} with all the given base URL, endpoint path, API key query param and all
     * other added query params.
     * @throws MalformedURLException if the protocol was not specified in the base URL.
     */
    public URL build() throws MalformedURLException {

        final Uri.Builder uriBuilder = Uri.parse(mTMDBBaseURL).buildUpon()
                .appendEncodedPath(mEndpoint)
                .appendQueryParameter(API_KEY_QUERY_PARAM, mAPIKey);

        for (Map.Entry<String, String> queryParam : mQueryParams.entrySet()) {
            uriBuilder.appendQueryParameter(queryParam.getKey(), queryParam.getValue());
        }

        return new URL(uriBuilder.build().toString());
    }
}
