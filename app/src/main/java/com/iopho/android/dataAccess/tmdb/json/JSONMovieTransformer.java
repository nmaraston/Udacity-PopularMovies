package com.iopho.android.dataAccess.tmdb.json;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements a {@link JSONToObjectTransformer} to transform a given {@link JSONObject} into a
 * {@link Movie}.
 */
public class JSONMovieTransformer implements JSONToObjectTransformer<Movie> {

    private enum JSON_KEY {
        POSTER_PATH        ("poster_path"),
        ADULT              ("adult"),
        OVERVIEW           ("overview"),
        RELEASE_DATE       ("release_date"),
        GENRE_IDS          ("genre_ids"),
        ID                 ("id"),
        ORIGINAL_TITLE     ("original_title"),
        ORIGINAL_LANGUAGE  ("original_language"),
        TITLE              ("title"),
        BACKDROP_PATH      ("backdrop_path"),
        POPULARITY         ("popularity"),
        VOTE_COUNT         ("vote_count"),
        VIDEO              ("video"),
        VOTE_AVERAGE       ("vote_average");

        private final String mKeyName;

        JSON_KEY(final String keyName) {
            this.mKeyName = keyName;
        }

        public String getKeyName() {
            return mKeyName;
        }
    }

    private static final String TMDB_MOVIE_DATE_FORMAT = "yyyy-MM-dd";


    /**
     * @see {@link JSONToObjectTransformer#transform(JSONObject)}
     *
     * @throws JSONException when an error occurs when attempting to read a key's value or a
     *                       required key is missing.
     * @throws ParseException
     */
    @Override
    public Movie transform(final JSONObject jsonObject) throws JSONException, ParseException {

        Preconditions.checkNotNull(jsonObject, "jsonObject must not be null.");

        final String posterPath = jsonObject.getString(JSON_KEY.POSTER_PATH.getKeyName());
        final boolean isAdult = jsonObject.getBoolean(JSON_KEY.ADULT.getKeyName());
        final String overview = jsonObject.getString(JSON_KEY.OVERVIEW.getKeyName());
        final long id = jsonObject.getLong(JSON_KEY.ID.getKeyName());
        final String originalTitle = jsonObject.getString(JSON_KEY.ORIGINAL_TITLE.getKeyName());
        final String originalLanguage = jsonObject.getString(JSON_KEY.ORIGINAL_LANGUAGE.getKeyName());
        final String title = jsonObject.getString(JSON_KEY.TITLE.getKeyName());
        final String backdropPath = jsonObject.getString(JSON_KEY.BACKDROP_PATH.getKeyName());
        final double popularity = jsonObject.getDouble(JSON_KEY.POPULARITY.getKeyName());
        final long voteCount = jsonObject.getLong(JSON_KEY.VOTE_COUNT.getKeyName());
        final boolean hasVideo = jsonObject.getBoolean(JSON_KEY.VIDEO.getKeyName());
        final double averageVote = jsonObject.getDouble(JSON_KEY.VOTE_AVERAGE.getKeyName());

        final Date releaseDate = parseReleaseDate(jsonObject);
        final long[] genreIDs = parseGenreIDs(jsonObject);

        return new Movie(posterPath, isAdult, overview, releaseDate, genreIDs, id, originalTitle,
                originalLanguage, title, backdropPath, popularity, voteCount, hasVideo,
                averageVote);
    }

    /**
     * Parse the {@link JSON_KEY#RELEASE_DATE} key value from a given {@link JSONObject}. It is
     * expected that the date format matches {@link JSONMovieTransformer#TMDB_MOVIE_DATE_FORMAT}.
     *
     * @param jsonObject the given {@link JSONObject} to read the {@link JSON_KEY#RELEASE_DATE} key
     *                   value from.
     * @return a {@link Date} representing the movie's release date.
     * @throws JSONException when an error occurs when attempting to read the
     * {@link JSON_KEY#RELEASE_DATE} key or the key is missing.
     * @throws ParseException when the date format is unexpected.
     */
    private Date parseReleaseDate(final JSONObject jsonObject)
            throws JSONException, ParseException {

        final String releaseDateStr = jsonObject.getString(JSON_KEY.RELEASE_DATE.getKeyName());
        final DateFormat dateFormat = new SimpleDateFormat(TMDB_MOVIE_DATE_FORMAT);

        return dateFormat.parse(releaseDateStr);
    }

    /**
     * Parse the {@link JSON_KEY#GENRE_IDS} key value from a given {@link JSONObject}.
     *
     * @param jsonObject the given {@link JSONObject} to read the {@link JSON_KEY#GENRE_IDS} key
     *                   value from.
     * @return an Array of genre IDs stored as long ints.
     * @throws JSONException when an error occurs when attempting to read the
     * {@link JSON_KEY#GENRE_IDS} or the key is not present.
     */
    private long[] parseGenreIDs(final JSONObject jsonObject) throws JSONException {

        final JSONArray jsonArray = jsonObject.getJSONArray(JSON_KEY.GENRE_IDS.getKeyName());
        final long[] genreIDs = new long[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            genreIDs[i] = jsonArray.getLong(i);
        }

        return genreIDs;
    }
}
