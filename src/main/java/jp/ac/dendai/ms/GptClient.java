package jp.ac.dendai.ms;

import com.google.gson.Gson;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

/**
 * GPT APIを一元管理する共通クラス
 * MoodAnalyzer・EmotionAnalyzer・ReasonGeneratorで使用する
 */
public class GptClient {

    private final OpenAIClient client;
    private final Gson gson;

    // GPTにJSON形式で返させた感情スコアをマッピングするための内部クラス
    private static class SentimentResult {
        double valence;
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
                .model(ChatModel.GPT_4O_MINI)
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
     * プロンプトをそのままGPTに送り、生成された文章を返す
     * @param prompt GPTに送るプロンプト
     * @return GPTが生成した文章
     */
    public String generateReason(String prompt) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .addUserMessage(prompt)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);
        return completion.choices().get(0).message().content().orElse("");

    }
}
