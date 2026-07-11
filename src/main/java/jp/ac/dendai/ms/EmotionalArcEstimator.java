package jp.ac.dendai.ms;

import java.util.ArrayList;
import java.util.List;

/**
 * 映画のタイトル・あらすじをGPT自身の作品知識で補強詳細な英語の物語文に拡張し、
 * それをSentenceDetectorで文分割した上で各文をGPT APIでvalence評価することで、
 * 物語の時系列(冒頭→結末)に沿った感情曲線(EmotionalArc)を推定するクラス
 */
public class EmotionalArcEstimator {

    private final SentenceDetector detector;
    private final GptClient gpt;

    public EmotionalArcEstimator(GptClient gpt) {
        this.detector = new SentenceDetector();
        this.gpt = gpt;
    }

    /**
     * 映画のタイトル・あらすじから感情曲線を推定する。
     * @param movie 対象映画(タイトル・あらすじを使用)
     * @return 感情曲線
     */
    public EmotionalArc estimate(Movie movie) {
        String expandedSynopsis = gpt.expandSynopsis(movie.getTitle(), movie.getOverview());
        List<String> sentences = detector.split(expandedSynopsis);

        List<Double> points = new ArrayList<>();
        for (String sentence : sentences) {
            points.add(gpt.scoreSentiment(sentence));
        }
        return new EmotionalArc(points);
    }
}
