package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * A fragment which displays the details of the selected movie.
 */
public class MovieDetailFragment extends Fragment {

  private Movie mMovie;

  public MovieDetailFragment() {
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    Intent intent = getActivity().getIntent();
    View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

    if (intent != null && intent.hasExtra(Movie.EXTRA_MOVIE)) {

      mMovie = intent.getParcelableExtra(Movie.EXTRA_MOVIE);

      // Movie Title
      ((TextView) rootView.findViewById(R.id.movie_detail_title))
          .setText(mMovie.getTitle());

      // Movie Overview
      ((TextView) rootView.findViewById(R.id.movie_detail_overview))
          .setText(mMovie.getOverview());

      // Movie Rating
      ((TextView) rootView.findViewById(R.id.movie_detail_rating))
          .setText(mMovie.getRatingString());

      // Movie Release Date
      ((TextView) rootView.findViewById(R.id.movie_detail_release_date))
          .setText(mMovie.getReleaseDateAsString());

      // Movie Poster
      Picasso.with(getContext())
          .load(mMovie.getMoviePosterURL(getString(R.string.pref_movie_poster_size)))
          .error(R.drawable.no_poster_available)
          .into((ImageView) rootView.findViewById(R.id.movie_detail_poster));

    }
    return rootView;
  }
}
