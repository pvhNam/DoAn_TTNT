package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ComputerPlayer {
    private int computerPlayer; 
    private int maxDepth = 5; 

    public ComputerPlayer(int computerPlayer) {
        this.computerPlayer = computerPlayer;
    }

    // hàm minimax
    public Point getBestMove(Boardgame game) {
        List<Point> validMoves = getValidMoves(game, computerPlayer);
        
        if (validMoves.isEmpty()) return null;

        Point bestMove = validMoves.get(0);
        int bestValue = Integer.MIN_VALUE;

        for (Point move : validMoves) {
            Boardgame nextBoard = cloneBoard(game);
            Logic.makeMove(nextBoard, move.x, move.y, computerPlayer);
            
            int val = minimax(false, nextBoard, maxDepth - 1, Player.getOpponent(computerPlayer));
            
            if (val > bestValue) {
                bestValue = val;
                bestMove = move;
            }
        }
        return bestMove;
    }
// minimax
    private int minimax(boolean maxmin, Boardgame state, int depth, int currentPlayer) {
        if (depth == 0 || isover(state)) {
            return evaluateTotalScore(state);
        }

        List<Point> validMoves = getValidMoves(state, currentPlayer);
        
        if (validMoves.isEmpty()) {
             return minimax(!maxmin, state, depth - 1, Player.getOpponent(currentPlayer));
        }

        if (maxmin == true) { // Máy (Max)
            int temp = -999999999; 
            for (Point move : validMoves) {
                Boardgame newState = cloneBoard(state);
                Logic.makeMove(newState, move.x, move.y, currentPlayer);
                
                int value = minimax(false, newState, depth - 1, Player.getOpponent(currentPlayer));
                if (value > temp) {
                    temp = value;
                }
            }
            return temp;
        } else { // Người (Min)
            int temp = 999999999;
            for (Point move : validMoves) {
                Boardgame newState = cloneBoard(state);
                Logic.makeMove(newState, move.x, move.y, currentPlayer);
                
                int value = minimax(true, newState, depth - 1, Player.getOpponent(currentPlayer));
                if (value < temp) {
                    temp = value;
                }
            }
            return temp;
        }
    }

    // Hàm tổng hợp heuristic
    private int evaluateTotalScore(Boardgame board) {
        // Điểm vị trí
        int positionalScore = heuristicPosition(board);
        
        // Điểm linh hoạt
        int mobilityScore = heuristicMobility(board);
        
        // Điểm chênh lệch quân
        int discScore = heuristicDiscParity(board);

        // Công thức tổng hợp có trọng số
        // Mobility rất quan trọng nên nhân hệ số cao (10)
        // Position quan trọng vừa phải (đã có trọng số trong mảng)
        // Disc Parity chỉ là phụ (hệ số thấp) để tránh tham ăn quân sớm
        
        return positionalScore + (10 * mobilityScore) + (2 * discScore);
    }

    // heuristic vị trí
    private int heuristicPosition(Boardgame board) {
        int myScore = 0;
        int opScore = 0;
        int opponent = Player.getOpponent(computerPlayer);

        int[][] weights = {
            { 20, -3,  11,  8,  8,  11, -3,  20},
            {-3, -7, -4,  1,  1, -4, -7, -3},
            { 11, -4,  2,  2,  2,  2, -4,  11},
            { 8,  1,  2, -3, -3,  2,  1,  8},
            { 8,  1,  2, -3, -3,  2,  1,  8},
            { 11, -4,  2,  2,  2,  2, -4,  11},
            {-3, -7, -4,  1,  1, -4, -7, -3},
            { 20, -3,  11,  8,  8,  11, -3,  20}
        };

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int piece = board.getPiece(i, j);
                if (piece == computerPlayer) {
                    myScore += weights[i][j];
                } else if (piece == opponent) {
                    opScore += weights[i][j];
                }
            }
        }
        return myScore - opScore;
    }

    // heuristic khả năng di chuyển
    private int heuristicMobility(Boardgame board) {
        int opponent = Player.getOpponent(computerPlayer);
        
        int myPossibleMoves = getValidMoves(board, computerPlayer).size();
        int opPossibleMoves = getValidMoves(board, opponent).size();

        return (myPossibleMoves - opPossibleMoves);
    }

    // heuristic chênh lêch quân cờ
    //lấy Quân mình - Quân địch
    private int heuristicDiscParity(Boardgame board) {
        int myCount = 0;
        int opCount = 0;
        int opponent = Player.getOpponent(computerPlayer);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int p = board.getPiece(i, j);
                if (p == computerPlayer) myCount++;
                else if (p == opponent) opCount++;
            }
        }
        
        return (myCount - opCount);
    }

    private boolean isover(Boardgame board) {
        return getValidMoves(board, Player.BLACK).isEmpty() && getValidMoves(board, Player.WHITE).isEmpty();
    }

    private Boardgame cloneBoard(Boardgame source) {
        Boardgame newGame = new Boardgame(); 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newGame.setPiece(i, j, source.getPiece(i, j));
            }
        }
        return newGame;
    }

    private List<Point> getValidMoves(Boardgame game, int player) {
        List<Point> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Logic.isValidMove(game, i, j, player)) {
                    moves.add(new Point(i, j));
                }
            }
        }
        return moves;
    }
}