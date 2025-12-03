package view;

import controller.Controller;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Othello extends JFrame {
	private JButton[][] board = new JButton[8][8];
	private Controller controller;
	private int currentPlayer = 1;

	public Othello() {
		setTitle("Othello");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setLayout(new GridLayout(8, 8));

		// Khởi tạo bàn cờ
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = new JButton();
				board[i][j].setBackground(Color.GRAY);
				board[i][j].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

				board[i][j].addActionListener(new CellClick(i, j));
				add(board[i][j]);
			}
		}
		// thêm nút reset game
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		JMenuItem resetItem = new JMenuItem("New Game");

		resetItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controller != null) {
					controller.resetGame();
				}
			}
		});
		gameMenu.add(resetItem);
		menuBar.add(gameMenu);
		setJMenuBar(menuBar);

		setResizable(false);
		setVisible(true);
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setCurrentPlayer(int p) {
		this.currentPlayer = p;
	}

	private class CellClick implements ActionListener {
		int x, y;

		CellClick(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (controller != null) {
				controller.processMove(x, y);
			}
		}
	}

	// cập nhật giao diện
	public void updateBoard(int[][] modelBoard, boolean[][] validMoves) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int piece = modelBoard[i][j];
				if (piece == Player.BLACK) {
					board[i][j].setBackground(Color.BLACK);
				} else if (piece == Player.WHITE) {
					board[i][j].setBackground(Color.WHITE);
				} else {
					if (validMoves[i][j]) {
						board[i][j].setBackground(Color.red);
					} else {
						board[i][j].setBackground(Color.green);
					}
					board[i][j].setEnabled(true);
				}
				board[i][j].repaint();
			}
		}

	}
}