package com.iopho.android.dataAccess.exception;

/**
 * A DataAccessParsingException indicates an error when attempting to parse a response returned
 * from requesting data from a data store.
 */
public class DataAccessParsingException extends Exception {

    /**
     * Construct a new DataAccessParsingException.
     *
     * @see {@link Exception#Exception(String, Throwable)}
     */
    public DataAccessParsingException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}

