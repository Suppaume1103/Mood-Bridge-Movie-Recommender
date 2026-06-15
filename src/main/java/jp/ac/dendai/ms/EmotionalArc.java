package jp.ac.dendai.ms;

import java.util.List;

/**
 * レビュー群から推定した感情スコアの時系列点列を保持するデータクラス
 */
public class EmotionalArc {

    private List<Double> points; // 感情スコアを文の出現順に並べたリスト。先頭が映画序盤、末尾が終盤に対応 (-1.0~1.0)

    public EmotionalArc(List points) {
        this.points = points;
    }

    public List getPoints() {
        return points;
    }

    public void setPoints(List points) {
        this.points = points;
    }

}
