import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MinesweeperGame extends JFrame {

    private static final int ROWS = 9;
    private static final int COLS = 9;
    private JButton[][] buttons = new JButton[ROWS][COLS];

    public MinesweeperGame() {
        setTitle("Minesweeper");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(ROWS, COLS));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton button = new JButton();
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setPreferredSize(new Dimension(50, 50));
                // Add action listener for clicks
                button.addActionListener(new CellButtonListener(row, col));
                buttons[row][col] = button;
                panel.add(button);
            }
        }

        add(panel);
    }

    private void createAndShowGUI() {
        setVisible(true);
    }

    private class CellButtonListener implements ActionListener {
        private int row;
        private int col;

        public CellButtonListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // For now, just show row and col of the clicked button
            JOptionPane.showMessageDialog(null, "Cell clicked at: (" + row + ", " + col + ")");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinesweeperGame game = new MinesweeperGame();
            game.createAndShowGUI();
        });
    }
}