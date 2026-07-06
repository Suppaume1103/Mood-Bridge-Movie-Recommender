package jp.ac.dendai.ms;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

/**
 * EmotionalArcのvalence推移を折れ線グラフで描画するパネル。
 */
public class ArcChartPanel extends JPanel {

    private static final int MARGIN_LEFT = 40;
    private static final int MARGIN_RIGHT = 20;
    private static final int MARGIN_TOP = 20;
    private static final int MARGIN_BOTTOM = 30;

    private EmotionalArc arc;

    public ArcChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(660, 200));
    }

    public void setArc(EmotionalArc arc) {
        this.arc = arc;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (arc == null || arc.getPoints() == null || arc.getPoints().isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int plotLeft = MARGIN_LEFT;
        int plotRight = width - MARGIN_RIGHT;
        int plotTop = MARGIN_TOP;
        int plotBottom = height - MARGIN_BOTTOM;
        int zeroY = plotTop + (plotBottom - plotTop) / 2;

        drawAxesAndLabels(g2, plotLeft, plotRight, plotTop, plotBottom, zeroY);
        drawArcLine(g2, plotLeft, plotRight, plotTop, zeroY);
    }

    private void drawAxesAndLabels(Graphics2D g2, int plotLeft, int plotRight, int plotTop, int plotBottom, int zeroY) {
        g2.setColor(Color.BLACK);
        g2.drawLine(plotLeft, plotTop, plotLeft, plotBottom);
        g2.drawLine(plotLeft, plotBottom, plotRight, plotBottom);

        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{3, 3}, 0));
        g2.drawLine(plotLeft, zeroY, plotRight, zeroY);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Tahoma", Font.PLAIN, 10));
        g2.drawString("+1", plotLeft - 24, plotTop + 4);
        g2.drawString("0", plotLeft - 14, zeroY + 4);
        g2.drawString("-1", plotLeft - 20, plotBottom + 4);
        g2.drawString("冒頭", plotLeft, plotBottom + 18);
        g2.drawString("結末", plotRight - 20, plotBottom + 18);
    }

    private void drawArcLine(Graphics2D g2, int plotLeft, int plotRight, int plotTop, int zeroY) {
        List<?> points = arc.getPoints();
        int n = points.size();
        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            double valence = (Double) points.get(i);
            double t = (n == 1) ? 0 : (double) i / (n - 1);
            xs[i] = plotLeft + (int) (t * (plotRight - plotLeft));
            double clamped = Math.max(-1.0, Math.min(1.0, valence));
            ys[i] = zeroY - (int) (clamped * (zeroY - plotTop));
        }

        g2.setColor(new Color(0x00, 0x33, 0xCC));
        g2.setStroke(new BasicStroke(2));
        g2.drawPolyline(xs, ys, n);
        for (int i = 0; i < n; i++) {
            g2.fillOval(xs[i] - 3, ys[i] - 3, 6, 6);
        }
    }
}
