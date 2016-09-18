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
        }

        ImageView imageView = (ImageView)resultView.findViewById(R.id.movie_image_view);

        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext())
                .load(mTMDBAssetURLFactory.getPosterImageURL(movie.getPosterPath(), ImageSize.W_185))
                .into(imageView);

        return imageView;
    }
}
