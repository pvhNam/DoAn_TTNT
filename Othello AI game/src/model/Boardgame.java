package model;

public class Boardgame {
    private int[][] board;
    public static final int SIZE = 8;

    public Boardgame() {
        board = new int[SIZE][SIZE];
        reset();
    }

    public void reset() {
        // Xóa bàn cờ
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = Player.EMPTY;
            }
        }
        // Khởi tạo 4 quân giữa
        board[3][3] = Player.WHITE;
        board[4][4] = Player.WHITE;
        board[3][4] = Player.BLACK;
        board[4][3] = Player.BLACK;
    }

    public int getPiece(int r, int c) {
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
            return board[r][c];
        }
        return -1; // Lỗi
    }

    public void setPiece(int r, int c, int piece) {
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
            board[r][c] = piece;
        }
    }
    
    public int[][] getBoardArray() {
        return board;
    }
}