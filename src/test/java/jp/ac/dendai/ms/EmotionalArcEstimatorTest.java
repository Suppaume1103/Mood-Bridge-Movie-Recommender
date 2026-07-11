package jp.ac.dendai.ms;

import java.util.List;

/**
 * EmotionalArcEstimatorの動作確認用テストクラス。
 * GPT APIを実際に呼び出すため、VS Codeのlaunch.jsonでOPENAI_API_KEYを
 * 設定した上で実行すること。
 */
public class EmotionalArcEstimatorTest {

    public static void main(String[] args) {
        GptClient gpt = new GptClient();
        EmotionalArcEstimator estimator = new EmotionalArcEstimator(gpt);

        Movie movie = new Movie(List.of("Drama"), 0,
                "A man starts his life in despair after losing everything, "
                + "but through unexpected friendships he gradually finds hope "
                + "and by the end achieves happiness.",
                0.0, 0, "Test Movie");

        EmotionalArc arc = estimator.estimate(movie);

        System.out.println("=== EmotionalArcEstimator テスト ===");
        System.out.println("感情曲線の点列: " + arc.getPoints());

        boolean pass = arc.getPoints() != null && arc.getPoints().size() >= 2;
        System.out.println(pass
                ? "PASS: あらすじから複数点のvalenceが推定された"
                : "FAIL: 点列の数が想定より少ない");
    }
}
