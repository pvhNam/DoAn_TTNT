package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Othello extends JFrame {
	private JButton[][] board = new JButton[8][8];
	private int currentPlayer = 1;

	public Othello() {
		setTitle("Othello");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setLayout(new GridLayout(8, 8));

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = new JButton();
				board[i][j].setBackground(Color.BLUE);
				board[i][j].addActionListener(new CellClick(i, j));
				add(board[i][j]);
			}
		}
		
		board[3][3].setBackground(Color.WHITE);
		board[3][4].setBackground(Color.BLACK);
		board[4][3].setBackground(Color.BLACK);
		board[4][4].setBackground(Color.WHITE);

		setVisible(true);

	}

	private class CellClick implements ActionListener {
		int x, y;
		CellClick(int x, int y) {
			this.x = x;
			this.y = y;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Clicked: " + x + "," + y);
		}
		
	}
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Othello());
	}
}