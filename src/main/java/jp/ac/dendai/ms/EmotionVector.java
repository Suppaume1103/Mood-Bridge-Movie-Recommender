package jp.ac.dendai.ms;

/**
 * 感情の正負と活性度を持つデータクラス
 */
public class EmotionVector {
    
    private double valance; // 感情の正負 (-1.0~1.0)
    private double arousal; // 活性度 (0.0~1.0)

    public EmotionVector(double valance, double arousal){
        this.valance = valance;
        this.arousal = arousal;
    }

    public double getValance() {
        return valance;
    }

    public void setValance(double valance) {
        this.valance = valance;
    }

    public double getArousal() {
        return arousal;
    }

    public void setArousal(double arousal) {
        this.arousal = arousal;
    }
}
