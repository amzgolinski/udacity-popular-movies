package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * An object to encapsulate movie data.
 */
public class Movie implements Parcelable {

  public static final String EXTRA_MOVIE
        = "com.example.android.popularmovies.EXTRA_MOVIE";

  private int mId;
  private double mRating;
  private double mPopularity;
  private String mTitle;
  private String mOverview;
  private String mPosterPath;
  private Date mReleaseDate;

  private static final long   NULL_DATE     = -1;

  public Movie () {
    // empty
  }

  public Movie (
      int id,
      double rating,
      double popularity,
      String title,
      String overview,
      String posterPath,
      Date releaseDate) {
    mId = id;
    mRating = rating;
    mPopularity = popularity;
    mTitle = title;
    mOverview = overview;
    mPosterPath = posterPath;
    mReleaseDate = releaseDate;
  }

  private Movie(Parcel in) {
    mId = in.readInt();
    mRating = in.readDouble();
    mPopularity = in.readDouble();
    mTitle = in.readString();
    mOverview = in.readString();
    mPosterPath = in.readString();

    long tmpDate = in.readLong();
    mReleaseDate = (tmpDate == NULL_DATE ? null : new Date(tmpDate));
 }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public String toString() {

    StringBuffer toReturn = new StringBuffer();
    toReturn.append("ID: " + Integer.toString(this.mId) + "\n");
    toReturn.append("Title: " + mTitle + "\n");
    toReturn.append("Overview: " + mOverview + "\n");
    toReturn.append("Release Date: " + getReleaseDateAsString() + "\n");
    toReturn.append("Rating: " + Double.toString(mRating) + "\n");
    toReturn.append("Popularity: " + Double.toString(mPopularity) + "\n");
    toReturn.append("Poster path: " + mPosterPath);
    return toReturn.toString();
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeInt(mId);
    parcel.writeDouble(mRating);
    parcel.writeDouble(mPopularity);
    parcel.writeString(mTitle);
    parcel.writeString(mOverview);
    parcel.writeString(mPosterPath);

    long time = (mReleaseDate == null ? NULL_DATE : mReleaseDate.getTime());
    parcel.writeLong(time);
  }

  public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
    @Override
    public Movie createFromParcel(Parcel parcel) {
      return new Movie(parcel);
    }

    @Override
    public Movie[] newArray(int i) {
      return new Movie[i];
    }

  };

  // Movie ID
  public int getId() {
    return mId;
  }

  public void setId(int id) {
    mId = id;
  }

  // Movie Rating
  public double getRating() {
    return mRating;
  }

  public void setRating(double rating) {
    mRating = rating;
  }

  public String getRatingString () {
    StringBuffer toReturn = new StringBuffer();
    if (mRating == 10.0 || mRating == 0.0) {
      toReturn.append(Math.round(mRating));
    } else {
      toReturn.append(mRating);
    }
    toReturn.append(" / 10");
    return toReturn.toString();
  }

  // Movie Popularity
  public double getPopularity() {
    return mPopularity;
  }

  public void setPopularity(double popularity) {
    mPopularity = popularity;
  }

  // Movie Title
  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    mTitle = title;
  }

  // Movie Overview
  public String getOverview() {
    return mOverview;
  }

  public void setOverview(String overview) {
    mOverview = overview;
  }


  // Movie Poster Path
  public void setPosterPath(String posterPath) {
    mPosterPath = posterPath;
  }

  public String getMoviePosterURL (String size) {

    final String POSTER_PATH   = "http://image.tmdb.org/t/p/";
    String toReturn = null;

    if (mPosterPath != null) {
      toReturn = POSTER_PATH + size + "/" + mPosterPath;
    }
    return toReturn;

  }

  // Movie Release Date
  public void setReleaseDate(Date releaseDate) {
    mReleaseDate = releaseDate;
  }
  public String getReleaseDateAsString () {
    String toReturn = "N/A";
    if (mReleaseDate != null) {
      SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
      toReturn = format.format(mReleaseDate);
    }
    return toReturn;
  }

}
