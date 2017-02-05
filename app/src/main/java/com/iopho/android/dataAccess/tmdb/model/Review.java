package com.iopho.android.dataAccess.tmdb.model;

import com.google.common.base.Preconditions;

public class Review {

    private final long mID;
    private final String mAuthor;
    private final String mContent;

    /**
     * Constructs a new movie Review.
     *
     * @param id unique TMDB review identifier.
     * @param author name of the reviewer.
     * @param content review content.
     */
    public Review(final long id,
                  final String author,
                  final String content) {

        Preconditions.checkNotNull(author, "author must not be null.");
        Preconditions.checkNotNull(content, "content must not be null.");

        this.mID = id;
        this.mAuthor = author;
        this.mContent = content;
    }

    /**
     * @return unique TMDB review identifier.
     */
    public long getID() {
        return mID;
    }

    /**
     * @return name of the reviewer.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * @return review content.
     */
    public String getContent() {
        return mContent;
    }

    @Override
    public String toString() {
        return "[ ID=" + mID +
                ", Author=" + mAuthor +
                ", Content=" + mContent +
                " ]";
    }
}
