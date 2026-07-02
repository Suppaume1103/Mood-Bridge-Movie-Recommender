package jp.ac.dendai.ms;

import java.util.ArrayList;
import java.util.List;

/**
 * レビュー群を文単位に分割し、各文の感情価(valence)をGPT APIで推定して
 * 感情曲線(EmotionalArc)を組み立てるクラス
 */
public class EmotionalArcEstimator {

    private final SentenceDetector detector;
    private final GptClient gpt;

    public EmotionalArcEstimator(GptClient gpt) {
        this.detector = new SentenceDetector();
        this.gpt = gpt;
    }

    /**
     * レビュー群から感情曲線を推定する。
     * 全レビューを出現順に文分割し、各文のvalenceを並べた点列を返す。
     * @param reviews レビューのリスト
     * @return 感情曲線
     */
    public EmotionalArc estimate(List<Review> reviews) {
        List<Double> points = new ArrayList<>();
        for (Review review : reviews) {
            List<String> sentences = detector.split(review.getContent());
            for (String sentence : sentences) {
                points.add(gpt.scoreSentiment(sentence));
            }
        }
        return new EmotionalArc(points);
    }
}
