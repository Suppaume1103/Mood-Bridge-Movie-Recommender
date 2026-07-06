package jp.ac.dendai.ms;

import java.util.List;

/**
 * AppControllerの動作確認用テストクラス(CLIで通しテスト)。
 * MoodAnalyzer・TmdbClient・EmotionalArcEstimator・KeyWordExtractor・
 * BridgeSelector・ReasonGeneratorを実際に組み合わせた全体フローの結合テスト。
 * GPT APIとTMDB APIを実際に呼び出すため、VS Codeのlaunch.jsonで
 * OPENAI_API_KEYとTMDB_API_KEYを設定した上で実行すること。
 */
public class AppControllerTest {
        public static void main(String[] args) {
                GptClient gpt = new GptClient();
                AppController controller = new AppController(gpt);

                MoodInput input = new MoodInput("最近仕事で失敗続きで落ち込んでいる", "前向きな気持ちになりたい",
                                List.of("ドラマ", "コメディ", "ロマンス"));

                System.out.println("=== AppController 通しテスト ===");
                Recommendation rec = controller.onSubmit(input);

                System.out.println("選ばれた映画: " + rec.getMovie().getTitle());
                System.out.println("推薦理由: " + rec.getReason());

                boolean pass = rec.getMovie() != null && rec.getReason() != null && !rec.getReason().isBlank();
                System.out.println(pass
                                ? "PASS: 気分入力から映画推薦が生成された"
                                : "FAIL: 推薦結果が不完全だった");

                boolean lastMatches = controller.getLastRecommendation() == rec;
                System.out.println(lastMatches
                                ? "PASS: getLastRecommendation()がonSubmit()の戻り値と一致した"
                                : "FAIL: getLastRecommendation()の値が一致しない");

                controller.onReset();
                boolean resetOk = controller.getLastRecommendation() == null;
                System.out.println(resetOk
                                ? "PASS: onReset()後にgetLastRecommendation()がnullになった"
                                : "FAIL: onReset()後もgetLastRecommendation()がnullにならなかった");
        }
}
