package jp.ac.dendai.ms;

import java.util.List;
import java.util.Map;

/**
 * MoodBridge(今の気分となりたい気分)と映画候補群から、
 * 感情曲線が最もmood bridgeに近い映画を選ぶクラス
 */
public class BridgeSelector {

    public BridgeSelector() {
    }

    /**
     * 映画候補群の中から、感情曲線の始点が今の気分に、終点がなりたい気分に
     * 最も近い映画を選びRecommendationを組み立てる。
     * reasonはこの時点では未設定(空文字列)とし、呼び出し側がReasonGeneratorで
     * 生成した文字列をRecommendation.setReason()で設定する想定。
     *
     * @param bridge 今の気分となりたい気分
     * @param movies 映画候補のリスト
     * @param arcs   映画ごとの感情曲線(EmotionalArcEstimatorで事前に推定したもの)
     * @return 選ばれた映画のRecommendation(reasonは空文字列)
     */
    public Recommendation select(MoodBridge bridge, List<Movie> movies, Map<Movie, EmotionalArc> arcs) {
        Movie bestMovie = null;
        EmotionalArc bestArc = null;
        double bestDistance = Double.MAX_VALUE;

        for (Movie movie : movies) {
            EmotionalArc arc = arcs.get(movie);
            if (arc == null || arc.getPoints() == null || arc.getPoints().isEmpty()) {
                continue;
            }

            List<?> points = arc.getPoints();
            double firstPoint = (Double) points.get(0);
            double lastPoint = (Double) points.get(points.size() - 1);

            double arcDelta = lastPoint - firstPoint;
            double desiredDelta = bridge.getGoal().getValance() - bridge.getStart().getValance();

            double endpointDistance = Math.abs(bridge.getStart().getValance() - firstPoint)
                    + Math.abs(bridge.getGoal().getValance() - lastPoint);
            // 方向の違いによるペナルティを加味する 変化の向き・大きさのズレも評価
            double directionPenalty = Math.abs(desiredDelta - arcDelta);

            double distance = endpointDistance + directionPenalty;

            if (distance < bestDistance) {
                bestDistance = distance;
                bestMovie = movie;
                bestArc = arc;
            }
        }

        if (bestMovie == null) {
            throw new IllegalStateException("感情曲線が推定できた映画候補がありません");
        }

        return new Recommendation(bestArc, bestMovie, "");
    }
}
