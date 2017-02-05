package com.iopho.android.dataAccess.tmdb;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.json.JSONDataPageTransformer;
import com.iopho.android.dataAccess.tmdb.json.JSONMovieTransformer;
import com.iopho.android.dataAccess.tmdb.json.JSONResultListTransformer;
import com.iopho.android.dataAccess.tmdb.json.JSONReviewTransformer;
import com.iopho.android.dataAccess.tmdb.json.JSONToObjectTransformer;
import com.iopho.android.dataAccess.tmdb.json.JSONVideoLinkTransformer;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;
import com.iopho.android.dataAccess.tmdb.model.Review;
import com.iopho.android.dataAccess.tmdb.model.VideoLink;
import com.iopho.android.util.HttpURLDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

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
     * @param tmdbBaseURL the TMDB API base URL.
     * @param apiKey the TMDB API KEY. Required to make calls to the TMDB API. See
     *               <a href="https://www.themoviedb.org/documentation/api">
     *                   https://www.themoviedb.org/documentation/api</a>
     * @param httpURLDownloader an HttpURLDownloader to make HTTP requests.
     */
    TMDBMovieClientImpl(final String tmdbBaseURL, final String apiKey,
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

        Preconditions.checkArgument(pageNumber >= 1 && pageNumber <= 1000,
                "pageNumber must be in range [1, 1000].");

        try {

            final URL url = new TMDBURLBuilder(
                    mTMDBBaseURL, mAPIKey, TMDBURLBuilder.Endpoint.MOVIES_TOP_RATED)
                    .withQueryParam(TMDBURLBuilder.QueryParamKey.PAGE, String.valueOf(pageNumber))
                    .build();

            final JSONDataPageTransformer<Movie> jsonMovieDataPageTransformer =
                    new JSONDataPageTransformer<>(new JSONMovieTransformer());

            return queryTMDB(url, jsonMovieDataPageTransformer);

        } catch (final MalformedURLException ex) {
            throw new DataAccessRequestException("Failed to build request URL.", ex);
        }
    }

    /**
     * @see {@link TMDBMovieClient#getPopularMovies(int)}
     */
    @Override
    public DataPage<Movie> getPopularMovies(final int pageNumber)
            throws DataAccessRequestException, DataAccessParsingException {

        Preconditions.checkArgument(pageNumber >= 1 && pageNumber <= 1000,
                "pageNumber must be in range [1, 1000].");

        try {

            final URL url = new TMDBURLBuilder(
                    mTMDBBaseURL, mAPIKey, TMDBURLBuilder.Endpoint.MOVIES_POPULAR)
                    .withQueryParam(TMDBURLBuilder.QueryParamKey.PAGE, String.valueOf(pageNumber))
                    .build();

            final JSONDataPageTransformer<Movie> jsonMovieDataPageTransformer =
                    new JSONDataPageTransformer<>(new JSONMovieTransformer());

            return queryTMDB(url, jsonMovieDataPageTransformer);

        } catch (final MalformedURLException ex) {
            throw new DataAccessRequestException("Failed to build request URL.", ex);
        }
    }

    /**
     * @see {@link TMDBMovieClient#getMovieReviews(long, int)}
     */
    @Override
    public DataPage<Review> getMovieReviews(final long movieId, final int pageNumber)
            throws DataAccessRequestException, DataAccessParsingException {

        Preconditions.checkArgument(pageNumber >= 1 && pageNumber <= 1000,
                "pageNumber must be in range [1, 1000].");

        try {

            final URL url = new TMDBURLBuilder(
                    mTMDBBaseURL, mAPIKey, TMDBURLBuilder.Endpoint.MOVIE_REVIEWS)
                    .withRecordId(movieId)
                    .withQueryParam(TMDBURLBuilder.QueryParamKey.PAGE, String.valueOf(pageNumber))
                    .build();

            final JSONDataPageTransformer<Review> jsonReviewDataPageTransformer =
                    new JSONDataPageTransformer<>(new JSONReviewTransformer());

            return queryTMDB(url, jsonReviewDataPageTransformer);

        } catch (final MalformedURLException ex) {
            throw new DataAccessRequestException("Failed to build request URL.", ex);
        }
    }

    /**
     * @see {@link TMDBMovieClient#getMovieVideoLinks(long)}
     */
    @Override
    public List<VideoLink> getMovieVideoLinks(final long movieId)
            throws DataAccessRequestException, DataAccessParsingException {

        try {

            final URL url = new TMDBURLBuilder(
                    mTMDBBaseURL, mAPIKey, TMDBURLBuilder.Endpoint.MOVIE_VIDEOS)
                    .withRecordId(movieId)
                    .build();

            final JSONResultListTransformer<VideoLink> jsonResultListTransformer =
                    new JSONResultListTransformer(new JSONVideoLinkTransformer());

            return queryTMDB(url, jsonResultListTransformer);

        } catch (final MalformedURLException ex) {
            throw new DataAccessRequestException("Failed to build request URL.", ex);
        }
    }

    private <T> T queryTMDB(final URL url, final JSONToObjectTransformer<T> jsonToObjectTransformer)
            throws DataAccessRequestException, DataAccessParsingException  {

        try {
            Log.d(LOG_TAG, "Attempting to download content at URI: " + url.toString());

            final String responseContent = mHTTPURLDownloader.downloadURL(url);

            final JSONObject contentJSONObject = new JSONObject(responseContent);

            Log.d(LOG_TAG, "Successfully downloaded content at URI: " + url.toString());

            return jsonToObjectTransformer.transform(contentJSONObject);
        } catch (final IOException ex) {
            throw new DataAccessRequestException("Failed to download URL content.", ex);
        } catch (final JSONException | ParseException ex) {
            throw new DataAccessParsingException("Failed to parse response content.", ex);
        }
    }
}
