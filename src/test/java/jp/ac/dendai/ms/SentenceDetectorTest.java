package jp.ac.dendai.ms;

import java.util.List;

/**
 * SentenceDetectorの動作確認用テストクラス。
 * 外部APIを使わないため、VS Codeの「Current File」構成でそのまま実行できる。
 */
public class SentenceDetectorTest {

    public static void main(String[] args) {
        SentenceDetector detector = new SentenceDetector();

        String text = "This movie was amazing. The plot twist at the end really surprised me! "
                + "I would definitely recommend it to my friends.";

        List<String> sentences = detector.split(text);

        System.out.println("=== SentenceDetector テスト ===");
        System.out.println("入力: " + text);
        System.out.println("分割結果 (" + sentences.size() + "文):");
        for (String sentence : sentences) {
            System.out.println("  - " + sentence);
        }

        boolean pass = sentences.size() == 3;
        System.out.println(pass ? "PASS: 3文に分割された" : "FAIL: 期待した文数と異なる (実際: " + sentences.size() + ")");
    }
}
