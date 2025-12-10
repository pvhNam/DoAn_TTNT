package controller;

import model.Boardgame;
import model.ComputerPlayer;
import model.Logic;
import model.Player;
import view.Othello;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller {
    private Boardgame model;
    private Othello view;
    private int currentPlayer;

    // Khai báo AI và chế độ chơi
    private ComputerPlayer aiPlayer;
    private boolean isSinglePlayer = true; // true: Chơi với máy, false: 2 người chơi

    public Controller(Othello view) {
        this.view = view;
        this.model = new Boardgame();
        this.currentPlayer = Player.BLACK; // Người chơi (Đen) đi trước

        // Khởi tạo máy tính cầm quân TRẮNG
        // Nếu muốn máy cầm quân Đen thì đổi thành Player.BLACK
        this.aiPlayer = new ComputerPlayer(Player.WHITE);

        updateView();
    }

    // Xử lý khi người chơi click vào ô cờ
    public void processMove(int r, int c) {
        // 1. Nếu đang là lượt của Máy thì chặn không cho người click
        if (isSinglePlayer && currentPlayer == Player.WHITE) {
            return;
        }

        // 2. Kiểm tra nước đi hợp lệ
        if (Logic.isValidMove(model, r, c, currentPlayer)) {
            // Thực hiện nước đi
            Logic.makeMove(model, r, c, currentPlayer);
            
            // Đổi lượt
            currentPlayer = Player.getOpponent(currentPlayer);
            
            // Cập nhật bàn cờ
            updateView();
            
            // Kiểm tra xem lượt tiếp theo (của Máy) có đi được không
            checkNextTurn();
        } else {
            System.out.println("Nước đi không hợp lệ!");
        }
    }

    // Hàm kiểm tra lượt đi tiếp theo
    private void checkNextTurn() {
        // Trường hợp 1: Người chơi hiện tại (currentPlayer) CÓ nước đi hợp lệ
        if (hasValidMove(currentPlayer)) {
            // Nếu đến lượt Máy -> Kích hoạt AI đánh (có độ trễ 1 chút cho tự nhiên)
            if (isSinglePlayer && currentPlayer == Player.WHITE) {
                Timer timer = new Timer(700, new ActionListener() { // Đợi 0.7 giây
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playComputerMove();
                        ((Timer)e.getSource()).stop(); // Dừng timer sau khi chạy xong
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
            return;
        }
        
        // Trường hợp 2: Người hiện tại KHÔNG có nước đi (bị Pass lượt)
        int opponent = Player.getOpponent(currentPlayer);
        
        // Kiểm tra xem đối thủ có đi được không
        if (hasValidMove(opponent)) {
            String name = (opponent == Player.BLACK) ? "Đen" : "Trắng";
            JOptionPane.showMessageDialog(view, "Không có nước đi! Đổi lượt lại cho " + name);
            
            // Chuyển lượt lại cho đối thủ
            currentPlayer = opponent;
            updateView();
            
            // Nếu lượt bị trả lại cho Máy -> Máy phải đánh ngay
            if (isSinglePlayer && currentPlayer == Player.WHITE) {
                 Timer timer = new Timer(700, e -> {
                    playComputerMove();
                    ((Timer)e.getSource()).stop();
                });
                timer.setRepeats(false);
                timer.start();
            }
        } 
        // Trường hợp 3: Cả 2 đều không đi được -> Hết game
        else {
            handleGameOver();
        }
    }

    // Logic để máy tính thực hiện nước đi
    private void playComputerMove() {
        // Gọi thuật toán Minimax để tìm nước đi tốt nhất
        Point bestMove = aiPlayer.getBestMove(model);
        
        if (bestMove != null) {
            System.out.println("Máy đánh vào: " + bestMove.x + ", " + bestMove.y);
            Logic.makeMove(model, bestMove.x, bestMove.y, currentPlayer);
            
            // Đổi lượt về cho người chơi
            currentPlayer = Player.getOpponent(currentPlayer);
            updateView();
            checkNextTurn();
        } else {
            // Nếu máy tính thế cờ và thấy không đi được đâu (dù hasValidMove trả về true - hiếm gặp)
            System.out.println("Lỗi: Máy không tìm thấy nước đi dù có thể đi.");
        }
    }

    // Kiểm tra xem một người chơi có bất kỳ nước đi hợp lệ nào không
    private boolean hasValidMove(int player) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Logic.isValidMove(model, i, j, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Xử lý kết thúc game
    private void handleGameOver() {
        int blackScore = 0;
        int whiteScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (model.getPiece(i, j) == Player.BLACK)
                    blackScore++;
                else if (model.getPiece(i, j) == Player.WHITE)
                    whiteScore++;
            }
        }

        String winner = "";
        if (blackScore > whiteScore) winner = "ĐEN CHIẾN THẮNG!";
        else if (whiteScore > blackScore) winner = "TRẮNG CHIẾN THẮNG!";
        else winner = "HÒA!";

        // Gọi hàm hiển thị bảng kết quả đẹp bên View
        view.showGameOverDialog(winner, blackScore, whiteScore);
    }

    // Reset game về trạng thái ban đầu
    public void resetGame() {
        model.reset();
        currentPlayer = Player.BLACK;
        updateView();
        view.setCurrentPlayer(currentPlayer);
        System.out.println("Game mới bắt đầu!");
    }

    // Cập nhật giao diện (gửi dữ liệu bàn cờ và gợi ý nước đi cho View)
    private void updateView() {
        boolean[][] validMoves = new boolean[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Tính toán gợi ý nước đi cho người chơi hiện tại
                validMoves[i][j] = Logic.isValidMove(model, i, j, currentPlayer);
            }
        }
        
        // Cập nhật bàn cờ
        view.updateBoard(model.getBoard(), validMoves);
        
        // Cập nhật thanh trạng thái (lượt ai, điểm số)
        view.setCurrentPlayer(currentPlayer);
    }
}