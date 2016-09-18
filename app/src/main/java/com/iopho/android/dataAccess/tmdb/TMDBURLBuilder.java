package com.iopho.android.dataAccess.tmdb;

import android.net.Uri;

import com.google.common.base.Preconditions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A TMDBURLBuilder is a Builder to assist in the construction of URLs for requests to the TMDB API.
 */
class TMDBURLBuilder {

    /**
     * Enum of possible TMDB API endpoints used to interact with different resources.
     */
    public enum EndPoint {

        CONFIGURATION    ("configuration"),

        MOVIES_POPULAR   ("movie/popular"),
        MOVIES_TOP_RATED ("movie/top_rated");

        private String endpointName;

        EndPoint(final String endpointName) {
            this.endpointName = endpointName;
        }

        public String getEndpointName() {
            return endpointName;
        }
    }

    /**
     * Enum of possible TMDB query params key names.
     */
    public enum QueryParamKey {

        PAGE    ("page");

        private String name;

        QueryParamKey(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // Do not include this query param in the enum above. The interface enforces that it be
    // provided via the constructor.
    private static final String API_KEY_QUERY_PARAM = "api_key";

    private final String mTMDBBaseURL;
    private final String mAPIKey;
    private final EndPoint mEndpoint;
    private final Map<QueryParamKey, String> mQueryParams;

    /**
     * Construct a new TMDBURLBuilder.
     *
     * @param tmdbBaseURL the base URL for the TMDB API.
     * @param apiKey a TMDB API key (required query param for all requests).
     */
    public TMDBURLBuilder(final String tmdbBaseURL, final String apiKey,
                          final EndPoint endpoint) {

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
    public TMDBURLBuilder withQueryParam(final QueryParamKey key, final String value) {

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
                .appendEncodedPath(mEndpoint.getEndpointName())
                .appendQueryParameter(API_KEY_QUERY_PARAM, mAPIKey);

        for (Map.Entry<QueryParamKey, String> queryParam : mQueryParams.entrySet()) {
            uriBuilder.appendQueryParameter(queryParam.getKey().getName(), queryParam.getValue());
        }

        return new URL(uriBuilder.build().toString());
    }
}
