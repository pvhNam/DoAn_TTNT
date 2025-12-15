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
    
    private int[][] previousBoard = new int[8][8]; 
    
    private JLabel statusLabel;
    private ModernScorePanel pnlBlackScore;   
    private ModernScorePanel pnlWhiteScore;
    private JPanel boardPanel;

    // --- COLOR PALETTE ---
    private static final Color APP_BG = new Color(245, 245, 245); 
    private static final Color BOARD_COLOR = new Color(39, 110, 50); 
    private static final Color GRID_COLOR = new Color(20, 60, 20); 
    
    private static final Color PIECE_BLACK = new Color(20, 20, 20); 
    private static final Color PIECE_WHITE = new Color(240, 240, 240); 
    
    // Màu Highlight
    private static final Color LAST_MOVE_BORDER = Color.RED; // Đỏ (Quân mới đánh)
    private static final Color FLIPPED_BORDER = Color.RED;   
    private static final Color HINT_COLOR = new Color(0, 0, 0, 60);

    public Othello() {
        setTitle("Othello Pro 2025");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 950); 
        setLayout(new BorderLayout());
        getContentPane().setBackground(APP_BG);

        for(int i=0; i<8; i++) 
            for(int j=0; j<8; j++) 
                previousBoard[i][j] = Player.EMPTY;

        initMenu();
        initMainLayout();

        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(650, 750)); 
        setVisible(true);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        
        JMenu gameMenu = new JMenu("Tùy Chọn");
        gameMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JMenuItem resetItem = new JMenuItem("Ván Mới");
        resetItem.addActionListener(e -> {
            if (controller != null) controller.resetGame();
        });
        
        gameMenu.add(resetItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    private void initMainLayout() {
        // CENTER: Bàn cờ
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(APP_BG);
        
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8, 8));
        boardPanel.setPreferredSize(new Dimension(640, 640)); 
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 2), 
            BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(30, 80, 30))
        ));

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
        centerPanel.add(boardPanel);
        add(centerPanel, BorderLayout.CENTER);

        // SOUTH: Score Board
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(APP_BG);
        bottomPanel.setBorder(new EmptyBorder(10, 40, 30, 40));

        statusLabel = new JLabel("SẴN SÀNG", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel scoreContainer = new JPanel(new GridLayout(1, 2, 40, 0)); 
        scoreContainer.setOpaque(false);

        pnlBlackScore = new ModernScorePanel("ĐEN", PIECE_BLACK);
        pnlWhiteScore = new ModernScorePanel("TRẮNG", PIECE_WHITE);

        scoreContainer.add(pnlBlackScore);
        scoreContainer.add(pnlWhiteScore);

        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(scoreContainer, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setCurrentPlayer(int p) {
        if (p == Player.BLACK) {
            statusLabel.setText("LƯỢT CỦA BẠN (ĐEN)");
            pnlBlackScore.setActive(true);
            pnlWhiteScore.setActive(false);
        } else {
            statusLabel.setText("MÁY ĐANG NGHĨ...");
            pnlWhiteScore.setActive(true);
            pnlBlackScore.setActive(false);
        }
    }

    // UPDATE LOGIC HIỂN THỊ
    public void updateBoard(int[][] modelBoard, boolean[][] validMoves) {
        int blackCount = 0;
        int whiteCount = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int newPiece = modelBoard[i][j];
                int oldPiece = previousBoard[i][j];

                if (newPiece == Player.BLACK) blackCount++;
                if (newPiece == Player.WHITE) whiteCount++;

                // 1. Logic quân mới đánh (Từ Rỗng -> Có quân)
                boolean isLastMove = (oldPiece == Player.EMPTY) && (newPiece != Player.EMPTY);
                
                // 2. Logic quân bị lật (Đã có quân -> Đổi màu)
                boolean isFlipped = (oldPiece != Player.EMPTY) && (oldPiece != newPiece) && (newPiece != Player.EMPTY);

                // Reset trạng thái lúc bắt đầu
                if (countPieces(modelBoard) <= 4) {
                    isLastMove = false;
                    isFlipped = false;
                }

                boardSquares[i][j].setPiece(newPiece);
                boardSquares[i][j].setHint(validMoves[i][j]);
                
                // Truyền cả 2 trạng thái
                boardSquares[i][j].setStates(isLastMove, isFlipped);
                
                previousBoard[i][j] = newPiece;
            }
        }
        
        pnlBlackScore.setScore(blackCount);
        pnlWhiteScore.setScore(whiteCount);
        boardPanel.repaint();
    }
    
    private int countPieces(int[][] board) {
        int count = 0;
        for(int[] row : board) for(int p : row) if(p != Player.EMPTY) count++;
        return count;
    }

    // --- DIALOG: KẾT THÚC GAME ĐẸP ---
    public void showModernGameOverDialog(String winner, int blackScore, int whiteScore) {
        JDialog dialog = new JDialog(this, "Kết Quả Trận Đấu", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true); // Bỏ thanh tiêu đề mặc định
        
        // Panel chính có viền
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 5));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JLabel lblHeader = new JLabel("GAME OVER", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblHeader.setForeground(Color.GRAY);
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Winner
        JLabel lblWinner = new JLabel(winner, SwingConstants.CENTER);
        lblWinner.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblWinner.setForeground(new Color(34, 139, 34)); // Xanh đậm
        lblWinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Score Box
        JPanel scorePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        scorePanel.setMaximumSize(new Dimension(400, 100));
        
        scorePanel.add(createMiniScore("ĐEN", blackScore, PIECE_BLACK));
        scorePanel.add(createMiniScore("TRẮNG", whiteScore, Color.GRAY));

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JButton btnReplay = createStyledButton("CHƠI LẠI", new Color(34, 139, 34));
        JButton btnExit = createStyledButton("THOÁT", new Color(200, 50, 50));
        
        btnReplay.addActionListener(e -> {
            // Reset logic
            for(int i=0; i<8; i++) for(int j=0; j<8; j++) previousBoard[i][j] = Player.EMPTY;
            if (controller != null) controller.resetGame();
            dialog.dispose();
        });
        
        btnExit.addActionListener(e -> System.exit(0));
        
        btnPanel.add(btnReplay);
        btnPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        btnPanel.add(btnExit);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(lblHeader);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblWinner);
        mainPanel.add(scorePanel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(btnPanel);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    // Helper tạo nút đẹp
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }
    
    // Helper tạo điểm số nhỏ trong Dialog
    private JPanel createMiniScore(String label, int score, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(240, 240, 240));
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel l1 = new JLabel(label, SwingConstants.CENTER);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l1.setForeground(color);
        
        JLabel l2 = new JLabel(String.valueOf(score), SwingConstants.CENTER);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        l2.setForeground(Color.DARK_GRAY);
        
        p.add(l1, BorderLayout.NORTH);
        p.add(l2, BorderLayout.CENTER);
        return p;
    }

    // --- DIALOG: THÔNG BÁO HẾT NƯỚC ĐI ---
    public void showNoMoveDialog(String playerName) {
        JDialog d = new JDialog(this, "Thông Báo", true);
        d.setUndecorated(true);
        d.setSize(400, 150);
        d.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
        p.setBackground(Color.WHITE);
        
        JLabel l = new JLabel("<html><center>" + playerName + " không có nước đi hợp lệ!<br>Chuyển lượt cho đối thủ.</center></html>", SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JButton b = new JButton("ĐÃ HIỂU");
        b.setBackground(Color.ORANGE);
        b.setForeground(Color.WHITE);
        b.addActionListener(e -> d.dispose());
        
        p.add(l, BorderLayout.CENTER);
        p.add(b, BorderLayout.SOUTH);
        
        d.add(p);
        d.setVisible(true);
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================
    
    private class ModernScorePanel extends JPanel {
        private JLabel lblName, lblScore;
        private Color pieceColor;
        private boolean isActive = false;

        public ModernScorePanel(String name, Color pColor) {
            this.pieceColor = pColor;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(Color.WHITE);
            
            lblName = new JLabel(name, SwingConstants.CENTER);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
            
            lblScore = new JLabel("2", SwingConstants.CENTER);
            lblScore.setFont(new Font("Segoe UI", Font.BOLD, 40)); 

            add(lblName, BorderLayout.NORTH);
            add(lblScore, BorderLayout.CENTER);
            updateStyle();
        }

        public void setScore(int score) { lblScore.setText(String.valueOf(score)); }
        
        public void setActive(boolean active) {
            this.isActive = active;
            updateStyle();
        }
        
        private void updateStyle() {
            if (isActive) {
                setBackground(new Color(255, 250, 205)); 
                setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0), 3)); 
            } else {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
            lblName.setForeground(pieceColor == PIECE_BLACK ? Color.BLACK : Color.GRAY);
            lblScore.setForeground(Color.GRAY);
        }
    }

    private class BoardSquare extends JPanel {
        private int row, col;
        private int piece = Player.EMPTY;
        private boolean isHint = false;
        private boolean isLastMove = false; 
        private boolean isFlipped = false; // Trạng thái mới: Bị lật

        public BoardSquare(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public void setPiece(int p) { this.piece = p; repaint(); }
        public void setHint(boolean hint) { this.isHint = hint; repaint(); }
        
        public void setStates(boolean lastMove, boolean flipped) {
            this.isLastMove = lastMove;
            this.isFlipped = flipped;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Nền & Lưới
            g2.setColor(BOARD_COLOR);
            g2.fillRect(0, 0, w, h);
            g2.setColor(GRID_COLOR);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRect(0, 0, w, h); 

            int margin = 8; 
            int diameter = Math.min(w, h) - 2 * margin;

            if (piece != Player.EMPTY) {
                // Vẽ quân cờ
                g2.setColor(piece == Player.BLACK ? PIECE_BLACK : PIECE_WHITE);
                g2.fillOval(margin, margin, diameter, diameter);
                // Viền mỏng
                g2.setColor(new Color(0,0,0,50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(margin, margin, diameter, diameter);

                // --- HIGHLIGHT 1: Quân mới đánh (Viền Đỏ Đậm) ---
                if (isLastMove) {
                    g2.setColor(LAST_MOVE_BORDER);
                    g2.setStroke(new BasicStroke(3.0f)); 
                    g2.drawOval(margin - 2, margin - 2, diameter + 4, diameter + 4);
                }
                
                // --- HIGHLIGHT 2: Quân bị lật (Viền Vàng Mảnh) ---
                // Yêu cầu của bạn: "Hiển thị các nước đã lật"
                else if (isFlipped) {
                    g2.setColor(FLIPPED_BORDER); // Màu vàng Gold
                    g2.setStroke(new BasicStroke(2.0f)); 
                    // Vẽ sát vào quân cờ
                    g2.drawOval(margin, margin, diameter, diameter);
                }
            } 
            else if (isHint) {
                g2.setColor(HINT_COLOR);
                int hintSize = diameter / 4; 
                g2.fillOval((w - hintSize) / 2, (h - hintSize) / 2, hintSize, hintSize);
            }
        }
    }
}