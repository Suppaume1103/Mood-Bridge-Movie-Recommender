package jp.ac.dendai.ms;

import com.google.gson.Gson;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

/**
 * GPT APIを一元管理する共通クラス
 * MoodAnalyzer・EmotionalArcEstimator・ReasonGeneratorで使用する
 */
public class GptClient {

    private final OpenAIClient client;
    private final Gson gson;

    // GPTにJSON形式で返させた感情スコアをマッピングするための内部クラス
    private static class SentimentResult {
        double valence;
    }

    // GPTにJSON形式で返させた活性度スコアをマッピングするための内部クラス
    private static class ArousalResult {
        double arousal;
    }

    public GptClient() {
        this.client = OpenAIOkHttpClient.fromEnv(); // 環境変数からAPIキーを取得して初期化
        this.gson = new Gson();
    }

    /**
     * テキストの感情価(valence)を-1.0~1.0の数値で返す
     * @param text 評価対象のテキスト
     * @return 感情価(-1.0~1.0)
     */
    public double scoreSentiment(String text) {
        String systemPrompt
                = "あなたはテキストの感情を評価する専門家です。"
                + "与えられたテキストの感情価を -1.0(非常にネガティブ)から "
                + "1.0(非常にポジティブ)の範囲の数値で評価してください。"
                + "出力は {\"valence\": 数値} という形式のJSONのみとし、"
                + "説明文やその他の文字は一切含めないでください。";

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_5_4_MINI)
                .addSystemMessage(systemPrompt)
                .addUserMessage(text)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        String content = completion.choices().get(0)
                .message().content().orElse("{\"valence\": 0.0}");

        SentimentResult result = gson.fromJson(content, SentimentResult.class);
        return result.valence;

    }

    /**
     * テキストの感情の活性度(arousal)を0.0~1.0の数値で返す
     * @param text 評価対象のテキスト
     * @return 活性度(0.0~1.0)
     */
    public double scoreArousal(String text) {
        String systemPrompt
                = "あなたはテキストの感情の活性度(興奮度)を評価する専門家です。"
                + "与えられたテキストの活性度を 0.0(非常に落ち着いている・穏やか)から "
                + "1.0(非常に興奮している・活発)の範囲の数値で評価してください。"
                + "出力は {\"arousal\": 数値} という形式のJSONのみとし、"
                + "説明文やその他の文字は一切含めないでください。";

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_5_4_MINI)
                .addSystemMessage(systemPrompt)
                .addUserMessage(text)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        String content = completion.choices().get(0)
                .message().content().orElse("{\"arousal\": 0.5}");

        ArousalResult result = gson.fromJson(content, ArousalResult.class);
        return result.arousal;
    }

    /**
     * 映画タイトルとあらすじ(TMDBのoverview)から、物語を時系列(冒頭→結末)に
     * 沿って8〜10文程度の英語の詳しい物語文に拡張する。あらすじが薄い場合はGPT自身の
     * 作品知識で補って生成させる。既存のSentenceDetector(英語専用モデル)で文分割できるように、出力は英語に統一する。
     * @param title    映画タイトル
     * @param overview あらすじ(TMDBのoverview)
     * @return 拡張された英語の物語文
     */
    public String expandSynopsis(String title, String overview) {
        String systemPrompt
                = "You are an expert at expanding a movie's brief overview into a "
                + "more detailed narrative description. Based on the given movie "
                + "title and overview, write out the story in chronological order "
                + "(beginning to end) in about 8 to 10 English sentences, "
                + "supplementing with your own knowledge of the film if helpful. "
                + "Output only the narrative description, with no other text.";

        String userMessage = "Movie title: " + title + "\nOverview: " + overview;

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_5_4_MINI)
                .addSystemMessage(systemPrompt)
                .addUserMessage(userMessage)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        return completion.choices().get(0).message().content().orElse(overview);
    }

    /**
     * プロンプトをそのままGPTに送り、生成された文章を返す
     * @param prompt GPTに送るプロンプト
     * @return GPTが生成した文章
     */
    public String generateReason(String prompt) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_5_4_MINI)
                .addUserMessage(prompt)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);
        return completion.choices().get(0).message().content().orElse("");

    }
}
