package com.iopho.android.dataAccess.tmdb.model;


import com.google.common.base.Preconditions;

import java.util.Date;

public class Movie {

    private final String mPosterPath;
    private final boolean mIsAdult;
    private final String mOverview;
    private final Date mReleaseDate;
    private final long[] mGenreIDs;
    private final long mID;
    private final String mOriginalTitle;
    private final String mOriginalLanguageCode;
    private final String mTitle;
    private final String mBackdropPath;
    private final double mPopularity;
    private final long mVoteCount;
    private final boolean mHasVideo;
    private final double mAverageVote;

    /**
     * Constructs a new Movie.
     *
     * @param posterPath relative path to the movie poster.
     * @param isAdult true iff the movie is adult only.
     * @param overview small description of the movie.
     * @param releaseDate Date of release.
     * @param genreIDs array of TMDB Genre IDs that this movie is tagged with.
     * @param id unique TMDB movie identifier.
     * @param originalTitle the untranslated original movie title.
     * @param originalLanguageCode the ISO 639-1 language code representing the name of movie's
     *                             original language.
     * @param title translated title of this movie.
     * @param backdropPath relative path to the movie's backdrop image.
     * @param popularity TMDB popularity score of the movie.
     * @param voteCount number of votes accumulated for this movie.
     * @param hasVideo true iff TMDB has available videos (trailers, teasers, clips, etc).
     * @param averageVote average vote score.
     */
    public Movie(final String posterPath,
                 final boolean isAdult,
                 final String overview,
                 final Date releaseDate,
                 final long[] genreIDs,
                 final long id,
                 final String originalTitle,
                 final String originalLanguageCode,
                 final String title,
                 final String backdropPath,
                 final double popularity,
                 final long voteCount,
                 final boolean hasVideo,
                 final double averageVote) {

        Preconditions.checkNotNull(posterPath, "postPath must not be null.");
        Preconditions.checkNotNull(overview, "overview must not be null.");
        Preconditions.checkNotNull(releaseDate, "releaseDate must not be null.");
        Preconditions.checkNotNull(genreIDs, "genreIDs must not be null.");
        Preconditions.checkArgument(genreIDs.length > 0, "genreIDs must not be empty.");
        Preconditions.checkNotNull(originalTitle, "originalTitle must not be null.");
        Preconditions.checkNotNull(originalLanguageCode, "originalLanguage must not be null.");
        Preconditions.checkNotNull(title, "title must not be null.");
        Preconditions.checkNotNull(backdropPath, "backdropPath must not be null.");
        Preconditions.checkArgument(voteCount >= 0, "voteCount must be non-negative.");

        this.mPosterPath = posterPath;
        this.mIsAdult = isAdult;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
        this.mGenreIDs = genreIDs;
        this.mID = id;
        this.mOriginalTitle = originalTitle;
        this.mOriginalLanguageCode = originalLanguageCode;
        this.mTitle = title;
        this.mBackdropPath = backdropPath;
        this.mPopularity = popularity;
        this.mVoteCount = voteCount;
        this.mHasVideo = hasVideo;
        this.mAverageVote = averageVote;
    }

    /**
     * @return relative path to the movie poster image.
     */
    public String getPosterPath() {
        return mPosterPath;
    }

    /**
     * @return true iff the movie is adult only.
     */
    public boolean isAdult() {
        return mIsAdult;
    }

    /**
     * @return an overview (small text description) of the movie.
     */
    public String getOverview() {
        return mOverview;
    }

    /**
     * @return Date of the films release.
     */
    public Date getReleaseDate() {
        return mReleaseDate;
    }

    /**
     * @return Array of TMDB Genre identifiers that this Movie is tagged with.
     */
    public long[] getGenreIDs() {
        return mGenreIDs;
    }

    /**
     * @return unique TMDB movie identifier.
     */
    public long getID() {
        return mID;
    }

    /**
     * @return the untranslated original movie title.
     */
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    /**
     * @return the ISO 639-1 alpha-2 language code representing the name of movie's original
     *         language.
     */
    public String getOriginalLanguageCode() {
        return mOriginalLanguageCode;
    }

    /**
     * @return translated title of this movie.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return relative path to the movie's backdrop image.
     */
    public String getBackdropPath() {
        return mBackdropPath;
    }

    /**
     * @return TMDB popularity score for this movie.
     */
    public double getPopularity() {
        return mPopularity;
    }

    /**
     * @return total number of votes accumulated for this movie.
     */
    public long getVoteCount() {
        return mVoteCount;
    }

    /**
     * @return true iff TMDB has available videos (trailers, teasers, clips, etc)
     */
    public boolean hasVideo() {
        return mHasVideo;
    }

    /**
     * @return average vote score.
     */
    public double getAverageVote() {
        return mAverageVote;
    }

    @Override
    public String toString() {
        return "[ PosterPath=" + mPosterPath +
                ", IsAdult=" + mIsAdult +
                ", Overview=" + mOverview +
                ", ReleaseDate=" + mReleaseDate +
                ", GenreIDs=" + mGenreIDs +
                ", ID=" + mID +
                ", OriginalTitle=" + mOriginalTitle +
                ", OriginalLanguage=" + mOriginalLanguageCode +
                ", Title=" + mTitle +
                ", Popularity=" + mPopularity +
                ", VoteCount=" + mVoteCount +
                ", HasVideo=" + mHasVideo +
                ", AverageVote=" + mAverageVote +
                " ]";
    }
}
