package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class Othello extends JFrame {
    // Màu sắc giao diện
    private static final Color BOARD_COLOR = new Color(39, 119, 20);
    private static final Color GRID_COLOR = new Color(20, 60, 10);
    private static final Color HINT_COLOR = new Color(255, 255, 255, 50);
    private static final Color PANEL_COLOR = new Color(40, 40, 40);

    private Cell[][] board = new Cell[8][8];
    
    // Các Label hiển thị thông tin
    private JLabel turnLabel;
    private JLabel blackScoreLabel;
    private JLabel whiteScoreLabel;
    
    private int currentPlayer = 1; // 1: Đen, 2: Trắng

    public Othello() {
        setTitle("Othello Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 780);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER PANEL (Chứa Tỉ số & Lượt đi) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Điểm Đen (Bên trái)
        blackScoreLabel = new JLabel("ĐEN: 2");
        blackScoreLabel.setForeground(Color.WHITE);
        blackScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        blackScoreLabel.setIcon(createIcon(Color.BLACK)); // Icon minh họa
        headerPanel.add(blackScoreLabel, BorderLayout.WEST);

        // Thông báo lượt (Ở giữa)
        turnLabel = new JLabel("Lượt: ĐEN", SwingConstants.CENTER);
        turnLabel.setForeground(Color.CYAN); // Màu nổi bật cho lượt đi
        turnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(turnLabel, BorderLayout.CENTER);

        // Điểm Trắng (Bên phải)
        whiteScoreLabel = new JLabel("TRẮNG: 2");
        whiteScoreLabel.setForeground(Color.WHITE);
        whiteScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        whiteScoreLabel.setIcon(createIcon(Color.WHITE)); // Icon minh họa
        whiteScoreLabel.setHorizontalTextPosition(SwingConstants.LEFT); // Chữ nằm bên trái icon
        headerPanel.add(whiteScoreLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. BÀN CỜ (Center) ---
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8, 8, 2, 2));
        boardPanel.setBackground(GRID_COLOR);
        boardPanel.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 5));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
                board[i][j].addMouseListener(new CellClick(i, j));
                boardPanel.add(board[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // --- 3. Khởi tạo ban đầu ---
        board[3][3].setPiece(2);
        board[4][4].setPiece(2);
        board[3][4].setPiece(1);
        board[4][3].setPiece(1);

        updateScore(); // Cập nhật điểm lần đầu

        setVisible(true);
    }

    // Hàm tạo icon tròn nhỏ cho phần tỉ số
    private Icon createIcon(Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                g.fillOval(x, y, getIconWidth(), getIconHeight());
                if (color == Color.BLACK) {
                    g.setColor(Color.GRAY);
                    g.drawOval(x, y, getIconWidth(), getIconHeight());
                }
            }
            @Override
            public int getIconWidth() { return 15; }
            @Override
            public int getIconHeight() { return 15; }
        };
    }

    // Hàm đếm và cập nhật giao diện điểm số
    private void updateScore() {
        int blackCount = 0;
        int whiteCount = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].piece == 1) blackCount++;
                else if (board[i][j].piece == 2) whiteCount++;
            }
        }

        blackScoreLabel.setText("ĐEN: " + blackCount);
        whiteScoreLabel.setText("TRẮNG: " + whiteCount);
        
        if (currentPlayer == 1) {
            turnLabel.setText("Lượt: ĐEN");
            turnLabel.setForeground(Color.GREEN);
        } else {
            turnLabel.setText("Lượt: TRẮNG");
            turnLabel.setForeground(Color.WHITE);
        }
    }

    // --- Class Cell (Giữ nguyên logic vẽ) ---
    private class Cell extends JPanel {
        int row, col;
        int piece = 0; // 0: Trống, 1: Đen, 2: Trắng
        boolean isHovered = false;

        public Cell(int r, int c) {
            this.row = r;
            this.col = c;
            setBackground(BOARD_COLOR);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        public void setPiece(int p) {
            this.piece = p;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            if (isHovered && piece == 0) {
                g2.setColor(HINT_COLOR);
                g2.fillOval(x + 5, y + 5, size - 10, size - 10);
            }

            if (piece != 0) {
                g2.setColor(new Color(0, 0, 0, 50)); // Shadow
                g2.fillOval(x + 3, y + 3, size, size);

                if (piece == 1) { // Đen
                    g2.setColor(Color.BLACK);
                    g2.fillOval(x, y, size, size);
                    g2.setColor(new Color(60, 60, 60)); // Highlight
                    g2.drawOval(x + 5, y + 5, size - 10, size - 10);
                } else { // Trắng
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x, y, size, size);
                    g2.setColor(Color.LIGHT_GRAY); // Border
                    g2.drawOval(x, y, size, size);
                }
            }
        }
    }

    // --- Xử lý sự kiện Click ---
    private class CellClick extends MouseAdapter {
        int x, y;
        CellClick(int x, int y) { this.x = x; this.y = y; }

        @Override
        public void mousePressed(MouseEvent e) {
            if (board[x][y].piece == 0) {
                // Đặt quân cờ
                board[x][y].setPiece(currentPlayer);
                
                // Đổi lượt
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                
                // CẬP NHẬT TỈ SỐ SAU MỖI NƯỚC ĐI
                updateScore(); 
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Othello());
    }
}