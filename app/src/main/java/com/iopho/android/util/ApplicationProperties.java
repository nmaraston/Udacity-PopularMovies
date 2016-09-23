package com.iopho.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * ApplicationProperties provides a general interface for reading key-values from a .properties
 * resource file.
 */
public class ApplicationProperties {

    private static final String LOG_TAG = ApplicationProperties.class.getSimpleName();

    private final Properties mProperties;

    /**
     * Construct a new ApplicationProperties instance.
     *
     * @param context the {@link Context} containing the
     * @param configFileId resource ID for the .properties resource file to read in key-value
     *                     properties from.
     * @throws IOException if unable to open/read the .properties file indicated by
     * <b>configFileId</b>
     */
    public ApplicationProperties(final Context context, final int configFileId) throws IOException {

        Preconditions.checkNotNull(context, "context must not be null.");

        final Resources resources = context.getResources();

        try {
            final InputStream resourceIn = resources.openRawResource(configFileId);
            mProperties = new Properties();
            mProperties.load(resourceIn);
        } catch (Resources.NotFoundException ex) {
            Log.e(LOG_TAG, "Unable to find resource for ID " + configFileId);
            throw ex;
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Failed to read resource file for ID " + configFileId);
            throw ex;
        }
    }

    /**
     * The value of the property associated with the given key. Returns null if the property key is
     * not found.
     *
     * @param key the property key
     * @return the value associated with the given property key or null if non exists.
     */
    public String getStringPropertyValue(final String key) {

        Preconditions.checkNotNull(key, "key must not be null.");
        return mProperties.getProperty(key);
    }
}
