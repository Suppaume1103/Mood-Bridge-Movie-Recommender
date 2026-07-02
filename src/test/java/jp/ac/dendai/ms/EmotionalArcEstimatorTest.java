package jp.ac.dendai.ms;

import java.util.ArrayList;
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

        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("user1",
                "The beginning was very sad and depressing. But by the end, "
                + "the story became hopeful and left me feeling inspired.", 8.0));

        EmotionalArc arc = estimator.estimate(reviews);

        System.out.println("=== EmotionalArcEstimator テスト ===");
        System.out.println("感情曲線の点列: " + arc.getPoints());

        boolean pass = arc.getPoints().size() >= 2;
        System.out.println(pass
                ? "PASS: 複数の文からvalenceが推定された"
                : "FAIL: 点列の数が想定より少ない");
    }
}
