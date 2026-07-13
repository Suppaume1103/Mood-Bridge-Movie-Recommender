package jp.ac.dendai.ms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * TMDB API へのHTTPアクセスを担当するクラス。
 * OkHttp でリクエストを送り、Gson でJSONをパースする。
 */
public class TmdbClient {

    private static final String BASE_URL = "https://api.themoviedb.org/3";

    // 常に人気上位のみに偏らないよう、取得ページをランダム化する(1〜5ページ目)
    private static final Random RANDOM = new Random();

    private final String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;

    /** ジャンル名(日本語・英語) → TMDB ジャンルID の変換マップ */
    private static final Map<String, Integer> GENRE_MAP = Map.ofEntries(
        Map.entry("アクション",     28),
        Map.entry("action",        28),
        Map.entry("アドベンチャー", 12),
        Map.entry("adventure",     12),
        Map.entry("コメディ",       35),
        Map.entry("comedy",        35),
        Map.entry("犯罪",           80),
        Map.entry("crime",         80),
        Map.entry("ドラマ",         18),
        Map.entry("drama",         18),
        Map.entry("ファンタジー",   14),
        Map.entry("fantasy",       14),
        Map.entry("ホラー",         27),
        Map.entry("horror",        27),
        Map.entry("ミステリー",      9648),
        Map.entry("mystery",       9648),
        Map.entry("ロマンス",       10749),
        Map.entry("romance",       10749),
        Map.entry("sf",            878),
        Map.entry("science fiction", 878),
        Map.entry("スリラー",       53),
        Map.entry("thriller",      53)
    );

    // --- TMDB レスポンスのデシリアライズ用内部クラス ---

    private static class MovieListResponse {
        List<TmdbMovie> results;
    }

    private static class TmdbMovie {
        int id;
        String title;
        @SerializedName("genre_ids") List<Integer> genreIds; // TMDBのJSONキーがスネークケース(genre_ids)なのでアノテーションでマッピング
        String overview;
        @SerializedName("release_date") String releaseDate; // "YYYY-MM-DD"形式
        @SerializedName("vote_average") Double voteAverage;
    }

    private static class ReviewListResponse {
        List<TmdbReview> results;
    }

    private static class TmdbReview {
        String author;
        String content;
        @SerializedName("author_details") AuthorDetails authorDetails; // genre_ids と同様に、TMDBのJSONキーがスネークケース(author_details)なのでアノテーションでマッピング
    }

    private static class AuthorDetails {
        Double rating;
    }

    // --- ジャンルID → ジャンル名 の逆引きマップ ---
    private static final Map<Integer, String> GENRE_ID_MAP = Map.ofEntries(
        Map.entry(28,    "アクション"),
        Map.entry(12,    "アドベンチャー"),
        Map.entry(35,    "コメディ"),
        Map.entry(80,    "犯罪"),
        Map.entry(18,    "ドラマ"),
        Map.entry(14,    "ファンタジー"),
        Map.entry(27,    "ホラー"),
        Map.entry(9648,  "ミステリー"),
        Map.entry(10749, "ロマンス"),
        Map.entry(878,   "SF"),
        Map.entry(53,    "スリラー")
    );

    /** GUIのジャンル選択リスト用。Map.ofEntriesはイテレーション順序が不定なため、
     *  表示順を固定するために別途用意する順序付きリスト */
    public static final List<String> DISPLAY_GENRES = List.of(
        "ドラマ", "アクション", "ロマンス", "アドベンチャー", "コメディ",
        "ファンタジー", "ホラー", "犯罪", "ミステリー", "SF", "スリラー"
    );

