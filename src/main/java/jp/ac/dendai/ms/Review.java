package jp.ac.dendai.ms;

/**
 * TMDBのレスポンスフィールド(レビュー執筆者ID,レビュー本文,ユーザースコア)に対応するデータクラス
 */
public class Review {

    private String author; // レビュー執筆者ID
    private String content; // レビュー本文
    private double rating; // ユーザースコア

    public Review() {
    }

    public Review(String author, String content, double rating) {
        this.author = author;
        this.content = content;
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}