package jp.ac.dendai.ms;

import java.util.List;

/**
 * 選ばれた映画・感情曲線・重要語をもとに、GPT APIで推薦理由文を生成するクラス
 */
public class ReasonGenerator {

    private final GptClient gpt;

    public ReasonGenerator(GptClient gpt) {
        this.gpt = gpt;
    }

    /**
     * 推薦理由文を生成する。
     * 映画のタイトル・あらすじ、感情曲線の序盤/終盤の傾向、重要語をプロンプトに
     * 組み込み、GPTに「前半〇〇な雰囲気だが、後半△△に転調する」という説明文を書かせる。
     *
     * @param rec      選ばれた映画のRecommendation(movie部分を使用)
     * @param arc      感情曲線
     * @param keywords レビューから抽出した重要語
     * @return GPTが生成した推薦理由文
     */
    public String generate(Recommendation rec, EmotionalArc arc, List<String> keywords) {
        Movie movie = rec.getMovie();
        List<?> points = arc.getPoints();
        double firstPoint = (Double) points.get(0);
        double lastPoint = (Double) points.get(points.size() - 1);

        String prompt = "あなたは映画推薦の理由を説明するアシスタントです。"
                + "以下の情報をもとに、この映画を視聴するとユーザーの気分がどのように"
                + "変化していくかを、「前半は〇〇な雰囲気だが、後半△△に転調する」という"
                + "形式を意識して2〜3文の日本語で説明してください。"
                + "説明文以外の文字は含めないでください。\n"
                + "映画タイトル: " + movie.getTitle() + "\n"
                + "あらすじ: " + movie.getOverview() + "\n"
                + "感情曲線: 序盤のvalence=" + firstPoint + ", 終盤のvalence=" + lastPoint
                + "(valenceは-1.0が非常にネガティブ, 1.0が非常にポジティブ)\n"
                + "レビューから抽出した重要語: " + String.join(", ", keywords);

        return gpt.generateReason(prompt);
    }
}
