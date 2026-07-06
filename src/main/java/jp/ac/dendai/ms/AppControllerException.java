package jp.ac.dendai.ms;

/**
 * AppController内で外部API呼び出し(TMDB API・重要語抽出のインデックス構築)に
 * 失敗した際に、検査例外IOExceptionをラップして投げる非検査例外。
 */
public class AppControllerException extends RuntimeException {
    public AppControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
