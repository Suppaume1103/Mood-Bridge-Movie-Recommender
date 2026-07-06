package jp.ac.dendai.ms;

import java.util.Arrays;
import java.util.List;

/**
 * ReasonGeneratorの動作確認用テストクラス。
 * GPT APIを実際に呼び出すため、VS Codeのlaunch.jsonでOPENAI_API_KEYを
 * 設定した上で実行すること。
 */
public class ReasonGeneratorTest {
    public static void main(String[] args) {
        GptClient gpt = new GptClient();
        ReasonGenerator generator = new ReasonGenerator(gpt);

        Movie movie = new Movie(List.of("Drama"), 1,
                "主人公が挫折を経験しながらも、周囲の支えによって再び前を向くまでを描いた物語。",
                7.5, 2020, "Sad to Hopeful");
        EmotionalArc arc = new EmotionalArc(Arrays.asList(-0.8, -0.3, 0.6));
        Recommendation rec = new Recommendation(arc, movie, "");
        List<String> keywords = List.of("hope", "friendship", "recovery");

        String reason = generator.generate(rec, arc, keywords);

        System.out.println("=== ReasonGenerator テスト ===");
        System.out.println("生成された推薦理由: " + reason);

        boolean pass = reason != null && !reason.isBlank();
        System.out.println(pass
                ? "PASS: 推薦理由文が生成された"
                : "FAIL: 推薦理由文が空だった");
    }
}
