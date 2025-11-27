package model;

public class Boardgame {
    private int[][] board;
    private int size = 8;

    public Boardgame() {
        board = new int[size][size];
        reset();
    }

    public void reset() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Player.EMPTY;
            }
        }
        board[3][3] = Player.WHITE;
        board[4][4] = Player.WHITE;
        board[3][4] = Player.BLACK;
        board[4][3] = Player.BLACK;
    }

    public int getPiece(int r, int c) {
        if (r >= 0 && r < size && c >= 0 && c < size) {
            return board[r][c];
        }
        return -1;
    }
    public void setPiece(int r, int c, int piece) {
        if (r >= 0 && r < size && c >= 0 && c < size) {
            board[r][c] = piece;
        }
    }
    
    public int[][] getBoardArray() {
        return board;
    }
}