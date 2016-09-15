package com.iopho.android.util;

import android.util.Log;

import com.google.common.base.Preconditions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A HttpURLDownloader provides the ability to download contents returned by making a request to an
 * HTTP endpoint.
 *
 * <b>Note</b>: this class's interface is somewhat bound to the {@link HttpURLConnection} class
 * interface. However, this is really a helper component to encapsulate routines of building a
 * {@link HttpURLConnection} request and reading the response into a buffer.
 */
public class HttpURLDownloader {

    private static final String LOG_TAG = HttpURLDownloader.class.getSimpleName();

    private final int mReadTimeout;
    private final int mConnectTimeout;

    /**
     * Construct a new HttpURLDownloader.
     *
     * @param readTimeout read timeout, in milliseconds. A non-zero value specifies the timeout when
     *                    reading from Input stream when a connection is established to a resource.
     *                    If the timeout expires before there is data available for read, a
     *                    {@link IOException} is raised. A timeout of zero is interpreted as an
     *                    infinite timeout.
     * @param connectTimeout specified timeout value, in milliseconds, to be used when opening a
     *                       communications link to the resource. If the timeout expires before the
     *                       connection can be established, a {@link IOException} is raised. A
     *                       timeout of zero is interpreted as an infinite timeout.
     *
     * @see {@link HttpURLConnection#setReadTimeout(int)}
     * @see {@link HttpURLConnection#setConnectTimeout(int)}
     */
    public HttpURLDownloader(final int readTimeout, final int connectTimeout) {

        Preconditions.checkArgument(readTimeout >= 0, "readTimeout must be non-negative.");
        Preconditions.checkArgument(connectTimeout >= 0, "connectTimeout must be non-negative.");

        this.mReadTimeout = readTimeout;
        this.mConnectTimeout = connectTimeout;
    }

    /**
     * Download the contents returned by making an HTTP request to the given {@link URL}.
     *
     * @return a {@link String} representation of the contains.
     * @throws IOException if the underlying HTTP request fails or HttpURLDownloader encounters an
     * error when attempting to parse the response.
     */
    public String downloadURL(final URL url) throws IOException {

        Preconditions.checkNotNull(url, "url must not be null.");

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(mReadTimeout);
            connection.setConnectTimeout(mConnectTimeout);
            connection.connect();

            final int responseCode = connection.getResponseCode();
            inputStream = connection.getInputStream();

            return parseResponse(responseCode, inputStream);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Log.e(LOG_TAG, "Failed to close input stream", ex);
                }
            }
        }
    }

    /**
     * Read the contents accessible by the given {@link InputStream} into a {@link String} buffer.
     *
     * @param responseCode HTTP response code associated with the HTTP response
     * @param inputStream providing a view into the payload from the HTTP response.
     * @return a {@link String} containing the contents of the given {@link InputStream}.
     * @throws IOException if the given response code is not 200 (OK) or if failure occurs when
     * attempting to read from the given {@link InputStream}.
     */
    private String parseResponse(final int responseCode, final InputStream inputStream)
            throws IOException {

        if (responseCode != 200) {
            throw new IOException(String.format(
                    "Response code of 200 expected. Got: %d",
                    responseCode));
        }

        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Log.e(LOG_TAG, "Failed to close buffered reader", ex);
                }
            }
        }
    }
}
