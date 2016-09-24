package com.iopho.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.common.base.Preconditions;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A helper class that provides helper IO routines (e.g. reading and writing from local files).
 */
public class IOHelper {

    private final static String LOG_TAG = IOHelper.class.getSimpleName();

    /**
     * Reads the contents of a resource file into a {@link String}.
     *
     * @param context the current Context.
     * @param resourceId resource file ID.
     * @return a {@link String} containing the resource file contents.
     * @throws IOException when failing to read the resource file specified by the given
     * resource ID.
     */
    public static String readResourceToString(final Context context, final int resourceId)
            throws IOException {

        Preconditions.checkNotNull(context, "context must not be null.");

        final Resources resources = context.getResources();

        InputStream resourceIn = null;

        try {
            resourceIn = resources.openRawResource(resourceId);
            return readInputToString(resourceIn);
        } finally {
            silentlyCloseResource(resourceIn);
        }
    }

    /**
     * Reads the contents of a {@link File} into a {@link String}.
     *
     * @param file the file to open for reading.
     * @return a {@link String} containing the contents of the file.
     * @throws IOException when failing to read the given {@link File}.
     * @throws FileNotFoundException if the given {@link File} does not exist.
     */
    public static String readFileToString(final File file) throws IOException {

        Preconditions.checkNotNull(file, "file must not be null.");

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            return readInputToString(fileInputStream);
        } catch (FileNotFoundException ex) {
            Log.e(LOG_TAG, "File not found: " + file.getAbsolutePath(), ex);
            throw ex;
        } finally {
            silentlyCloseResource(fileInputStream);
        }
    }

    /**
     * Writes the given {@link String} into the given {@link File}.
     *
     * @param contents text to output to the file.
     * @param file the file to be opened for writing.
     * @param append if true, the contents will be written to the end of the file rather than the
     *               beginning.
     * @throws IOException when failing to write to the file.
     */
    public static void writeStringToFile(final String contents, final File file, boolean append)
            throws IOException {

        Preconditions.checkNotNull(contents, "contents must not be null.");
        Preconditions.checkNotNull(file, "file must not be null.");

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file, append);
            fileOutputStream.write(contents.getBytes());
        } finally {
            silentlyCloseResource(fileOutputStream);
        }
    }

    /**
     * Attempts to close a {@link Closeable}. If an I/O error occurs when attempting to close the
     * resource we silently fail and log the error.
     *
     * The given {@link Closeable} can be null in which case no action is performed.
     *
     * @param closeableResource the resource to close.
     */
    public static void silentlyCloseResource(final Closeable closeableResource) {

        if (closeableResource != null) {
            try {
                closeableResource.close();
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Failed to close resource.", ex);
            }
        }
    }

    /*
     * Read the contents of an InputStream into a String.
     */
    private static String readInputToString(final InputStream inputStream) throws IOException {

        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            silentlyCloseResource(reader);
        }

        return result.toString();
    }
}
