import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MinesweeperGame extends JFrame {

    private static final int ROWS = 9;
    private static final int COLS = 9;
    private static final int NUM_MINES = 10;

    private JButton[][] buttons = new JButton[ROWS][COLS];
    private Cell[][] cells = new Cell[ROWS][COLS];
    private boolean gameOver = false;

    private JLabel statusLabel = new JLabel("Game in progress");
    private JLabel timerLabel = new JLabel("Time: 0");
    private Timer timer;
    private int elapsedSeconds = 0;
    private boolean timerStarted = false;

    public MinesweeperGame() {
        setTitle("Minesweeper");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(statusLabel, BorderLayout.CENTER);

        timerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(timerLabel, BorderLayout.WEST);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());
        topPanel.add(resetButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(ROWS, COLS));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton button = new JButton();
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setPreferredSize(new Dimension(50, 50));
                button.setFont(new Font("Arial", Font.BOLD, 16));
                button.setFocusPainted(false);
                button.setBackground(new Color(192, 192, 192));
                button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                button.addMouseListener(new CellMouseListener(row, col));
                buttons[row][col] = button;
                panel.add(button);
            }
        }

        add(panel, BorderLayout.CENTER);

        timer = new Timer(1000, e -> {
            elapsedSeconds++;
            timerLabel.setText("Time: " + elapsedSeconds);
        });

        initGame();
        setVisible(true);
    }

    private void initGame() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col] = new Cell();
            }
        }
        placeMines();
        calculateAdjacentMines();
    }

    private void resetGame() {
        gameOver = false;
        timer.stop();
        timerStarted = false;
        elapsedSeconds = 0;
        timerLabel.setText("Time: 0");
        statusLabel.setText("Game in progress");

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                buttons[row][col].setText("");
                buttons[row][col].setEnabled(true);
                buttons[row][col].setBackground(new Color(192, 192, 192));
                buttons[row][col].setForeground(Color.BLACK);
                buttons[row][col].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                cells[row][col] = new Cell();
            }
        }
        placeMines();
        calculateAdjacentMines();
    }

    private void placeMines() {
        Random random = new Random();
        int placedMines = 0;
        while (placedMines < NUM_MINES) {
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLS);
            if (!cells[row][col].isMine()) {
                cells[row][col].setMine(true);
                placedMines++;
            }
        }
    }

    private void calculateAdjacentMines() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (!cells[row][col].isMine()) {
                    int count = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int nRow = row + i;
                            int nCol = col + j;
                            if (nRow >= 0 && nRow < ROWS && nCol >= 0 && nCol < COLS) {
                                if (cells[nRow][nCol].isMine()) {
                                    count++;
                                }
                            }
                        }
                    }
                    cells[row][col].setAdjacentMines(count);
                }
            }
        }
    }

    private void revealCell(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return;
        if (cells[row][col].isRevealed() || gameOver || cells[row][col].isFlagged()) return;

        if (!timerStarted) {
            timerStarted = true;
            timer.start();
        }

        cells[row][col].setRevealed(true);
        JButton button = buttons[row][col];

        button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        button.setEnabled(false);
        button.setBackground(new Color(224, 224, 224));

        if (cells[row][col].isMine()) {
            button.setText("X");
            button.setForeground(Color.RED);
            gameOver = true;
            timer.stop();
            revealAllMines();
            statusLabel.setText("Game Over! You clicked a mine.");
            JOptionPane.showMessageDialog(this, "Game Over! You clicked a mine.");
        } else {
            int count = cells[row][col].getAdjacentMines();
            if (count > 0) {
                button.setText(String.valueOf(count));
                button.setForeground(getColorForNumber(count));
            } else {
                button.setText("");
                for (int i = -1; i <=1; i++) {
                    for (int j = -1; j <=1; j++) {
                        if(i != 0 || j != 0) {
                            revealCell(row + i, col + j);
                        }
                    }
                }
            }

            if (checkWin()) {
                gameOver = true;
                timer.stop();
                statusLabel.setText("Congratulations! You won the game!");
                JOptionPane.showMessageDialog(this, "Congratulations! You won the game!");
            }
        }
    }

    private void toggleFlag(int row, int col) {
        if (cells[row][col].isRevealed() || gameOver) return;

        JButton button = buttons[row][col];
        Cell cell = cells[row][col];
        if (cell.isFlagged()) {
            cell.setFlagged(false);
            button.setText("");
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        } else {
            cell.setFlagged(true);
            button.setText("F");
            button.setForeground(Color.BLUE);
            button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        }
    }

    private void revealAllMines() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].isMine()) {
                    JButton button = buttons[row][col];
                    button.setText("X");
                    button.setForeground(Color.RED);
                    button.setBackground(new Color(224, 224, 224));
                    button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    button.setEnabled(false);
                }
            }
        }
    }

    private boolean checkWin() {
        int revealedCount = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].isRevealed()) {
                    revealedCount++;
                }
            }
        }
        return revealedCount == (ROWS * COLS - NUM_MINES);
    }

    private Color getColorForNumber(int num) {
        switch (num) {
            case 1: return Color.BLUE;
            case 2: return new Color(0, 128, 0);
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128);
            case 5: return new Color(128, 0, 0);
            case 6: return new Color(64, 224, 208);
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }

    private class CellMouseListener extends MouseAdapter {
        private final int row;
        private final int col;
        private boolean rightClickPressed = false;

        public CellMouseListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!gameOver && !cells[row][col].isRevealed()) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    buttons[row][col].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    rightClickPressed = true;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!gameOver && !cells[row][col].isRevealed()) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    buttons[row][col].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                    revealCell(row, col);
                } else if (SwingUtilities.isRightMouseButton(e) && rightClickPressed) {
                    toggleFlag(row, col);
                }
            }
            rightClickPressed = false;
        }
    }

    private static class Cell {
        private boolean isMine;
        private int adjacentMines;
        private boolean revealed;
        private boolean flagged;

        public boolean isMine() { return isMine; }
        public void setMine(boolean mine) { isMine = mine; }

        public int getAdjacentMines() { return adjacentMines; }
        public void setAdjacentMines(int count) { adjacentMines = count; }

        public boolean isRevealed() { return revealed; }
        public void setRevealed(boolean val) { revealed = val; }

        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean val) { flagged = val; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MinesweeperGame::new);
    }
}
