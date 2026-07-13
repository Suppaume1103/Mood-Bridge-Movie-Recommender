package jp.ac.dendai.ms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * APIキー等の設定値を取得するユーティリティ。
 * プロジェクトルートの.envファイルからのみ読み込む。
 */
public final class EnvLoader {

    private static final Map<String, String> DOTENV = load();

    private EnvLoader() {
    }

    /**
     * 指定したキーの値を.envファイルから取得する。
     * @param key キー名
     * @return 値
     * @throws IllegalStateException .envファイルに指定キーが設定されていない場合
     */
    public static String get(String key) {
        String value = DOTENV.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "環境変数 " + key + " が.envファイルに設定されていません。"
                    + ".env.exampleをプロジェクトルートに.envとしてコピーし、値を設定してください。");
        }
        return value;
    }

    private static Map<String, String> load() {
        Map<String, String> values = new HashMap<>();
        Path path = Path.of(".env");
        if (!Files.exists(path)) {
            return values;
        }
        try {
            for (String line : Files.readAllLines(path)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int separatorIndex = trimmed.indexOf('=');
                if (separatorIndex <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, separatorIndex).trim();
                String value = trimmed.substring(separatorIndex + 1).trim();
                values.put(key, value);
            }
        } catch (IOException e) {
            System.err.println(".envファイルの読み込みに失敗しました: " + e.getMessage());
        }
        return values;
    }
}