    /**
     * コンストラクタ。
     * 環境変数 TMDB_API_KEY からAPIキーを読み込む。
     */
    public TmdbClient() {
        // .envファイルからAPIキーを取得する(EnvLoader参照)
        this.apiKey = EnvLoader.get("TMDB_API_KEY");
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    /**
     * ジャンル名で映画候補リストを取得する。
     *
     * @param genre ジャンル名(日本語または英語)
     * @return Movie のリスト
     */
    public List<Movie> searchMovies(String genre) throws IOException {
        // ジャンル名 → ジャンルID に変換
        Integer genreId = GENRE_MAP.get(genre.toLowerCase());
        if (genreId == null) {
            throw new IllegalArgumentException("対応していないジャンルです: " + genre);
        }

        int page = RANDOM.nextInt(5) + 1; // 1〜5からランダムに選び、常に最新の人気上位に偏らないようにする

        HttpUrl url = HttpUrl.parse(BASE_URL + "/discover/movie").newBuilder()
                .addQueryParameter("api_key", apiKey)
                .addQueryParameter("with_genres", String.valueOf(genreId))
                .addQueryParameter("language", "en-US")
                .addQueryParameter("sort_by", "popularity.desc")
                .addQueryParameter("page", String.valueOf(page))
                .build();

        String json = get(url);
        MovieListResponse response = gson.fromJson(json, MovieListResponse.class);

        // TMDBがエラー応答(resultsを含まないJSON)を返すとresponse/response.resultsがnullになるため防御する
        if (response == null || response.results == null) {
            throw new IOException("TMDBの映画検索応答を解析できませんでした(genre=" + genre + ")");
        }

        List<Movie> movies = new ArrayList<>();
        for (TmdbMovie tmdbMovie : response.results) {
            Movie movie = new Movie();
            movie.setId(tmdbMovie.id);
            movie.setTitle(tmdbMovie.title);
            movie.setOverview(tmdbMovie.overview != null ? tmdbMovie.overview : "");
            movie.setRating(tmdbMovie.voteAverage != null ? tmdbMovie.voteAverage : 0.0);

            // "YYYY-MM-DD"形式の先頭4文字を公開年として解釈(不正値・欠損時は0)
            int year = 0;
            if (tmdbMovie.releaseDate != null && tmdbMovie.releaseDate.length() >= 4) {
                try {
                    year = Integer.parseInt(tmdbMovie.releaseDate.substring(0, 4));
                } catch (NumberFormatException e) {
                    year = 0;
                }
            }
            movie.setReleaseYear(year);

            // ジャンルID → ジャンル名 に変換
            List<String> genreNames = new ArrayList<>();
            if (tmdbMovie.genreIds != null) {
                for (int id : tmdbMovie.genreIds) {
                    String name = GENRE_ID_MAP.getOrDefault(id, "その他");
                    genreNames.add(name);
                }
            }
            movie.setGenres(genreNames);
            movies.add(movie);
        }
        return movies;
    }

    /**
     * 映画IDを指定してレビューリストを取得する。
     *
     * @param movieId TMDB の映画ID
     * @return Review のリスト
     */
    public List<Review> fetchReviews(int movieId) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "/movie/" + movieId + "/reviews").newBuilder()
                .addQueryParameter("api_key", apiKey)
                .addQueryParameter("language", "en-US")
                .build();

        String json = get(url);
        ReviewListResponse response = gson.fromJson(json, ReviewListResponse.class);

        // TMDBがエラー応答(resultsを含まないJSON)を返すとresponse/response.resultsがnullになるため防御する
        if (response == null || response.results == null) {
            throw new IOException("TMDBのレビュー取得応答を解析できませんでした(movieId=" + movieId + ")");
        }

        List<Review> reviews = new ArrayList<>();
        for (TmdbReview tmdbReview : response.results) {
            Review review = new Review();
            review.setAuthor(tmdbReview.author != null ? tmdbReview.author : "");
            review.setContent(tmdbReview.content != null ? tmdbReview.content : "");
            double rating = (tmdbReview.authorDetails != null
                    && tmdbReview.authorDetails.rating != null)
                    ? tmdbReview.authorDetails.rating : 0.0;
            review.setRating(rating);
            reviews.add(review);
        }
        return reviews;
    }

    /**
     * 指定URLにGETリクエストを送りレスポンスボディを文字列で返す。
     */
    private String get(HttpUrl url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTPエラー: " + response.code());
            }
            if (response.body() == null) {
                throw new IOException("HTTP応答のボディが空です");
            }
            return response.body().string();
        }
    }
}