package jp.ac.dendai.ms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ユーザーの気分入力(MoodInput)を受け取り、MoodAnalyzer → TmdbClient →
 * EmotionalArcEstimator → KeyWordExtractor → BridgeSelector → ReasonGenerator
 * の一連の処理を束ねて推薦結果(Recommendation)を組み立てる全体制御クラス。
 */
public class AppController {

    private final MoodAnalyzer moodAnalyzer;
    private final TmdbClient tmdbClient;
    private final EmotionalArcEstimator arcEstimator;
    private final KeyWordExtractor keywordExtractor;
    private final BridgeSelector bridgeSelector;
    private final ReasonGenerator reasonGenerator;

    private Recommendation lastRecommendation;

    public AppController(GptClient gpt) {
        this.moodAnalyzer = new MoodAnalyzer(gpt);
        this.tmdbClient = new TmdbClient();
        this.arcEstimator = new EmotionalArcEstimator(gpt);
        this.keywordExtractor = new KeyWordExtractor();
        this.bridgeSelector = new BridgeSelector();
        this.reasonGenerator = new ReasonGenerator(gpt);
    }

    // --- ユーザーの気分入力を受け取り、映画推薦を生成するメソッド群 ---
    public Recommendation onSubmit(MoodInput input) {
        if (input.getGenres() == null || input.getGenres().isEmpty()) {
            throw new AppControllerException("ジャンルが1件も選択されていません", null);
        }

        try {
            MoodBridge bridge = moodAnalyzer.buildBridge(input);

            List<Movie> targets = new ArrayList<>();
            for (String genre : input.getGenres()) {
                List<Movie> found = tmdbClient.searchMovies(genre);
                // ジャンルごとに最大3件まで候補を選定する
                int poolSize = Math.min(3, found.size());
                for (int i = 0; i < poolSize; i++) {
                    targets.add(found.get(i));
                }
            }

            // 感情曲線はあらすじ(タイトル・overview)から推定するためレビュー取得は不要。
            // 候補ごとにMap<Movie, EmotionalArc>を構築し、BridgeSelector.select()に渡す
            Map<Movie, EmotionalArc> arcs = new HashMap<>();
            for (Movie movie : targets) {
                arcs.put(movie, arcEstimator.estimate(movie));
            }

            Recommendation rec = bridgeSelector.select(bridge, targets, arcs);

            // レビューは選定後の勝者1本についてのみ取得し、重要語抽出に使用する
            List<Review> reviews = tmdbClient.fetchReviews(rec.getMovie().getId());
            List<String> keywords = keywordExtractor.extractKeywords(reviews);
            String reason = reasonGenerator.generate(rec, rec.getArc(), keywords);
            rec.setReason(reason);
            rec.setKeywords(keywords);

            this.lastRecommendation = rec;
            return rec;
        } catch (IOException e) {
            throw new AppControllerException("映画推薦処理でエラーが発生しました: " + e.getMessage(), e);
        }
    }

    // --- ユーザーの気分入力をリセットするメソッド群 ---
    public void onReset() {
        this.lastRecommendation = null;
    }

    // --- 最後に生成したRecommendationを取得するメソッド群 ---
    public Recommendation getLastRecommendation() {
        return lastRecommendation;
    }
}
