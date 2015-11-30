package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;


/**
 * The Fragment which handles movie detail display
 */
public class MovieListActivityFragment extends Fragment {

  private MovieListAdapter mMovieListAdapter;
  private ArrayList<Movie> mMovieList;
  private String mSortOrder;

  public MovieListActivityFragment() {
    //empty
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View movieListView = inflater.inflate(R.layout.fragment_movie_list, container, false);
    GridView gridview = (GridView) movieListView.findViewById(R.id.movie_poster_grid);
    mMovieListAdapter = new MovieListAdapter(getActivity(), mMovieList);
    gridview.setAdapter(mMovieListAdapter);
    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie movie = mMovieListAdapter.getItem(position);
        Intent movieDeatilIntent = new Intent(getActivity(), MovieDetailActivity.class);
        movieDeatilIntent.putExtra(Movie.EXTRA_MOVIE, movie);
        startActivity(movieDeatilIntent);
      }
    });

    return movieListView;
  }

  @Override
  public void onCreate(Bundle savedInstanceState){

    super.onCreate(savedInstanceState);
    String moviesKey = getString(R.string.movie_bundle_key);

    if(savedInstanceState == null || !savedInstanceState.containsKey(moviesKey)) {
      mMovieList = new ArrayList<Movie>();
      mSortOrder = this.getSortOrder();
      getMovies();
    } else {
      mMovieList = savedInstanceState.getParcelableArrayList(moviesKey);
      mSortOrder = savedInstanceState.getString("SortOrder");
    }
  }

  @Override
  public void onStart() {
    super.onStart();

    if (!getSortOrder().equals(mSortOrder)) {
      mSortOrder = getSortOrder();
      getMovies();
    }

  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelableArrayList(
        getString(R.string.movie_bundle_key),
        mMovieList
    );
    outState.putString(getString(R.string.sort_order_bundle_key), mSortOrder);

    super.onSaveInstanceState(outState);
  }

  private String getSortOrder() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String sortOrder =  prefs.getString(
        getString(R.string.pref_movie_sort_key),
        getString(R.string.pref_movie_sort_default));
    return sortOrder;
  }

  private void getMovies() {
    FetchMoviesTask movieTask = new FetchMoviesTask();
    movieTask.execute(mSortOrder);
  }

  /*
   * Encapsulates logic which makes an HTTP GET request to api.themoviedb.org
   */
  private class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final String MOVIE_DB_URL   = "http://api.themoviedb.org/3/discover/movie?";
    private final String SORT_PARAM     = "sort_by";
    private final String API_KEY_PARAM  = "api_key";
    private final String PAGE_PARAM     = "page";

    @Override
    protected Movie[] doInBackground(String... params) {

      // The movies to be returned
      Movie[] movies = null;

      // These two need to be declared outside the try/catch
      // so that they can be closed in the finally block.
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;

      // Will contain the raw JSON response as a string.
      String movieJSON = null;

      try {

        Uri builder = Uri.parse(MOVIE_DB_URL).buildUpon()
          .appendQueryParameter(SORT_PARAM, params[0])
          .appendQueryParameter(PAGE_PARAM,    "1")
          .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
          .build();

        URL url = new URL(builder.toString());

        // Log the URL
        Log.v(LOG_TAG, builder.toString());

        // Create the request to the Movie DB
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
          // Nothing to do.
          movieJSON = null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
          // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
          // But it does make debugging a *lot* easier if you print out the completed
          // buffer for debugging.
          buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
          // Stream was empty.
          return null;
        }
        movieJSON = buffer.toString();
        Log.v(LOG_TAG, movieJSON);
        movies = parseMovieJSON(movieJSON);

      } catch (JSONException jsonException) {

        Log.e(LOG_TAG, "JSON Error ", jsonException);

      } catch (IOException ioException) {

        Log.e(LOG_TAG, "IO Error ", ioException);
        // If the code didn't successfully get the weather data, there's no point in attempting
        // to parse it.
        return null;
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (final IOException e) {
            Log.e(LOG_TAG, "Error closing stream", e);
          }
        }
      }
      return movies;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
      if (movies != null) {
        mMovieList = new ArrayList<Movie>(Arrays.asList(movies));
        mMovieListAdapter.clear();
        mMovieListAdapter.addAll(mMovieList);
      }
    }

    private Date parseReleaseDate(String releaseDate) {
      Date toReturn = null;
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      try {
        toReturn = dateFormat.parse(releaseDate);
      } catch (ParseException pException) {
        Log.e(LOG_TAG, "Error parsing date: " + pException.toString());
      }
      return toReturn;
    }

    private Movie[] parseMovieJSON(String movieJSONString)
      throws JSONException {

      ArrayList<Movie> parsedMovies;

      final String ID             = "id";
      final String RESULTS        = "results";
      final String TITLE          = "original_title";
      final String OVERVIEW       = "overview";
      final String RELEASE_DATE   = "release_date";
      final String RATING         = "vote_average";
      final String POPULARITY     = "popularity";
      final String POSTER_PATH    = "poster_path";

      JSONObject movieJSON = new JSONObject(movieJSONString);
      JSONArray movies = movieJSON.getJSONArray(RESULTS);

      parsedMovies = new ArrayList<Movie>();

      for (int i=0; i<movies.length(); i++) {
        JSONObject jsonMovie = movies.getJSONObject(i);
        Movie currentMovie = new Movie();
        currentMovie.setId(jsonMovie.getInt(ID));
        currentMovie.setTitle(jsonMovie.getString(TITLE));
        currentMovie.setOverview(jsonMovie.getString(OVERVIEW));
        currentMovie.setPosterPath(jsonMovie.getString(POSTER_PATH));
        currentMovie.setRating(jsonMovie.getDouble(RATING));
        currentMovie.setPopularity(jsonMovie.getDouble(POPULARITY));
        currentMovie.setReleaseDate(
            parseReleaseDate(jsonMovie.getString(RELEASE_DATE))
        );
        parsedMovies.add(currentMovie);
        Log.v(LOG_TAG, "Adding movie:\n" + currentMovie.toString());

      }

      Movie[] toReturn = new Movie[parsedMovies.size()];
      return parsedMovies.toArray(toReturn);
    }

  }

}
