package jp.ac.dendai.ms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BridgeSelectorの動作確認用テストクラス。
 * 外部APIを使わないため、VS Codeの「Current File」構成でそのまま実行できる。
 */
public class BridgeSelectorTest {
    public static void main(String[] args) {
        // 今の気分: valence=-0.8(落ち込んでいる) → なりたい気分: valence=0.6(前向き)
        MoodBridge bridge = new MoodBridge(
                new EmotionVector(-0.8, 0.2),
                new EmotionVector(0.6, 0.6));

        Movie sadToHopeful = new Movie(List.of("Drama"), 1, "序盤は暗いが後半に希望が見える映画", "Sad to Hopeful");
        Movie alwaysHappy = new Movie(List.of("Comedy"), 2, "終始明るい映画", "Always Happy");

        List<Movie> movies = new ArrayList<>();
        movies.add(sadToHopeful);
        movies.add(alwaysHappy);

        Map<Movie, EmotionalArc> arcs = new HashMap<>();
        // bridgeの始点・終点にほぼ一致する感情曲線(距離が小さいはず)
        arcs.put(sadToHopeful, new EmotionalArc(Arrays.asList(-0.75, -0.3, 0.55)));
        // bridgeとはかけ離れた感情曲線(常にvalenceが高い)
        arcs.put(alwaysHappy, new EmotionalArc(Arrays.asList(0.8, 0.85, 0.9)));

        BridgeSelector selector = new BridgeSelector();
        Recommendation rec = selector.select(bridge, movies, arcs);

        System.out.println("=== BridgeSelector テスト ===");
        System.out.println("選ばれた映画: " + rec.getMovie().getTitle());

        boolean pass = rec.getMovie() == sadToHopeful;
        System.out.println(pass
                ? "PASS: 感情曲線がmood bridgeに近い映画が選ばれた"
                : "FAIL: 期待した映画が選ばれなかった");
    }
}
