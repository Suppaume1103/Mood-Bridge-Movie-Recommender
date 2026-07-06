package jp.ac.dendai.ms;

import java.util.List;

/**
 * MoodAnalyzerの動作確認用テストクラス。
 * GPT APIを実際に呼び出すため、VS Codeのlaunch.jsonでOPENAI_API_KEYを
 * 設定した上で実行すること。
 */
public class MoodAnalyzerTest {
        public static void main(String[] args) {
                GptClient gpt = new GptClient();
                MoodAnalyzer analyzer = new MoodAnalyzer(gpt);

                MoodInput input = new MoodInput(
                                "仕事で失敗して落ち込んでいて、何もやる気が起きない",
                                "元気を取り戻して前向きな気持ちになりたい",
                                List.of("ドラマ"));

                MoodBridge bridge = analyzer.buildBridge(input);

                System.out.println("=== MoodAnalyzer テスト ===");
                System.out.printf("今の気分   : valence=%.2f, arousal=%.2f%n",
                                bridge.getStart().getValance(), bridge.getStart().getArousal());
                System.out.printf("なりたい気分: valence=%.2f, arousal=%.2f%n",
                                bridge.getGoal().getValance(), bridge.getGoal().getArousal());

                boolean pass = bridge.getStart().getValance() < bridge.getGoal().getValance();
                System.out.println(pass
                                ? "PASS: 今の気分よりなりたい気分の方がvalenceが高い"
                                : "FAIL: valenceの大小関係が期待と異なる(GPTの評価揺れの可能性あり)");
        }
}
