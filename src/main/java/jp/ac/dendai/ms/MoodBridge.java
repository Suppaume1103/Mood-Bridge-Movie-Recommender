package jp.ac.dendai.ms;

/**
 * 今の気分のEmotionVectorとなりたい気分のEmotionVectorをもつデータクラス
 */
public class MoodBridge {

    private EmotionVector start; // 今の気分
    private EmotionVector goal; // なりたい気分

    public MoodBridge(EmotionVector start, EmotionVector goal) {
        this.start = start;
        this.goal = goal;
    }

    public EmotionVector getStart() {
        return start;
    }

    public void setStart(EmotionVector start){
        this.start = start;
    }

    public EmotionVector getGoal(){
        return goal;
    }

    public void setGoal(EmotionVector goal){
        this.goal = goal;
    }

}
