package com.iopho.android.dataAccess.tmdb;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.tmdb.json.JSONConfigurationTransformer;
import com.iopho.android.dataAccess.tmdb.model.Configuration;
import com.iopho.android.util.HttpURLDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

/**
 * A client to access the TMDB configuration resource.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/#reference/configuration">
 *     http://docs.themoviedb.apiary.io/#reference/configuration</a>
 */
class TMDBConfigurationClient {

    private static final String LOG_TAG = TMDBConfigurationClient.class.getSimpleName();

    private final String mTMDBBaseURL;
    private final String mAPIKey;
    private final HttpURLDownloader mHTTPURLDownloader;

    /**
     * Construct a new TMDBConfigurationClient.
     *
     * @param tmdbBaseURL the TMDB API base URL.
     * @param apiKey the TMDB API KEY. Required to make calls to the TMDB API. See
     *               <a href="https://www.themoviedb.org/documentation/api">
     *                   https://www.themoviedb.org/documentation/api</a>
     * @param httpURLDownloader an HttpURLDownloader to make HTTP requests.
     */
    public TMDBConfigurationClient(final String tmdbBaseURL, final String apiKey,
                                   final HttpURLDownloader httpURLDownloader) {

        Preconditions.checkNotNull(tmdbBaseURL, "tmdbBaseURL must not be null.");
        Preconditions.checkNotNull(apiKey, "apiKey must not be null.");
        Preconditions.checkNotNull(httpURLDownloader, "httpURLDownloader must not be null.");

        this.mTMDBBaseURL = tmdbBaseURL;
        this.mAPIKey = apiKey;
        this.mHTTPURLDownloader = httpURLDownloader;
    }

    /**
     * Retrieve remote TMDB API configuration.
     *
     * @return a {@link Configuration} instance holding TMDB configuration values.
     * @throws DataAccessRequestException when unable to query TMDB. This can occur for various
     *                                    reasons. The Exception cause is stored.
     * @throws DataAccessParsingException when unable to parse the TMDB response.
     */
    public Configuration getConfiguration() throws DataAccessRequestException,
            DataAccessParsingException {

        try  {

            final URL url = new TMDBURLBuilder(
                    mTMDBBaseURL, mAPIKey, TMDBURLBuilder.Endpoint.CONFIGURATION)
                    .build();

            Log.d(LOG_TAG, "Attempting to download content at URI: " + url.toString());

            final String responseContent = mHTTPURLDownloader.downloadURL(url);

            final JSONObject contentJSONObject = new JSONObject(responseContent);

            final JSONConfigurationTransformer jsonConfigurationTransformer =
                    new JSONConfigurationTransformer();

            Log.d(LOG_TAG, "Successfully downloaded content at URI: " + url.toString());

            return jsonConfigurationTransformer.transform(contentJSONObject);

        } catch (MalformedURLException ex) {
            throw new DataAccessRequestException("Failed to build request URL.", ex);
        } catch (IOException ex) {
            throw new DataAccessRequestException("Failed to download URL content.", ex);
        } catch (JSONException | ParseException ex) {
            throw new DataAccessParsingException("Failed to parse response content.", ex);
        }
    }
}
