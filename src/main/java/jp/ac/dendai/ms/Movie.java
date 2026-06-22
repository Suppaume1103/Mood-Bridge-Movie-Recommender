package jp.ac.dendai.ms;

import java.util.List;

/**
 * TMDBのレスポンスフィールド(TMDb ID,タイトル,ジャンル,あらすじ)に対応するデータクラス
 */
public class Movie {

    private int id; // TMDb ID
    private String title; // タイトル
    private List<String> genres; // ジャンル
    private String overview; // あらすじ

    public Movie() {
    }

    public Movie(List<String> genres, int id, String overview, String title) {
        this.genres = genres;
        this.id = id;
        this.overview = overview;
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
}