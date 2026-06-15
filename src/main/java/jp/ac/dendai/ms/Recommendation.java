package jp.ac.dendai.ms;

/**
 * ResultPanelに渡すmovie・arc・reasonを持つデータクラス
 */
public class Recommendation {

    private Movie movie;
    private EmotionalArc arc;
    String reason; // 推薦理由文

    public Recommendation(EmotionalArc arc, Movie movie, String reason) {
        this.arc = arc;
        this.movie = movie;
        this.reason = reason;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public EmotionalArc getArc() {
        return arc;
    }

    public void setArc(EmotionalArc arc) {
        this.arc = arc;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
