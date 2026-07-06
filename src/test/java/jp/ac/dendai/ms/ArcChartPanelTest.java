package jp.ac.dendai.ms;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * ArcChartPanelの表示確認用テストクラス(手動確認)。
 * 外部APIを使わず、ダミーのEmotionalArcを描画して目視で確認する。
 * PASS/FAIL判定はできないため、折れ線グラフが正しく表示されるかを
 * ウィンドウで確認すること。
 */
public class ArcChartPanelTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ArcChartPanel panel = new ArcChartPanel();
            panel.setArc(new EmotionalArc(Arrays.asList(-0.8, -0.5, -0.3, 0.1, 0.4, 0.6, 0.75, 0.9)));

            JFrame frame = new JFrame("ArcChartPanel テスト");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
