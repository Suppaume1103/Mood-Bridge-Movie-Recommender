package jp.ac.dendai.ms;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * アプリ全体を統合するウィンドウ。CardLayoutで入力・処理中・結果の3画面を切り替える。
 * エラーはカードではなくJOptionPaneで表示する。
 */
public class MainWindow extends JFrame {

    private static final String CARD_INPUT = "input";
    private static final String CARD_LOADING = "loading";
    private static final String CARD_RESULT = "result";

    private final AppController controller;
    private final InputPanel inputPanel = new InputPanel();
    private final ResultPanel resultPanel = new ResultPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public MainWindow(AppController controller) {
        super("気分の架け橋");
        this.controller = controller;

        cards.add(inputPanel, CARD_INPUT);
        cards.add(buildLoadingPanel(), CARD_LOADING);
        cards.add(resultPanel, CARD_RESULT);

        inputPanel.addSubmitListener(e -> onSubmit());
        resultPanel.addBackListener(e -> onBackToInput());

        setContentPane(cards);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(760, 640));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildLoadingPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel("感情曲線を推定しています…しばらくお待ちください。");
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        return panel;
    }

    private void onSubmit() {
        MoodInput input = inputPanel.getMoodInput();
        if (input.getGenres() == null || input.getGenres().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ジャンルを1件以上選択してください。", "入力エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cardLayout.show(cards, CARD_LOADING);

        SwingWorker<Recommendation, Void> worker = new SwingWorker<>() {
            @Override
            protected Recommendation doInBackground() {
                return controller.onSubmit(input);
            }

            @Override
            protected void done() {
                try {
                    Recommendation rec = get();
                    resultPanel.display(rec);
                    cardLayout.show(cards, CARD_RESULT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showErrorAndReturnToInput("処理が中断されました。");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    String message = (cause != null) ? cause.getMessage() : e.getMessage();
                    showErrorAndReturnToInput(message);
                }
            }
        };
        worker.execute();
    }

    private void showErrorAndReturnToInput(String message) {
        Object[] options = {"入力に戻る", "再試行"};
        int choice = JOptionPane.showOptionDialog(this, message, "エラー",
                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        cardLayout.show(cards, CARD_INPUT);
        if (choice == 1) {
            onSubmit();
        }
    }

    private void onBackToInput() {
        controller.onReset();
        cardLayout.show(cards, CARD_INPUT);
    }

    /**
     * ウィンドウを表示する。
     * メソッド名をshow()にすると、Component.setVisible()の内部実装が
     * (後方互換のため)show()を呼び出す関係で無限再帰(StackOverflowError)に
     * なるため、displayという名前にしている。
     */
    public void display() {
        setVisible(true);
    }
}
