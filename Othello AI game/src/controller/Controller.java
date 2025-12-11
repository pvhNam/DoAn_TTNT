package controller;

import model.Boardgame;
import model.Logic;
import model.Player;
import model.ComputerPlayer; // Import AI
import view.Othello;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller {
    private Boardgame model;
    private Othello view;
    private int currentPlayer;
    
    // thêm AI
    private ComputerPlayer ai;
    private boolean isVsComputer = true; // Bật chế độ chơi với máy

    public Controller(Othello view) {
        this.view = view;
        this.model = new Boardgame();
        this.currentPlayer = Player.BLACK;
        
        // Khởi tạo AI cầm quân TRẮNG
        this.ai = new ComputerPlayer(Player.WHITE);

        updateView();
    }

    public void processMove(int r, int c) {
        // Nếu đang là lượt máy thì chặn người dùng click
        if (isVsComputer && currentPlayer == Player.WHITE) return;

        if (Logic.isValidMove(model, r, c, currentPlayer)) {
            executeMove(r, c);
        } 
    }
    
    // Tách hàm thực hiện nước đi để tái sử dụng cho cả Người và Máy
    private void executeMove(int r, int c) {
        Logic.makeMove(model, r, c, currentPlayer);
        currentPlayer = Player.getOpponent(currentPlayer);
        updateView();
        checkNextTurn();
    }

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
// kiểm tra lượt đi tiếp theo
    private void checkNextTurn() {
        //Nếu người hiện tại đi được thì phải kiểm tra xem có phải máy không?
        if (hasValidMove(currentPlayer)) {
            if (isVsComputer && currentPlayer == Player.WHITE) {
                // Dùng Timer để tạo độ trễ 1 giây trước khi máy đi
                Timer timer = new Timer(700, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        doComputerMove();
                    }
                });
                timer.setRepeats(false); // Chỉ chạy 1 lần
                timer.start();
            }
            return;
        }

        // 2. Nếu bí nước thì chuyển lượt
        int opponent = Player.getOpponent(currentPlayer);
        if (hasValidMove(opponent)) {
            currentPlayer = opponent;
            JOptionPane.showMessageDialog(view, "Không có nước đi! Đổi lượt lại cho " + (currentPlayer == Player.BLACK ? "Đen" : "Trắng"));
            updateView();
            
            // sau khi đổi lượt, nếu lại trúng lượt máy thì máy phải đi tiếp
            checkNextTurn(); 
            // nếu cả hai đều k có lượt thì kết thúc game
        } else {
            handleGameOver();
        }
    }
    
    // hàm thực hiện nước đi của máy
    private void doComputerMove() {
        Point bestMove = ai.getBestMove(model);
        if (bestMove != null) {
            executeMove(bestMove.x, bestMove.y);
        }
    }
// kết thúc game
    private void handleGameOver() {
        int blackScore = 0;
        int whiteScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (model.getPiece(i, j) == Player.BLACK) blackScore++;
                else if (model.getPiece(i, j) == Player.WHITE) whiteScore++;
            }
        }

        String result = "TỈ SỐ: ĐEN " + blackScore + " - TRẮNG " + whiteScore + "\n";
        String winner = (blackScore > whiteScore) ? "ĐEN Thắng!" : (whiteScore > blackScore) ? "TRẮNG Thắng!" : "Hòa!";
        
        JOptionPane.showMessageDialog(view, "GAME OVER!\n" + result + winner);
        
        int option = JOptionPane.showConfirmDialog(view, "Chơi ván mới?", "Game Over", JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION){
            resetGame();
        }
    }

    public void resetGame() {
        model.reset();
        currentPlayer = Player.BLACK;
        updateView();
        view.setCurrentPlayer(currentPlayer);
    }

    private void updateView() {
        boolean[][] validMoves = new boolean[8][8];
        // nếu là lượt máy thì không hiển thị hint 
        if (!isVsComputer || currentPlayer == Player.BLACK) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    validMoves[i][j] = Logic.isValidMove(model, i, j, currentPlayer);
                }
            }
        }
        view.updateBoard(model.getBoard(), validMoves);
        view.setCurrentPlayer(currentPlayer);
    }
}