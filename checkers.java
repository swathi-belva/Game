import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CheckersGame extends JFrame {
    private static final int BOARD_SIZE = 8; // 8x8 board
    private static final int TILE_SIZE = 80; // Size of each tile
    private Board board;
    private boolean isWhiteTurn = true;
    private Piece selectedPiece = null;

    public CheckersGame() {
        this.board = new Board();
        this.setTitle("Checkers Game");
        this.setSize(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(new BoardPanel());
    }

    // Panel that will draw the board and handle user interaction
    private class BoardPanel extends JPanel {
        public BoardPanel() {
            this.setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMouseClick(e.getX(), e.getY());
                }
            });
        }


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CheckersGame game = new CheckersGame();
            game.setVisible(true);
        });
    }
}
