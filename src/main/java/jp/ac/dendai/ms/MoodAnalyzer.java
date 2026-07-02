package jp.ac.dendai.ms;

/**
 * ユーザーの気分テキストをGPT APIで感情ベクトル化し、
 * 「今の気分」から「なりたい気分」への橋渡し(MoodBridge)を組み立てるクラス
 */
public class MoodAnalyzer {

    private final GptClient gpt;

    public MoodAnalyzer(GptClient gpt) {
        this.gpt = gpt;
    }

    /**
     * 気分を表すテキストをEmotionVector(valence, arousal)に変換する
     * @param text 気分を表すテキスト
     * @return 感情ベクトル
     */
    public EmotionVector analyze(String text) {
        double valence = gpt.scoreSentiment(text);
        double arousal = gpt.scoreArousal(text);
        return new EmotionVector(valence, arousal);
    }

    /**
     * MoodInput(今の気分・なりたい気分)からMoodBridgeを組み立てる
     * @param input 今の気分・なりたい気分を持つ入力
     * @return 感情ベクトルの橋渡し
     */
    public MoodBridge buildBridge(MoodInput input) {
        EmotionVector start = analyze(input.getCurrentMood());
        EmotionVector goal = analyze(input.getTargetMood());
        return new MoodBridge(start, goal);
    }
}
