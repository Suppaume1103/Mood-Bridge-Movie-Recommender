package jp.ac.dendai.ms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * KeyWordExtractorの動作確認用テストクラス。
 * 外部APIを使わないため、VS Codeの「Current File」構成でそのまま実行できる。
 */
public class KeyWordExtractorTest {

    public static void main(String[] args) throws IOException {
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("user1",
                "This space adventure movie has stunning visuals and a thrilling story about astronauts.", 8.0));
        reviews.add(new Review("user2",
                "The astronauts in this adventure movie face a thrilling battle in space.", 7.5));
        reviews.add(new Review("user3",
                "A quiet romantic drama with beautiful music, very different from a space adventure.", 6.0));

        KeyWordExtractor extractor = new KeyWordExtractor();
        List<String> keywords = extractor.extractKeywords(reviews);

        System.out.println("=== KeyWordExtractor テスト ===");
        System.out.println("抽出された重要語: " + keywords);

        boolean pass = !keywords.isEmpty()
                && (keywords.contains("adventure") || keywords.contains("space"));
        System.out.println(pass ? "PASS: 重要語が抽出された" : "FAIL: 期待した重要語が含まれていない");
    }
}
