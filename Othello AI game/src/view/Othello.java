package view;

import controller.Controller;
import model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class Othello extends JFrame {
    private BoardSquare[][] boardSquares = new BoardSquare[8][8];
    private Controller controller;
    
    // Lưu bàn cờ cũ để so sánh tìm thay đổi
    private int[][] previousBoard = new int[8][8]; 
    
    // UI Components
    private JLabel statusLabel;
    private JLabel lblBlackScore;
    private JLabel lblWhiteScore; 
    private JPanel boardPanel;

    // Màu sắc
    private static final Color BOARD_BG = new Color(0, 100, 0); 
    private static final Color GRID_COLOR = new Color(0, 50, 0); 
    private static final Color PIECE_BLACK = Color.BLACK;
    private static final Color PIECE_WHITE = Color.WHITE;
    private static final Color HINT_COLOR = new Color(0, 0, 0, 80); 
    private static final Color STATUS_BG = new Color(40, 40, 40);
    private static final Color HIGHLIGHT_COLOR = Color.red;

    public Othello() {
        setTitle("Othello Classic");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 720);
        setLayout(new BorderLayout());

        // Khởi tạo bàn cờ cũ
        for(int i=0; i<8; i++) 
            for(int j=0; j<8; j++) 
                previousBoard[i][j] = Player.EMPTY;

        initMenu();
        initBoard();
        initStatusBar();

        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Tùy chọn");
        JMenuItem resetItem = new JMenuItem("Chơi Lại");
        resetItem.addActionListener(e -> {
            if (controller != null) controller.resetGame();
        });
        gameMenu.add(resetItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    private void initBoard() {
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8, 8));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 4));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardSquares[i][j] = new BoardSquare(i, j);
                int r = i;
                int c = j;
                boardSquares[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (controller != null) controller.processMove(r, c);
                        }
                    }
                });
                boardPanel.add(boardSquares[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
    }

    private void initStatusBar() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        statusPanel.setBackground(STATUS_BG);

        statusLabel = new JLabel("Lượt: ĐEN");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);

        JPanel scoreContainer = new JPanel(new GridLayout(1, 2, 15, 0)); 
        scoreContainer.setOpaque(false);

        lblBlackScore = createScoreLabel("ĐEN: 2", Color.BLACK, Color.WHITE);
        lblWhiteScore = createScoreLabel("TRẮNG: 2", Color.WHITE, Color.BLACK);

        scoreContainer.add(lblBlackScore);
        scoreContainer.add(lblWhiteScore);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(scoreContainer, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createScoreLabel(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setPreferredSize(new Dimension(100, 35));
        lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return lbl;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setCurrentPlayer(int p) {
        if (p == Player.BLACK) {
            statusLabel.setText("Lượt: ĐEN");
            lblBlackScore.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
            lblWhiteScore.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        } else {
            statusLabel.setText("Lượt: TRẮNG");
            lblWhiteScore.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
            lblBlackScore.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }

    //  update bang
    public void updateBoard(int[][] modelBoard, boolean[][] validMoves) {
        int blackCount = 0;
        int whiteCount = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int newPiece = modelBoard[i][j];
                int oldPiece = previousBoard[i][j];

                if (newPiece == Player.BLACK) blackCount++;
                if (newPiece == Player.WHITE) whiteCount++;

                //  Phát hiện quân vừa bị lật thay đổi màu
                boolean isFlipped = (oldPiece != Player.EMPTY) 
                                 && (oldPiece != newPiece) 
                                 && (newPiece != Player.EMPTY);

               
                boolean isLastMove = (oldPiece == Player.EMPTY) && (newPiece != Player.EMPTY);

                // Reset trạng thái khi bắt đầu game mới
                if (countPieces(modelBoard) <= 4) {
                    isFlipped = false;
                    isLastMove = false;
                }

                boardSquares[i][j].setPiece(newPiece);
                boardSquares[i][j].setHint(validMoves[i][j]);
                
                //Truyền cả 2 trạng thái xuống View con
                boardSquares[i][j].setFlippedState(isFlipped);
                boardSquares[i][j].setLastMoveState(isLastMove);
                
                // Cập nhật lại mảng lưu trữ
                previousBoard[i][j] = newPiece;
            }
        }
        
        lblBlackScore.setText("ĐEN: " + blackCount);
        lblWhiteScore.setText("TRẮNG: " + whiteCount);
        boardPanel.repaint();
    }
    
    private int countPieces(int[][] board) {
        int count = 0;
        for(int[] row : board) for(int p : row) if(p != Player.EMPTY) count++;
        return count;
    }

    public void showGameOverDialog(String winner, int blackScore, int whiteScore) {
        JDialog dialog = new JDialog(this, "Kết Quả", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        JLabel lblWinner = new JLabel(winner);
        lblWinner.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblWinner.setForeground(new Color(0, 100, 0));

        JLabel lblScore = new JLabel("ĐEN: " + blackScore + "  -  TRẮNG: " + whiteScore);
        lblScore.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblScore.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(lblWinner);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(lblScore);

        JButton btnNewGame = new JButton("CHƠI VÁN MỚI");
        btnNewGame.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNewGame.setBackground(new Color(255, 140, 0));
        btnNewGame.setForeground(Color.WHITE);
        btnNewGame.setFocusPainted(false);
        btnNewGame.setBorderPainted(false);
        btnNewGame.setPreferredSize(new Dimension(180, 45));
        
        btnNewGame.addActionListener(e -> {
            for(int i=0; i<8; i++) 
                for(int j=0; j<8; j++) 
                    previousBoard[i][j] = Player.EMPTY;
            
            if (controller != null) controller.resetGame();
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 25, 0));
        buttonPanel.add(btnNewGame);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // --- BoardSquare: Vẽ ô cờ ---
    private class BoardSquare extends JPanel {
        private int row, col;
        private int piece = Player.EMPTY;
        private boolean isHint = false;
        private boolean isFlipped = false;
        // [MỚI] Biến theo dõi quân vừa đánh
        private boolean isLastMove = false; 

        public BoardSquare(int row, int col) {
            this.row = row;
            this.col = col;
            this.setBackground(BOARD_BG);
            this.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));
        }

        public void setPiece(int p) { this.piece = p; }
        public void setHint(boolean hint) { this.isHint = hint; }
        public void setFlippedState(boolean flipped) { this.isFlipped = flipped; }
        // [MỚI] Setter cho last move
        public void setLastMoveState(boolean lastMove) { this.isLastMove = lastMove; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margin = 6;

            if (piece != Player.EMPTY) {
                // Vẽ quân cờ
                g2.setColor(piece == Player.BLACK ? PIECE_BLACK : PIECE_WHITE);
                g2.fillOval(margin, margin, w - 2 * margin, h - 2 * margin);
                
                // Vẽ viền cơ bản (mỏng, xám)
                g2.setStroke(new BasicStroke(1));
                g2.setColor(piece == Player.WHITE ? Color.LIGHT_GRAY : new Color(50,50,50));
                g2.drawOval(margin, margin, w - 2 * margin, h - 2 * margin);

                // Nếu là quân vừa đánh (đầu) HOẶC quân vừa bị lật (cuối)
                if (isLastMove || isFlipped) {
                    g2.setColor(HIGHLIGHT_COLOR); 
                    g2.setStroke(new BasicStroke(3)); 
                    g2.drawOval(margin, margin, w - 2 * margin, h - 2 * margin);
                }
            } 
            else if (isHint) {
                g2.setColor(HINT_COLOR);
                int hintSize = w / 5;
                g2.fillOval((w - hintSize) / 2, (h - hintSize) / 2, hintSize, hintSize);
            }
        }
    }
}