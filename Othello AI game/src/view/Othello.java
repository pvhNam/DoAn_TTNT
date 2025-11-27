package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import controller.GameController; // Import Controller

public class Othello extends JFrame {
    // Màu sắc giao diện
    private static final Color BOARD_COLOR = new Color(39, 119, 20);
    private static final Color GRID_COLOR = new Color(20, 60, 10);
    private static final Color HINT_COLOR = new Color(255, 255, 255, 50);
    private static final Color PANEL_COLOR = new Color(40, 40, 40);

    private boolean[][] validMoveHints = new boolean[8][8];
    private Cell[][] board = new Cell[8][8];
    private JLabel turnLabel, blackScoreLabel, whiteScoreLabel;
    
    private GameController controller; // Tham chiếu Controller
    private int currentPlayer = 1; 

    public Othello() {
        setTitle("Othello Game - MVC");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 780);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        refreshValidMoveHints();
        setResizable(false);
        
     // --- 0. THÊM MENU BAR (MỚI) ---
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem resetItem = new JMenuItem("New Game");
        // Phím tắt Ctrl + N
        resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        
        // Bắt sự kiện khi bấm nút New Game
        resetItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.resetGame(); // Gọi hàm reset bên Controller
                }
            }
        });

        gameMenu.add(resetItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar); // Gắn Menu Bar vào cửa sổ
        // ----------------------------------
        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        blackScoreLabel = new JLabel("ĐEN: 2");
        blackScoreLabel.setForeground(Color.WHITE);
        blackScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        blackScoreLabel.setIcon(createIcon(Color.BLACK));
        headerPanel.add(blackScoreLabel, BorderLayout.WEST);

        turnLabel = new JLabel("Lượt: ĐEN", SwingConstants.CENTER);
        turnLabel.setForeground(Color.GREEN);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(turnLabel, BorderLayout.CENTER);

        whiteScoreLabel = new JLabel("TRẮNG: 2");
        whiteScoreLabel.setForeground(Color.WHITE);
        whiteScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        whiteScoreLabel.setIcon(createIcon(Color.WHITE));
        whiteScoreLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        headerPanel.add(whiteScoreLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- BOARD ---
        JPanel boardPanel = new JPanel(new GridLayout(8, 8, 2, 2));
        boardPanel.setBackground(GRID_COLOR);
        boardPanel.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 5));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
                boardPanel.add(board[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
        
        // Khởi tạo Controller
        controller = new GameController(this);

        setVisible(true);
    }

    // Hàm để Controller gọi cập nhật giao diện từng ô
    public void updateBoardCell(int r, int c, int piece) {
        board[r][c].setPiece(piece);
        updateScore(); // Tính lại điểm mỗi khi bàn cờ thay đổi
        refreshValidMoveHints();
    }

    // Hàm để Controller set lượt người chơi
    public void setCurrentPlayer(int p) {
        this.currentPlayer = p;
        updateScore();
        refreshValidMoveHints();
    }

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

    private Icon createIcon(Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                g.fillOval(x, y, 15, 15);
                if (color == Color.BLACK) {
                    g.setColor(Color.GRAY);
                    g.drawOval(x, y, 15, 15);
                }
            }
            @Override
            public int getIconWidth() { return 15; }
            @Override
            public int getIconHeight() { return 15; }
        };
    }

    // --- INNER CLASS CELL ---
    private class Cell extends JPanel {
        int row, col;
        int piece = 0; 

        public Cell(int r, int c) {
            this.row = r;
            this.col = c;
            setBackground(BOARD_COLOR);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (controller != null) {
                        controller.handleCellClick(row, col);
                    }
                }
              
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

         // Chỉ vẽ hint nếu ô đó là nước đi hợp lệ
            if (piece == 0 && validMoveHints[row][col]) {
                // Tùy chọn: Có thể thay đổi màu hint cho rõ ràng hơn
                g2.setColor(HINT_COLOR); 
                g2.fillOval(x + 5, y + 5, size - 10, size - 10);
            }

            if (piece != 0) {
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillOval(x + 3, y + 3, size, size); // Shadow

                if (piece == 1) { // Black
                    g2.setColor(Color.BLACK);
                    g2.fillOval(x, y, size, size);
                    g2.setColor(new Color(60, 60, 60));
                    g2.drawOval(x + 5, y + 5, size - 10, size - 10);
                } else { // White
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x, y, size, size);
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawOval(x, y, size, size);
                }
            }
        }
    }
    private void refreshValidMoveHints() {
        if (controller != null) {
            this.validMoveHints = controller.getValidMoves();
            // Yêu cầu vẽ lại toàn bộ bàn cờ để hint mới hiển thị
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    board[i][j].repaint();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Othello());
    }
}