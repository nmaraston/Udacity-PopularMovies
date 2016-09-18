package com.iopho.android.dataAccess.tmdb;

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
 * An implementation of a {@link TMDBMovieClient}
 */
public class TMDBMovieClientImpl implements TMDBMovieClient {

    private static final String LOG_TAG = TMDBMovieClientImpl.class.getSimpleName();

    private final String mTMDBBaseURL;
    private final String mAPIKey;
    private final HttpURLDownloader mHTTPURLDownloader;

    /**
     * Construct a new TMDBMovieClientImpl.
     *
     * @param apiKey the TMDB API KEY. Required to make calls to the TMDB API. See
     *               <a href="https://www.themoviedb.org/documentation/api">
     *                   https://www.themoviedb.org/documentation/api</a>
     * @param httpURLDownloader an HttpURLDownloader to make HTTP requests.
     */
    public TMDBMovieClientImpl(final String tmdbBaseURL, final String apiKey,
                               final HttpURLDownloader httpURLDownloader) {

        Preconditions.checkNotNull(tmdbBaseURL, "tmdbBaseURL must not be null.");
        Preconditions.checkNotNull(apiKey, "apiKey must not be null.");
        Preconditions.checkNotNull(httpURLDownloader, "httpURLDownloader must not be null.");

        this.mTMDBBaseURL = tmdbBaseURL;
        this.mAPIKey = apiKey;
        this.mHTTPURLDownloader = httpURLDownloader;
    }

    /**
     * @see {@link TMDBMovieClient#getTopRatedMovies(int)}
     */
    @Override
    public DataPage<Movie> getTopRatedMovies(final int pageNumber)
            throws DataAccessRequestException, DataAccessParsingException {

        Preconditions.checkArgument(pageNumber >= 1, "pageNumber must be positive.");

        try  {

            final URL url = new TMDBURLBuilder(
                    mTMDBBaseURL, mAPIKey, TMDBURLBuilder.END_POINT.MOVIES_TOP_RATED)
                    .withQueryParam(TMDBURLBuilder.QUERY_PARAM_KEY.PAGE, String.valueOf(pageNumber))
                    .build();

            Log.d(LOG_TAG, "Attempting to download content at URI: " + url.toString());

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