package model;

public class Player {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    public static int getOpponent(int player) {
        if ( player == BLACK) {
        	return WHITE;
        }else {
        	return BLACK;
        } 
    }
}