package jp.ac.dendai.ms;

import java.util.List;

/**
 * TMDBのレスポンスフィールド(TMDb ID,タイトル,ジャンル,あらすじ,公開年,評価)に対応するデータクラス
 */
public class Movie {

    private int id; // TMDb ID
    private String title; // タイトル
    private List<String> genres; // ジャンル
    private String overview; // あらすじ
    private int releaseYear; // 公開年(不明時は0)
    private double rating; // TMDB評価(0.0〜10.0、不明時は0.0)

    public Movie() {
    }

    public Movie(List<String> genres, int id, String overview, double rating, int releaseYear, String title) {
        this.genres = genres;
        this.id = id;
        this.overview = overview;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.title = title;
    }

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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
