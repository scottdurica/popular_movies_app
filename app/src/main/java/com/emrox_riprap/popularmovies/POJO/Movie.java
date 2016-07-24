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
    private String poster_path;
    private String release_date;
//    @JsonProperty("vote_average")
    private double vote_average;


    public Movie() {

    }

    public Movie(int id, String title, String overview, String posterPath, String releaseDate, double voterAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster_path = posterPath;
        this.release_date = releaseDate;
        this.vote_average = voterAverage;
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

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
//    @JsonProperty("vote_average")
    public double getVote_average() {
        return vote_average;
    }
//    @JsonProperty("vote_average")
    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }


    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            Movie mMovie = new Movie();
            mMovie.id = source.readInt();
            mMovie.title = source.readString();
            mMovie.overview = source.readString();
            mMovie.poster_path = source.readString();
            mMovie.release_date = source.readString();
            mMovie.vote_average = source.readDouble();
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
        parcel.writeString(poster_path);
        parcel.writeString(release_date);
        parcel.writeDouble(vote_average);

    }
}
