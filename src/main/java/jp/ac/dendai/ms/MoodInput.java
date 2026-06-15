package jp.ac.dendai.ms;

/**
 * ユーザーの現在の気分と目標の気分を表すデータクラス
 */
public class MoodInput {
    
    private String currentMood; // 今の気分
    private String targetMood;  // なりたい気分

    public MoodInput(String currentMood, String targetMood){
        this.currentMood = currentMood;
        this.targetMood = targetMood;
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
}
