package controller;

import model.Boardgame;
import model.Logic;
import model.Player;
import model.ComputerPlayer;
import view.Othello;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller {
    private Boardgame model;
    private Othello view;
    private int currentPlayer;
    
    private ComputerPlayer ai;
    private boolean isVsComputer = true; 

    public Controller(Othello view) {
        this.view = view;
        this.model = new Boardgame();
        this.currentPlayer = Player.BLACK;
        this.ai = new ComputerPlayer(Player.WHITE);

        updateView();
    }

    public void processMove(int r, int c) {
        if (isVsComputer && currentPlayer == Player.WHITE) return;

        if (Logic.isValidMove(model, r, c, currentPlayer)) {
            executeMove(r, c);
        } 
    }
    
    private void executeMove(int r, int c) {
        Logic.makeMove(model, r, c, currentPlayer);
        currentPlayer = Player.getOpponent(currentPlayer);
        updateView();
        
        // Kiểm tra logic lượt tiếp theo
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

    private void checkNextTurn() {
        // 1. Nếu người hiện tại đi được
        if (hasValidMove(currentPlayer)) {
            if (isVsComputer && currentPlayer == Player.WHITE) {
                Timer timer = new Timer(800, e -> doComputerMove());
                timer.setRepeats(false);
                timer.start();
            }
            return;
        }

        // 2. Nếu bí nước -> Kiểm tra đối thủ
        int opponent = Player.getOpponent(currentPlayer);
        if (hasValidMove(opponent)) {
            // Hiển thị thông báo Hết nước đi đẹp hơn
            String skippedPlayer = (currentPlayer == Player.BLACK) ? "ĐEN" : "TRẮNG";
            view.showNoMoveDialog(skippedPlayer); 
            
            currentPlayer = opponent;
            updateView();
            checkNextTurn(); 
        } else {
            // 3. Cả 2 đều bí -> Game Over
            handleGameOver();
        }
    }
    
    private void doComputerMove() {
        Point bestMove = ai.getBestMove(model);
        if (bestMove != null) {
            executeMove(bestMove.x, bestMove.y);
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

        String winner;
        if (blackScore > whiteScore) winner = "ĐEN THẮNG!";
        else if (whiteScore > blackScore) winner = "TRẮNG THẮNG!";
        else winner = "HÒA!";
        
        // Gọi giao diện Game Over đẹp
        view.showModernGameOverDialog(winner, blackScore, whiteScore);
    }

    public void resetGame() {
        model.reset();
        currentPlayer = Player.BLACK;
        updateView();
        view.setCurrentPlayer(currentPlayer);
    }

    private void updateView() {
        boolean[][] validMoves = new boolean[8][8];
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