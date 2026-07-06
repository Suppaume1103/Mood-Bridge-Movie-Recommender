package jp.ac.dendai.ms;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

/**
 * 気分入力(今の気分・なりたい気分)とジャンル選択を行うパネル。
 */
public class InputPanel extends JPanel {

    private final JTextArea currentMoodField = new JTextArea(3, 40);
    private final JTextArea targetMoodField = new JTextArea(3, 40);
    private final JList<String> genreList = new JList<>(TmdbClient.DISPLAY_GENRES.toArray(String[]::new));
    private final JButton clearButton = new JButton("クリア");
    private final JButton submitButton = new JButton("映画を探す");

    public InputPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        currentMoodField.setLineWrap(true);
        currentMoodField.setWrapStyleWord(true);
        JPanel currentMoodPanel = new JPanel(new BorderLayout());
        currentMoodPanel.setBorder(BorderFactory.createTitledBorder("今の気分"));
        currentMoodPanel.add(new JScrollPane(currentMoodField), BorderLayout.CENTER);

        targetMoodField.setLineWrap(true);
        targetMoodField.setWrapStyleWord(true);
        JPanel targetMoodPanel = new JPanel(new BorderLayout());
        targetMoodPanel.setBorder(BorderFactory.createTitledBorder("なりたい気分"));
        targetMoodPanel.add(new JScrollPane(targetMoodField), BorderLayout.CENTER);

        genreList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        genreList.setVisibleRowCount(6);
        JPanel genrePanel = new JPanel(new BorderLayout());
        genrePanel.setBorder(BorderFactory.createTitledBorder("ジャンル(Ctrl+クリックで複数選択・1件以上必須)"));
        genrePanel.add(new JScrollPane(genreList), BorderLayout.CENTER);

        clearButton.addActionListener(e -> clearFields());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(submitButton);

        add(currentMoodPanel);
        add(Box.createVerticalStrut(8));
        add(targetMoodPanel);
        add(Box.createVerticalStrut(8));
        add(genrePanel);
        add(Box.createVerticalStrut(8));
        add(buttonPanel);
    }

    public MoodInput getMoodInput() {
        List<String> selectedGenres = genreList.getSelectedValuesList();
        return new MoodInput(currentMoodField.getText(), targetMoodField.getText(), selectedGenres);
    }

    public void clearFields() {
        currentMoodField.setText("");
        targetMoodField.setText("");
        genreList.clearSelection();
    }

    /**
     * 「映画を探す」ボタン押下時の処理を登録する(MainWindowが入力検証・
     * SwingWorker起動を行うためのフック)。
     */
    public void addSubmitListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }
}
