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

    private static class JSON_KEY {
        public static final String POSTER_PATH       = "poster_path";
        public static final String ADULT             = "adult";
        public static final String OVERVIEW          = "overview";
        public static final String RELEASE_DATE      = "release_date";
        public static final String GENRE_IDS         = "genre_ids";
        public static final String ID                = "id";
        public static final String ORIGINAL_TITLE    = "original_title";
        public static final String ORIGINAL_LANGUAGE = "original_language";
        public static final String TITLE             = "title";
        public static final String BACKDROP_PATH     = "backdrop_path";
        public static final String POPULARITY        = "popularity";
        public static final String VOTE_COUNT        = "vote_count";
        public static final String VIDEO             = "video";
        public static final String VOTE_AVERAGE      = "vote_average";
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

        final String posterPath = jsonObject.getString(JSON_KEY.POSTER_PATH);
        final boolean isAdult = jsonObject.getBoolean(JSON_KEY.ADULT);
        final String overview = jsonObject.getString(JSON_KEY.OVERVIEW);
        final long id = jsonObject.getLong(JSON_KEY.ID);
        final String originalTitle = jsonObject.getString(JSON_KEY.ORIGINAL_TITLE);
        final String originalLanguage = jsonObject.getString(JSON_KEY.ORIGINAL_LANGUAGE);
        final String title = jsonObject.getString(JSON_KEY.TITLE);
        final String backdropPath = jsonObject.getString(JSON_KEY.BACKDROP_PATH);
        final double popularity = jsonObject.getDouble(JSON_KEY.POPULARITY);
        final long voteCount = jsonObject.getLong(JSON_KEY.VOTE_COUNT);
        final boolean hasVideo = jsonObject.getBoolean(JSON_KEY.VIDEO);
        final double averageVote = jsonObject.getDouble(JSON_KEY.VOTE_AVERAGE);

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

        final String releaseDateStr = jsonObject.getString(JSON_KEY.RELEASE_DATE);
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

        final JSONArray jsonArray = jsonObject.getJSONArray(JSON_KEY.GENRE_IDS);
        final long[] genreIDs = new long[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            genreIDs[i] = jsonArray.getLong(i);
        }

        return genreIDs;
    }
}
