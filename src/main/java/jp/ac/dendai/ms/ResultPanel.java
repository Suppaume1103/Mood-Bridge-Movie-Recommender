package jp.ac.dendai.ms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * 推薦結果(映画情報・推薦理由・感情曲線・重要語)を表示するパネル。
 */
public class ResultPanel extends JPanel {

    private final JLabel movieTitleLabel = new JLabel();
    private final JLabel movieInfoLabel = new JLabel();
    private final JTextArea reasonArea = new JTextArea(4, 40);
    private final ArcChartPanel arcChart = new ArcChartPanel();
    private final JLabel keywordsLabel = new JLabel();
    private final JButton backButton = new JButton("入力に戻る");

    public ResultPanel() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        movieTitleLabel.setFont(movieTitleLabel.getFont().deriveFont(Font.BOLD, 16f));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(movieTitleLabel);
        headerPanel.add(movieInfoLabel);

        reasonArea.setEditable(false);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(BorderFactory.createTitledBorder("推薦理由"));

        arcChart.setBorder(BorderFactory.createTitledBorder("感情曲線(valence)"));

        keywordsLabel.setBorder(BorderFactory.createTitledBorder("レビュー重要語"));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(reasonArea);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(arcChart);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(keywordsLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void display(Recommendation rec) {
        Movie movie = rec.getMovie();
        movieTitleLabel.setText(movie.getTitle());

        String genres = String.join("・", movie.getGenres());
        String year = movie.getReleaseYear() > 0 ? String.valueOf(movie.getReleaseYear()) : "公開年不明";
        movieInfoLabel.setText(String.format("%s ／ %s ／ TMDB %.1f", year, genres, movie.getRating()));

        reasonArea.setText(rec.getReason());

        arcChart.setArc(rec.getArc());

        List<String> keywords = rec.getKeywords();
        keywordsLabel.setText(keywords != null ? String.join(", ", keywords) : "");
    }

    public void clear() {
        movieTitleLabel.setText("");
        movieInfoLabel.setText("");
        reasonArea.setText("");
        arcChart.setArc(null);
        keywordsLabel.setText("");
    }

    /**
     * 「入力に戻る」ボタン押下時の処理を登録する(MainWindowがCardLayout切り替えと
     * AppController.onReset()を行うためのフック)。
     */
    public void addBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
