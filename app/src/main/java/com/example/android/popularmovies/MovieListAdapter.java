package com.example.android.popularmovies;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A custom ArrayAdapter to handle Movie objects.
 */
public class MovieListAdapter extends ArrayAdapter<Movie> {

  public MovieListAdapter(Activity context, List<Movie> movies) {
    super(context, 0, movies);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    // Gets the Movie object from the ArrayAdapter at the appropriate position
    Movie movie = getItem(position);
    ImageView poster;

    // If the view is not re-cycled
    if (convertView == null) {
      poster = new ImageView(getContext());
      int width = Math.round(getContext().getResources().getDimension(R.dimen.poster_height));
      int height = Math.round(getContext().getResources().getDimension(R.dimen.poster_width));
      poster.setLayoutParams(new GridView.LayoutParams(width, height));
      poster.setScaleType(ImageView.ScaleType.FIT_CENTER);

    } else {
      poster = (ImageView) convertView;
    }

    Picasso.with(getContext())
        .load(movie.getMoviePosterURL(getContext()
            .getString(R.string.pref_movie_poster_size)))
        .error(R.drawable.no_poster_available)
        .into(poster);

    return poster;
  }

}
