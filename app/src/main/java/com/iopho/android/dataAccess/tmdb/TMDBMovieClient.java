package com.iopho.android.dataAccess.tmdb;

import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;
import com.iopho.android.dataAccess.tmdb.model.Review;
import com.iopho.android.dataAccess.tmdb.model.VideoLink;

import java.util.List;

/**
 * A TMDB (The Movie Database) client.
 *
 * A TMDBMovieClient implements methods to interact with The Movie Database API.
 *
 * @see <a href="https://www.themoviedb.org/">https://www.themoviedb.org</a>
 * @see <a href="http://docs.themoviedb.apiary.io">TMDB API Documentation</a>
 */
public interface TMDBMovieClient {

    /**
     * Queries TMDB to retrieve a page of Movies sorted by rating.
     *
     * @param pageNumber page number of movies to return. Must be in range: [1, 1000].
     * @return a {@link DataPage} of {@link Movie} results from the TMDB TopRated result set.
     * @throws DataAccessRequestException when unable to query TMDB. This can occur for various
     * reasons. The Exception cause is stored.
     * @throws DataAccessParsingException when unable to parse the TMDB response.
     */
    DataPage<Movie> getTopRatedMovies(int pageNumber)
            throws DataAccessRequestException, DataAccessParsingException;

    /**
     * Queries TMDB to retrieve a page of Movies sorted by popularity.
     *
     * @param pageNumber page number of movies to return. Must be in range: [1, 1000].
     * @return a {@link DataPage} of {@link Movie} results from the TMDB Popular result set.
     * @throws DataAccessRequestException when unable to query TMDB. This can occur for various
     * reasons. The Exception cause is stored.
     * @throws DataAccessParsingException when unable to parse the TMDB response.
     */
    DataPage<Movie> getPopularMovies(int pageNumber)
            throws DataAccessRequestException, DataAccessParsingException;

    /**
     * Queries TMDB to retrieve a page of Reviews for a Movie identified by a given ID.
     *
     * @param movieId the unique TMDB identifier of the movie to query reviews for.
     * @param pageNumber page number of movies to return. Must be in range: [1, 1000].
     * @return a {@link DataPage} of {@link Review}s.
     * @throws DataAccessRequestException when unable to query TMDB. This can occur for various
     * reasons. The Exception cause is stored.
     * @throws DataAccessParsingException when unable to parse the TMDB response.
     */
    DataPage<Review> getMovieReviews(long movieId, int pageNumber)
            throws DataAccessRequestException, DataAccessParsingException;

    /**
     *
     *
     * @param movieId
     * @return
     * @throws DataAccessRequestException
     * @throws DataAccessParsingException
     */
    List<VideoLink> getMovieVideoLinks(long movieId)
            throws DataAccessRequestException, DataAccessParsingException;


}
