package com.emrox_riprap.popularmovies.POJO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by scott on 6/26/2016.
 */
public class Movie implements Parcelable{

    public static final String PARCEL_KEY = "parcel_key";
    private int id;
    private String title;
    private String overview;
    private String posterPath;



    private String releaseDate;
    private double voterRating;


    public Movie() {

    }

    public Movie(int id, String title, String overview, String posterPath, String releaseDate, double voterAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voterRating = voterAverage;
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoterRating() {
        return voterRating;
    }

    public void setVoterRating(double voterRating) {
        this.voterRating = voterRating;
    }


    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            Movie mMovie = new Movie();
            mMovie.id = source.readInt();
            mMovie.title = source.readString();
            mMovie.overview = source.readString();
            mMovie.posterPath = source.readString();
            mMovie.releaseDate = source.readString();
            mMovie.voterRating = source.readDouble();
            return mMovie;
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(posterPath);
        parcel.writeString(releaseDate);
        parcel.writeDouble(voterRating);

    }
}
