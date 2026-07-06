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

    // MoodInputにジャンル情報がなく、GptClientにもジャンル推定手段がないため、
    // 固定のジャンル候補ごとに人気No.1の映画を1本ずつ集めて比較対象とする
    private static final List<String> CANDIDATE_GENRES = List.of("ドラマ", "コメディ", "ロマンス");

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
        try {
            MoodBridge bridge = moodAnalyzer.buildBridge(input);

            List<Movie> targets = new ArrayList<>();
            for (String genre : CANDIDATE_GENRES) {
                List<Movie> found = tmdbClient.searchMovies(genre);
                if (!found.isEmpty()) {
                    targets.add(found.get(0));
                }
            }

            // 選定後にレビューを再取得しないよう、映画ごとのレビューを保持しておく。
            // Movieはequals/hashCode未オーバーライドのため参照同一性で引く必要があり、
            // BridgeSelector.select()が候補リストの要素をそのまま返す実装に依存する
            Map<Movie, List<Review>> reviewsByMovie = new HashMap<>();
            Map<Movie, EmotionalArc> arcs = new HashMap<>();
            for (Movie movie : targets) {
                List<Review> reviews = tmdbClient.fetchReviews(movie.getId());
                reviewsByMovie.put(movie, reviews);
                arcs.put(movie, arcEstimator.estimate(reviews));
            }

            Recommendation rec = bridgeSelector.select(bridge, targets, arcs);

            List<String> keywords = keywordExtractor.extractKeywords(reviewsByMovie.get(rec.getMovie()));
            String reason = reasonGenerator.generate(rec, rec.getArc(), keywords);
            rec.setReason(reason);

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
