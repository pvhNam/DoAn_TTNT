package model;

public class Boardgame {
    private int[][] board;

    public Boardgame() {
        board = new int[8][8];
        reset();
    }

    public void reset() {
        // Xóa bàn cờ
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
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
        if (r >= 0 && r < 8 && c >= 0 && c < 8) {
            return board[r][c];
        }
        return -1;
    }

    public void setPiece(int r, int c, int piece) {
        if (r >= 0 && r < 8 && c >= 0 && c < 8) {
            board[r][c] = piece;
        }
    }
    
    public int[][] getBoardArray() {
        return board;
    }
}