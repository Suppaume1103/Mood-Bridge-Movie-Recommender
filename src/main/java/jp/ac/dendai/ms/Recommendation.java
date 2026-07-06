package jp.ac.dendai.ms;

import java.util.List;

/**
 * ResultPanelに渡すmovie・arc・reason・keywordsを持つデータクラス
 */
public class Recommendation {

    private Movie movie;
    private EmotionalArc arc;
    String reason; // 推薦理由文
    private List<String> keywords; // レビューから抽出した重要語(BridgeSelector選定後にAppControllerが設定)

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

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

}
