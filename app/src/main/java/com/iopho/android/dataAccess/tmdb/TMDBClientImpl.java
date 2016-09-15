package com.iopho.android.dataAccess.tmdb;

import android.net.Uri;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.json.JSONDataPageTransformer;
import com.iopho.android.dataAccess.tmdb.json.JSONMovieTransformer;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;
import com.iopho.android.util.HttpURLDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

/**
 * An implementation of a {@link TMDBClient}
 */
public class TMDBClientImpl implements TMDBClient {

    private final static String LOG_TAG = TMDBClientImpl.class.getSimpleName();

    private final String TMDB_BASE_URL = "http://api.themoviedb.org/3/";

    private enum TMDB_END_POINT {

        POPULAR   ("movie/popular"),
        TOP_RATED ("movie/top_rated");

        private String endpointName;

        TMDB_END_POINT(final String endpointName) {
            this.endpointName = endpointName;
        }

        public String getEndpointName() {
            return endpointName;
        }
    }

    private enum TMDB_QUERY_PARAM {

        PAGE    ("page"),
        API_KEY ("api_key");

        private String name;

        TMDB_QUERY_PARAM(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final String mAPIKey;
    private final HttpURLDownloader mHTTPURLDownloader;

    /**
     * Construct a new TMDBClientImpl.
     *
     * @param apiKey the TMDB API KEY. Required to make calls to the TMDB API. See
     *               <a href="https://www.themoviedb.org/documentation/api">
     *                   https://www.themoviedb.org/documentation/api</a>
     * @param httpURLDownloader an HttpURLDownloader to make HTTP requests.
     */
    public TMDBClientImpl(final String apiKey, final HttpURLDownloader httpURLDownloader) {

        Preconditions.checkNotNull(apiKey, "apiKey must not be null.");
        Preconditions.checkNotNull(httpURLDownloader, "httpURLDownloader must not be null.");

        this.mAPIKey = apiKey;
        this.mHTTPURLDownloader = httpURLDownloader;
    }

    /**
     * @see {@link TMDBClient#getTopRatedMovies(int)}
     */
    @Override
    public DataPage<Movie> getTopRatedMovies(final int pageNumber)
            throws DataAccessRequestException, DataAccessParsingException {

        Preconditions.checkArgument(pageNumber >= 1, "pageNumber must be positive.");

        final Uri requestUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendEncodedPath(TMDB_END_POINT.TOP_RATED.getEndpointName())
                .appendQueryParameter(TMDB_QUERY_PARAM.PAGE.getName(), String.valueOf(pageNumber))
                .appendQueryParameter(TMDB_QUERY_PARAM.API_KEY.getName(), mAPIKey)
                .build();

        Log.d(LOG_TAG, "Attempting to download content at URI: " + requestUri.toString());

        try  {

            final URL url = new URL(requestUri.toString());
            final String responseContent = mHTTPURLDownloader.downloadURL(url);

            final JSONObject contentJSONObject = new JSONObject(responseContent);

            final JSONDataPageTransformer<Movie> jsonDataPageTransformer =
                    new JSONDataPageTransformer<>(new JSONMovieTransformer());

            return jsonDataPageTransformer.transform(contentJSONObject);

        } catch (MalformedURLException ex) {
            throw new DataAccessRequestException("Failed to build request URL.", ex);
        } catch (IOException ex) {
            throw new DataAccessRequestException("Failed to download URL content.", ex);
        } catch (JSONException | ParseException ex) {
            throw new DataAccessParsingException("Failed to parse response content.", ex);
        }
    }
}
