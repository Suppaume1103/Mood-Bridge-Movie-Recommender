package jp.ac.dendai.ms;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME; // 文分割を行うクラス
import opennlp.tools.sentdetect.SentenceModel; // 学習済みモデルを読み込むためのクラス

/**
 * OpenNLPの英語文分割モデルを使い、レビュー本文を文単位に分割するクラス
 */
public class SentenceDetector {

    // opennlp-models-sentdetect-en の jar 直下に同梱されているモデルファイル
    private static final String MODEL_RESOURCE = "/opennlp-en-ud-ewt-sentence-1.3-2.5.4.bin";

    private final SentenceDetectorME detector;

    public SentenceDetector() {
        try (InputStream modelIn = getClass().getResourceAsStream(MODEL_RESOURCE)) {
            if (modelIn == null) {
                throw new IllegalStateException("文分割モデルが見つかりません: " + MODEL_RESOURCE);
            }
            SentenceModel model = new SentenceModel(modelIn);
            this.detector = new SentenceDetectorME(model);
        } catch (IOException e) {
            throw new IllegalStateException("文分割モデルの読み込みに失敗しました", e);
        }
    }

    /**
     * テキストを文単位に分割する
     * @param text 分割対象のテキスト
     * @return 分割された文のリスト
     */
    public List<String> split(String text) {
        return Arrays.asList(detector.sentDetect(text));
    }
}
