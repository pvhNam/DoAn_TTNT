package controller;

import model.Boardgame;
import model.ComputerPlayer; // Import AI
import model.GameLogic;
import model.Player;
import view.Othello;

import javax.swing.JOptionPane;
import javax.swing.Timer; // Import Timer cho độ trễ

public class GameController {
    private Boardgame model;
    private Othello view;
    private int currentPlayer;

    // 1. Thêm đối tượng AI
    private ComputerPlayer aiPlayer;
    private boolean isAiTurn = false; // Cờ kiểm tra đang đến lượt AI chưa

    public GameController(Othello view) {
        this.view = view;
        this.model = new Boardgame();
        this.currentPlayer = Player.BLACK;

        // 2. Khởi tạo AI (AI cầm quân TRẮNG)
        // Nếu muốn AI cầm quân Đen thì đổi thành Player.BLACK
        this.aiPlayer = new ComputerPlayer(Player.WHITE);

        updateViewFromModel();
    }

    public void handleCellClick(int row, int col) {
        // Chặn người chơi click khi AI đang suy nghĩ
        if (isAiTurn) return;

        if (GameLogic.isValidMove(model, row, col, currentPlayer)) {
            // Thực hiện nước đi người chơi
            GameLogic.makeMove(model, row, col, currentPlayer);
            updateViewFromModel();
            
            // Chuyển lượt (sẽ kích hoạt AI nếu đến lượt Trắng)
            switchTurn();
        } 
        // Xóa phần else báo lỗi để trải nghiệm mượt hơn (bấm nhầm không bị hiện popup)
    }

    private void switchTurn() {
        int nextPlayer = Player.getOpponent(currentPlayer);

        // Kiểm tra người tiếp theo có nước đi không
        if (GameLogic.hasAnyMove(model, nextPlayer)) {
            currentPlayer = nextPlayer;
            view.setCurrentPlayer(currentPlayer);

            // 3. LOGIC KÍCH HOẠT AI
            // Nếu người chơi hiện tại là TRẮNG -> Là lượt của AI
            if (currentPlayer == Player.WHITE) {
                isAiTurn = true;

                // Dùng Timer tạo độ trễ 0.5s để người chơi kịp nhìn thấy nước vừa đi
                Timer timer = new Timer(500, e -> {
                    performAiMove();
                    ((Timer)e.getSource()).stop(); // Dừng timer sau khi chạy xong
                });
                timer.setRepeats(false); // Chỉ chạy 1 lần
                timer.start();
            } else {
                isAiTurn = false; // Trả lại lượt cho người
            }

        } else {
            // --- XỬ LÝ KHI KHÔNG CÓ NƯỚC ĐI (PASS) ---
            
            // Kiểm tra xem người hiện tại (người vừa đánh xong) có còn nước đi không
            if (GameLogic.hasAnyMove(model, currentPlayer)) {
                JOptionPane.showMessageDialog(view,
                    (nextPlayer == Player.BLACK ? "ĐEN" : "TRẮNG") + " không có nước đi! Giữ nguyên lượt.");
                
                // Quan trọng: Nếu bị Pass lượt mà vẫn là lượt TRẮNG (AI) thì AI phải đánh tiếp
                if (currentPlayer == Player.WHITE) {
                    // Gọi lại AI nhưng cần delay xíu để popup tắt hẳn
                    Timer timer = new Timer(1000, e -> {
                        performAiMove();
                        ((Timer)e.getSource()).stop();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                
            } else {
                // Cả 2 đều bí -> Hết game
                handleGameOver();
            }
        }
    }

    // 4. Hàm thực hiện nước đi của AI
    private void performAiMove() {
        // Gọi bộ não AI để lấy nước đi tốt nhất
        // Lưu ý: Point là class con static trong ComputerPlayer
        ComputerPlayer.Point bestMove = aiPlayer.getBestMove(model);

        if (bestMove != null) {
            // AI thực hiện nước đi
            GameLogic.makeMove(model, bestMove.x, bestMove.y, currentPlayer);
            updateViewFromModel();
            
            // Đổi lại sang lượt người
            switchTurn(); 
        } else {
            // Trường hợp hy hữu: AI không tìm thấy nước đi (dù hasAnyMove trả về true)
            // Có thể xảy ra nếu logic check và logic getMove không đồng bộ
            System.out.println("AI Error: Cannot find move despite valid moves existing.");
        }
    }

    private void updateViewFromModel() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                view.updateBoardCell(i, j, model.getPiece(i, j));
            }
        }
    }

    private void handleGameOver() {
        int blackScore = 0;
        int whiteScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (model.getPiece(i, j) == Player.BLACK) blackScore++;
                else if (model.getPiece(i, j) == Player.WHITE) whiteScore++;
            }
        }

        String winner = (blackScore > whiteScore) ? "ĐEN Thắng!" : (whiteScore > blackScore) ? "TRẮNG Thắng!" : "Hòa!";
        JOptionPane.showMessageDialog(view, "GAME OVER!\n" + winner);
    }

    public void resetGame() {
        model.reset();
        currentPlayer = Player.BLACK;
        isAiTurn = false; // Reset trạng thái AI
        
        updateViewFromModel();
        view.setCurrentPlayer(currentPlayer);
        
        System.out.println("Game has been reset!");
    }

    public boolean[][] getValidMoves() {
        boolean[][] validMoves = new boolean[Boardgame.SIZE][Boardgame.SIZE];
        for (int i = 0; i < Boardgame.SIZE; i++) {
            for (int j = 0; j < Boardgame.SIZE; j++) {
                validMoves[i][j] = GameLogic.isValidMove(model, i, j, currentPlayer);
            }
        }
        return validMoves;
    }
}