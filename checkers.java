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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);
        }

        private void drawBoard(Graphics g) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    boolean isLightSquare = (row + col) % 2 == 0;
                    g.setColor(isLightSquare ? Color.WHITE : Color.GRAY);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                    Piece piece = board.getPiece(row, col);
                    if (piece != null) {
                        drawPiece(g, piece, col * TILE_SIZE, row * TILE_SIZE);
                    }
                }
            }
        }

        private void drawPiece(Graphics g, Piece piece, int x, int y) {
            g.setColor(piece.getColor());
            g.fillOval(x + 10, y + 10, TILE_SIZE - 20, TILE_SIZE - 20);
            if (piece.isKing()) {
                g.setColor(Color.YELLOW);
                g.fillOval(x + TILE_SIZE / 4, y + TILE_SIZE / 4, TILE_SIZE / 2, TILE_SIZE / 2);
            }
        }

        private void handleMouseClick(int x, int y) {
            int row = y / TILE_SIZE;
            int col = x / TILE_SIZE;

            if (selectedPiece == null) {
                // Select a piece
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor().equals(isWhiteTurn ? Color.WHITE : Color.BLACK)) {
                    selectedPiece = piece;
                    highlightValidMoves(row, col);
                }
            } else {
                // Try to move the piece
                if (isValidMove(selectedPiece, row, col)) {
                    board.movePiece(selectedPiece, row, col);
                    selectedPiece = null;
                    isWhiteTurn = !isWhiteTurn;
                    repaint();
                    checkForKingPromotion(row, col);
                } else if (isValidCapture(selectedPiece, row, col)) {
                    board.capturePiece(selectedPiece, row, col);
                    selectedPiece = null;
                    isWhiteTurn = !isWhiteTurn;
                    repaint();
                } else {
                    selectedPiece = null;
                    repaint();
                }
            }
        }

        private void highlightValidMoves(int row, int col) {
            // Highlight valid regular moves and jumps for the selected piece
            // This can be extended with more detailed UI highlighting
        }

        private boolean isValidMove(Piece piece, int row, int col) {
            // Regular move validation (must be diagonal and one square away)
            if (Math.abs(piece.getRow() - row) == 1 && Math.abs(piece.getCol() - col) == 1) {
                if (board.getPiece(row, col) == null) {
                    return true;
                }
            }
            return false;
        }

        private boolean isValidCapture(Piece piece, int row, int col) {
            // Jump capture validation (must jump over an opponent's piece)
            if (Math.abs(piece.getRow() - row) == 2 && Math.abs(piece.getCol() - col) == 2) {
                int midRow = (piece.getRow() + row) / 2;
                int midCol = (piece.getCol() + col) / 2;
                Piece midPiece = board.getPiece(midRow, midCol);
                if (midPiece != null && midPiece.getColor() != piece.getColor() && board.getPiece(row, col) == null) {
                    return true;
                }
            }
            return false;
        }

        private void checkForKingPromotion(int row, int col) {
            // Check if a piece has reached the last row and should be promoted to a king
            Piece piece = board.getPiece(row, col);
            if (piece != null && (row == 0 || row == BOARD_SIZE - 1) && !piece.isKing()) {
                piece.makeKing();
            }
        }
    }

    private static class Board {
        private Piece[][] board;

        public Board() {
            board = new Piece[BOARD_SIZE][BOARD_SIZE];
            initializePieces();
        }

        private void initializePieces() {
            // Place white pieces
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if ((row + col) % 2 != 0) {
                        board[row][col] = new Piece(row, col, Color.WHITE);
                    }
                }
            }

            // Place black pieces
            for (int row = 5; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if ((row + col) % 2 != 0) {
                        board[row][col] = new Piece(row, col, Color.BLACK);
                    }
                }
            }
        }

        public Piece getPiece(int row, int col) {
            return board[row][col];
        }

        public void movePiece(Piece piece, int newRow, int newCol) {
            board[piece.getRow()][piece.getCol()] = null;
            piece.setRow(newRow);
            piece.setCol(newCol);
            board[newRow][newCol] = piece;
        }

        public void capturePiece(Piece piece, int newRow, int newCol) {
            int midRow = (piece.getRow() + newRow) / 2;
            int midCol = (piece.getCol() + newCol) / 2;
            board[midRow][midCol] = null;  // Remove captured piece
            movePiece(piece, newRow, newCol);
        }

        public void removePiece(int row, int col) {
            board[row][col] = null;
        }

        public boolean isEmpty(int row, int col) {
            return board[row][col] == null;
        }
    }

    private static class Piece {
        private int row, col;
        private Color color;
        private boolean isKing;

        public Piece(int row, int col, Color color) {
            this.row = row;
            this.col = col;
            this.color = color;
            this.isKing = false;
        }

        public Color getColor() {
            return color;
        }

        public boolean isKing() {
            return isKing;
        }

        public void makeKing() {
            isKing = true;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CheckersGame game = new CheckersGame();
            game.setVisible(true);
        });
    }
}
