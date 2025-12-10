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
    
    // UI Components
    private JLabel statusLabel;
    
    // Thay vì 1 label chung, ta tách ra 2 label riêng cho Đen và Trắng
    private JLabel lblBlackScore;
    private JLabel lblWhiteScore; 
    private JPanel boardPanel;

    // --- MÀU SẮC ---
    private static final Color BOARD_BG = new Color(0, 100, 0); 
    private static final Color GRID_COLOR = new Color(0, 50, 0); 
    private static final Color PIECE_BLACK = Color.BLACK;
    private static final Color PIECE_WHITE = Color.WHITE;
    private static final Color HINT_COLOR = new Color(0, 0, 0, 80); 
    private static final Color STATUS_BG = new Color(40, 40, 40);

    public Othello() {
        setTitle("Othello Classic");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 720);
        setLayout(new BorderLayout());

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
                        if (controller != null) controller.processMove(r, c);
                    }
                });
                boardPanel.add(boardSquares[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
    }

    // --- CẬP NHẬT: Thanh trạng thái chia rõ 2 màu Đen/Trắng ---
    private void initStatusBar() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        statusPanel.setBackground(STATUS_BG);

        // 1. Bên trái: Hiển thị lượt đi
        statusLabel = new JLabel("Lượt: ĐEN");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);

        // 2. Bên phải: Panel chứa 2 ô điểm số
        JPanel scoreContainer = new JPanel(new GridLayout(1, 2, 15, 0)); // Cách nhau 15px
        scoreContainer.setOpaque(false); // Trong suốt để lấy màu nền cha

        // Tạo ô điểm ĐEN (Nền đen, chữ trắng)
        lblBlackScore = new JLabel("ĐEN: 2", SwingConstants.CENTER);
        lblBlackScore.setOpaque(true); // Để hiển thị màu nền
        lblBlackScore.setBackground(Color.BLACK);
        lblBlackScore.setForeground(Color.WHITE);
        lblBlackScore.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBlackScore.setPreferredSize(new Dimension(100, 35));
        lblBlackScore.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Tạo ô điểm TRẮNG (Nền trắng, chữ đen)
        lblWhiteScore = new JLabel("TRẮNG: 2", SwingConstants.CENTER);
        lblWhiteScore.setOpaque(true);
        lblWhiteScore.setBackground(Color.WHITE);
        lblWhiteScore.setForeground(Color.BLACK);
        lblWhiteScore.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWhiteScore.setPreferredSize(new Dimension(100, 35));
        lblWhiteScore.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        scoreContainer.add(lblBlackScore);
        scoreContainer.add(lblWhiteScore);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(scoreContainer, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setCurrentPlayer(int p) {
        // Cập nhật text và làm nổi bật ô điểm của người đang chơi
        if (p == Player.BLACK) {
            statusLabel.setText("Lượt: ĐEN");
            lblBlackScore.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2)); // Viền cam báo hiệu lượt
            lblWhiteScore.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        } else {
            statusLabel.setText("Lượt: TRẮNG");
            lblWhiteScore.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
            lblBlackScore.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }

    public void updateBoard(int[][] modelBoard, boolean[][] validMoves) {
        int blackCount = 0;
        int whiteCount = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int piece = modelBoard[i][j];
                if (piece == Player.BLACK) blackCount++;
                if (piece == Player.WHITE) whiteCount++;

                boardSquares[i][j].setPiece(piece);
                boardSquares[i][j].setHint(validMoves[i][j]);
            }
        }
        
        // Cập nhật số điểm vào 2 ô riêng biệt
        lblBlackScore.setText("ĐEN: " + blackCount);
        lblWhiteScore.setText("TRẮNG: " + whiteCount);
        
        boardPanel.repaint();
    }

    // --- Dialog Kết thúc Game ---
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
            controller.resetGame();
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

    // --- BoardSquare: Vẽ 2D Phẳng ---
    private class BoardSquare extends JPanel {
        private int row, col;
        private int piece = Player.EMPTY;
        private boolean isHint = false;

        public BoardSquare(int row, int col) {
            this.row = row;
            this.col = col;
            this.setBackground(BOARD_BG);
            this.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));
        }

        public void setPiece(int p) { this.piece = p; }
        public void setHint(boolean hint) { this.isHint = hint; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margin = 6;

            if (piece != Player.EMPTY) {
                g2.setColor(piece == Player.BLACK ? PIECE_BLACK : PIECE_WHITE);
                g2.fillOval(margin, margin, w - 2 * margin, h - 2 * margin);
                
                g2.setStroke(new BasicStroke(1));
                if (piece == Player.WHITE) {
                    g2.setColor(Color.LIGHT_GRAY);
                } else {
                    g2.setColor(new Color(50,50,50));
                }
                g2.drawOval(margin, margin, w - 2 * margin, h - 2 * margin);
            } 
            else if (isHint) {
                g2.setColor(HINT_COLOR);
                int hintSize = w / 5;
                g2.fillOval((w - hintSize) / 2, (h - hintSize) / 2, hintSize, hintSize);
            }
        }
    }
}