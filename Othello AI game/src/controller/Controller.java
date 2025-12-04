package controller;

import model.Boardgame;
import model.Logic;
import model.Player;
import view.Othello;

import javax.swing.*;

public class Controller {
	private Boardgame model;
	private Othello view;
	private int currentPlayer;

	public Controller(Othello view) {
		this.view = view;
		this.model = new Boardgame();
		this.currentPlayer = Player.BLACK;

		updateView();
	}

	public void processMove(int r, int c) {
		// Kiểm tra nước đi có hợp lệ không
		if (Logic.isValidMove(model, r, c, currentPlayer)) {
			// thực hiện nước đi (lật quân)
			Logic.makeMove(model, r, c, currentPlayer);
			// đổi sang lượt đối thủ
			currentPlayer = Player.getOpponent(currentPlayer);
			updateView();
			// kiểm tra đối thủ tiếp theo có nước đi không
			checkNextTurn();
		} else {
			System.out.println("Nước đi k hợp lệ");
		}
	}

	// kiểm tra lượt đi đối thủ có lượt đi không
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
	// kiểm tra lượt đi
	private void checkNextTurn() {
		if(hasValidMove(currentPlayer)) {
			return;
		}
		int opponent = Player.getOpponent(currentPlayer);
		if(hasValidMove(opponent)) {
            JOptionPane.showMessageDialog(view, "Không có nước đi! Đổi lượt lại cho " + (opponent == Player.BLACK ? "Đen" : "Trắng"));
            currentPlayer = opponent;
            updateView();
		}
		else {
			handleGameOver();
		}
	}
	// kết thúc game
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

		String winner = (blackScore > whiteScore) ? "ĐEN Thắng!" : (whiteScore > blackScore) ? "TRẮNG Thắng!" : "Hòa!";
		JOptionPane.showMessageDialog(view, "GAME OVER!\n" + winner);
	}

	// tạo game mới
	public void resetGame() {
		model.reset();
		currentPlayer = Player.BLACK;
		updateView();
		view.setCurrentPlayer(currentPlayer);
		System.out.println("Game mới");
	}

	private void updateView() {
		boolean[][] validMoves = new boolean[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				// hint hiển thị nước đi hợp lệ
				validMoves[i][j] = Logic.isValidMove(model, i, j, currentPlayer);
			}
		}
		view.updateBoard(model.getBoard(), validMoves);
		view.setTitle("Othello - Lượt: " + (currentPlayer == Player.BLACK ? "Đen" : "Trắng"));
	}
}