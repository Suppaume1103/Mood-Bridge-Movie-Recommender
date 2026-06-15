package jp.ac.dendai.ms;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class Main {
    public static void main(String[] args) {
        // launch.json の env から OPENAI_API_KEY を読む
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O)
                .addSystemMessage("あなたは簡潔に答えるアシスタントです。")
                .addUserMessage("Javaとは何か、一文で説明して。")
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        completion.choices().forEach(choice ->
                choice.message().content().ifPresent(System.out::println)
        );
    }
}