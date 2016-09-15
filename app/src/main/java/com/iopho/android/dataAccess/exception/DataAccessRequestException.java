package com.iopho.android.dataAccess.exception;

/**
 * A DataAccessRequestException indicates an error when attempting to request data from a data
 * source.
 */
public class DataAccessRequestException extends Exception {

    /**
     * Construct a new DataAccessRequestException.
     *
     * @see {@link Exception#Exception(String, Throwable)}
     */
    public DataAccessRequestException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
