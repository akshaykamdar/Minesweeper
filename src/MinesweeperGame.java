import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;

public class MinesweeperGame extends JFrame {

    private static final int ROWS = 9;
    private static final int COLS = 9;

    public MinesweeperGame() {
        setTitle("Minesweeper");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(ROWS, COLS));

        // Add buttons or cells later for the grid here

        add(panel);
    }

    private void createAndShowGUI() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinesweeperGame game = new MinesweeperGame();
            game.createAndShowGUI();
        });
    }
}