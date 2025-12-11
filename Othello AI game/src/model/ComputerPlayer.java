package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ComputerPlayer {
    private int computerPlayer; 
    private int maxDepth = 4; // độ sâu tìm kiếm

    public ComputerPlayer(int computerPlayer) {
        this.computerPlayer = computerPlayer;
    }
// kiểm tra nước đi tốt nhất
    public Point getBestMove(Boardgame game) {
        List<Point> validMoves = getValidMoves(game, computerPlayer);
        
        if (validMoves.isEmpty()) return null;

        Point bestMove = validMoves.get(0);
        int bestValue = Integer.MIN_VALUE;

        // Đây là lớp ngoài cùng, tương ứng với lượt MAX (Máy tính)
        for (Point move : validMoves) {
        	Boardgame nextBoard = cloneBoard(game);
            Logic.makeMove(nextBoard, move.x, move.y, computerPlayer);
            
            // Gọi để quy: False vì lượt sau là người chơi (Min)
            int val = minimax(false, nextBoard, maxDepth - 1, Player.getOpponent(computerPlayer));
            
            if (val > bestValue) {
                bestValue = val;
                bestMove = move;
            }
        }
        return bestMove;
    }
    // phương thức minimax
    private int minimax(boolean maxmin, Boardgame state, int depth, int currentPlayer) {
        // cơ sở
        if (depth == 0 || isover(state)) {
            return heuristic(state);
        }

        List<Point> validMoves = getValidMoves(state, currentPlayer);
        
        // Nếu không có nước đi (Pass lượt), giữ nguyên lượt cho người kia đi tiếp
        if (validMoves.isEmpty()) {
             return minimax(!maxmin, state, depth - 1, Player.getOpponent(currentPlayer));
        }

        // đệ quy
        if (maxmin == true) { // max node (Máy tính)
        	
            int temp = -999999999; 
            for (Point move : validMoves) {
                Boardgame newState = cloneBoard(state); // Tạo node con
                Logic.makeMove(newState, move.x, move.y, currentPlayer);
                
                int value = minimax(false, newState, depth - 1, Player.getOpponent(currentPlayer));
                if (value > temp) {
                    temp = value;
                    // ghi lại node
                }
            }
            return temp;
        } else { // min node (Người chơi)
            int temp = 999999999;
            for (Point move : validMoves) {
                Boardgame newState = cloneBoard(state); // tạo node con
                Logic.makeMove(newState, move.x, move.y, currentPlayer);
                
                int value = minimax(true, newState, depth - 1, Player.getOpponent(currentPlayer));
                if (value < temp) {
                    temp = value;
                 // ghi lại node
                }
            }
            return temp;
        }
    }

    // đánh giá Heuristic
    private int heuristic(Boardgame board) {
        int myScore = 0;// người
        int opScore = 0;// máy 
        int opponent = Player.getOpponent(computerPlayer);

        // bảng trọng số 
        int[][] weights = {
            { 4, -3,  2,  2,  2,  2, -3,  4},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            { 2, -1,  1,  0,  0,  1, -1,  2},
            { 2, -1,  0,  1,  1,  0, -1,  2},
            { 2, -1,  0,  1,  1,  0, -1,  2},
            { 2, -1,  1,  0,  0,  1, -1,  2},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            { 4, -3,  2,  2,  2,  2, -3,  4}
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

    //kết thúc game
    private boolean isover(Boardgame board) {
        // nếu cả 2 bên đều ko đi được thì là Over
        return getValidMoves(board, Player.BLACK).isEmpty() && getValidMoves(board, Player.WHITE).isEmpty();
    }

 // hàm tạo ra một đối tượng boardgame mới giống hệt cái cũ
    private Boardgame cloneBoard(Boardgame source) {
        Boardgame newGame = new Boardgame(); // Tạo mới (nó sẽ tự reset về ban đầu)
        // Copy từng ô từ source sang newGame
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newGame.setPiece(i, j, source.getPiece(i, j));
            }
        }
        return newGame;
    }
    // kiểm tra nước đi hợp lệ 
    private List<Point> getValidMoves(Boardgame game, int player) {
        List<Point> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Gọi trực tiếp Logic.isValidMove với đối tượng Boardgame
                if (Logic.isValidMove(game, i, j, player)) {
                    moves.add(new Point(i, j));
                }
            }
        }
        return moves;
    }
}