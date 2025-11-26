package model;

public class Player {
    // Định nghĩa giá trị giống như trong View 
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    // Hàm tiện ích để lấy đối thủ (1 -> 2, 2 -> 1)
    public static int getOpponent(int player) {
        return (player == BLACK) ? WHITE : BLACK;
    }
}