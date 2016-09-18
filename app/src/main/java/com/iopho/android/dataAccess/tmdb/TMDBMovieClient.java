package com.iopho.android.dataAccess.tmdb;


import com.iopho.android.dataAccess.exception.DataAccessRequestException;
import com.iopho.android.dataAccess.exception.DataAccessParsingException;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;

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
     * @return a DataPage of Movie results from the TMDB TopRated result set.
     * @throws DataAccessRequestException when unable to query TMDB. This can occur for various
     *                                    reasons. The Exception cause is stored.
     * @throws DataAccessParsingException when unable to parse the TMDB response.
     */
    DataPage<Movie> getTopRatedMovies(int pageNumber)
            throws DataAccessRequestException, DataAccessParsingException;
}
