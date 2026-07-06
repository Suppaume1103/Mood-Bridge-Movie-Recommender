package jp.ac.dendai.ms;

import java.util.List;

/**
 * ユーザーの現在の気分と目標の気分、検索対象ジャンルを表すデータクラス
 */
public class MoodInput {

    private String currentMood; // 今の気分
    private String targetMood;  // なりたい気分
    private List<String> genres; // 検索対象ジャンル(1件以上必須)

    public MoodInput(String currentMood, String targetMood, List<String> genres){
        this.currentMood = currentMood;
        this.targetMood = targetMood;
        this.genres = genres;
    }

    public String getCurrentMood(){
        return currentMood;
    }

    public void setCurrentMood(String currentMood){
        this.currentMood = currentMood;
    }

    public String getTargetMood(){
        return targetMood;
    }

    public void setTargetMood(String targetMood){
        this.targetMood = targetMood;
    }

    public List<String> getGenres(){
        return genres;
    }

    public void setGenres(List<String> genres){
        this.genres = genres;
    }
}
