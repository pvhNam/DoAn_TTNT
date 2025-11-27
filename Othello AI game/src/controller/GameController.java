package controller;

import model.Boardgame;
import model.GameLogic;
import model.Player;
import view.Othello;
import javax.swing.JOptionPane;

public class GameController {
    private Boardgame model;
    private Othello view;
    private int currentPlayer;

    public GameController(Othello view) {
        this.view = view;
        this.model = new Boardgame();
        this.currentPlayer = Player.BLACK;
        updateViewFromModel();
    }

    public void handleCellClick(int row, int col) {
        if (GameLogic.isValidMove(model, row, col, currentPlayer)) {
        	GameLogic.makeMove(model, row, col, currentPlayer);
            updateViewFromModel();
            switchTurn();
        }else {
        	System.out.println("không đi đc ở đây");
        }
    }

    private void switchTurn() {
        int nextPlayer = Player.getOpponent(currentPlayer);

        if (GameLogic.hasAnyMove(model, nextPlayer)) {
            currentPlayer = nextPlayer;
        } else {
            // Nếu người tiếp theo không có nước đi, kiểm tra người hiện tại
            if (GameLogic.hasAnyMove(model, currentPlayer)) {
                JOptionPane.showMessageDialog(view, 
                    (nextPlayer == Player.BLACK ? "ĐEN" : "TRẮNG") + " không có nước đi! Giữ nguyên lượt.");
                // currentPlayer giữ nguyên
            } else {
                // Cả 2 đều không đi được -> Hết game
                handleGameOver();
                return;
            }
        }
        
        // Cập nhật thông tin lượt chơi trên View
        view.setCurrentPlayer(currentPlayer);
    }

    private void updateViewFromModel() {
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                // Cập nhật từng ô trên bàn cờ
                view.updateBoardCell(i, j, model.getPiece(i, j));
            }
        }
    }

    private void handleGameOver() {
        int blackScore = 0;
        int whiteScore = 0;
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                if(model.getPiece(i, j) == Player.BLACK) blackScore++;
                else if(model.getPiece(i, j) == Player.WHITE) whiteScore++;
            }
        }
        
        String winner = (blackScore > whiteScore) ? "ĐEN Thắng!" : (whiteScore > blackScore) ? "TRẮNG Thắng!" : "Hòa!";
        JOptionPane.showMessageDialog(view, "GAME OVER!\n" + winner);
    }
    public void resetGame() {
        model.reset();
        currentPlayer = Player.BLACK;
        updateViewFromModel();
        view.setCurrentPlayer(currentPlayer);
        System.out.println("Game has been reset!");
    }
    // ----------------------------------
    public boolean[][] getValidMoves() {
    	boolean[][] validMoves = new boolean[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Sử dụng GameLogic để kiểm tra nước đi hợp lệ
                validMoves[i][j] = GameLogic.isValidMove(model, i, j, currentPlayer);
            }
        }
        return validMoves;
    }
}