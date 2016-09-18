package com.iopho.android.dataAccess.tmdb.model;


import com.google.common.base.Preconditions;

import java.util.List;

/**
 * A DataPage is a container for a page of results returned by the TMDB API.
 *
 * Page metadata - page number - and total result set metadata - total page/result count - are
 * stored to allow for pagination of the result set.
 *
 * @param <T> the type of results contained in this DataPage.
 */
public class DataPage<T> {

    private final int mPageNumber;
    private final int mTotalPageCount;
    private final int mTotalResultCount;
    private List<T> mResults;

    /**
     * Construct a new DataPage.
     *
     * @param pageNumber the page number for this page. Must be a positive number and be less than
     *                   totalPageCount.
     * @param totalPageCount the total number of pages available. Must be a positive number.
     * @param totalResultCount the total number of results available. Must be a non-negative number.
     * @param results list of results included in this page.
     */
    public DataPage(final int pageNumber,
                    final int totalPageCount,
                    final int totalResultCount,
                    final List<T> results) {

        Preconditions.checkArgument(pageNumber >= 1, "pageNumber should be positive.");
        Preconditions.checkArgument(totalPageCount >=1, "totalPageCount should be positive.");
        Preconditions.checkArgument(pageNumber <= totalPageCount,
                "pageNumber must be less than or equal to totalPageCount");
        Preconditions.checkArgument(totalResultCount >= 0,
                "totalResultCount should be non-negative.");
        Preconditions.checkNotNull(results, "results must be non-null.");

        this.mPageNumber = pageNumber;
        this.mTotalPageCount = totalPageCount;
        this.mTotalResultCount = totalResultCount;
        this.mResults = results;
    }

    /**
     * @return the page number for this page. The page number is always a positive number (>0) and
     * less than the value returned by {@link #getTotalPageCount()} method.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    /**
     * @return the total number of pages that exist for the entire result set.
     */
    public int getTotalPageCount() {
        return mTotalPageCount;
    }

    /**
     * @return the total number of results that exist for the entire result set.
     */
    public int getTotalResultCount() {
        return mTotalResultCount;
    }

    /**
     * @return the results contained in this page.
     */
    public List<T> getResults() {
        return mResults;
    }

    @Override
    public String toString() {
        return "[ PageNumber=" + mPageNumber +
                ", TotalPageCount=" + mTotalPageCount +
                ", TotalResultCount=" + mTotalResultCount +
                ", Results=" + mResults +
                " ]";
    }
}
