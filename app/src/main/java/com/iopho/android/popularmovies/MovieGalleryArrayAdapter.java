package com.iopho.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.common.base.Preconditions;
import com.iopho.android.dataAccess.tmdb.TMDBAssetURLFactory;
import com.iopho.android.dataAccess.tmdb.model.ImageSize;
import com.iopho.android.dataAccess.tmdb.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieGalleryArrayAdapter extends ArrayAdapter<Movie> {

    private final TMDBAssetURLFactory mTMDBAssetURLFactory;

    /**
     * Construct a new MovieGalleryArrayAdapter.
     *
     * @param tmdbAssetURLFactory to build TMDB image asset URLs.
     */
    public MovieGalleryArrayAdapter(final Activity context, final List<Movie> movies,
                                    final TMDBAssetURLFactory tmdbAssetURLFactory) {
        super(context, 0, movies);

        Preconditions.checkNotNull(tmdbAssetURLFactory, "tmdbAssetURLFactory must not be null.");
        this.mTMDBAssetURLFactory = tmdbAssetURLFactory;
    }

    /**
     * @see {@link ArrayAdapter#getView(int, View, ViewGroup)}
     */
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        final Movie movie = getItem(position);
        View resultView = convertView;

        if (resultView == null) {
            resultView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_movie_poster, parent, false);
            resultView.setTag(new ViewHolder(resultView));
        }

        final ViewHolder viewHolder = (ViewHolder)resultView.getTag();
        if (viewHolder == null) {
            throw new IllegalStateException(
                    "convertView.getTag() did not return expected ViewHolder.");
        }

        Picasso.with(getContext())
                .load(mTMDBAssetURLFactory.getPosterImageURL(
                        movie.getPosterPath(), ImageSize.W_185))
                .into(viewHolder.moviePosterImageView);

        return resultView;
    }

    private static class ViewHolder {
        public final ImageView moviePosterImageView;

        public ViewHolder(final View view) {
            moviePosterImageView = (ImageView)view.findViewById(R.id.movie_image_view);
        }
    }
}
