package pham.ntu.grabtheater;

import java.io.Serializable;

/**
 * Created by Administrator PC on 3/31/2016.
 */
public class Movie implements Serializable{
    boolean adult;
    String backdrop_path;
    int[] genre_ids;
    int id;
    String original_language;
    String original_title;
    String overview;
    String release_date;
    String poster_path;
    double popularity;
    String title;
    boolean video;
    double vote_average;
    int vote_count;

    public Movie(boolean adult,
            String backdrop_path,int[] genre_ids,int id,
            String original_language,String original_title,String overview,
            String release_date,String poster_path,double popularity,String title,
            boolean video,double vote_average,int vote_count){
        this.adult = adult;
        this.backdrop_path = backdrop_path;
        this.genre_ids = genre_ids;
        this.id = id;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.popularity = popularity;
        this.title = title;
        this.video = video;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public int[] getGenre_ids() {
        return genre_ids;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public int getId() {
        return id;
    }

    public double getPopularity() {
        return popularity;
    }
}
