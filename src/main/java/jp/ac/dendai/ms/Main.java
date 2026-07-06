package jp.ac.dendai.ms;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GptClient gpt = new GptClient();
            AppController controller = new AppController(gpt);
            MainWindow window = new MainWindow(controller);
            window.display();
        });
    }
}
