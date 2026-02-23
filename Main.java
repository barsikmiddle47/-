import ui.TodoFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TodoFrame frame = new TodoFrame();
            frame.setVisible(true);
        });
    }
}
