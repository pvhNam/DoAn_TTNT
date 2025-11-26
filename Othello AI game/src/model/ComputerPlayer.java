package model;

import java.util.ArrayList;
import java.util.List;

public class ComputerPlayer {
    
    // Độ sâu suy nghĩ của AI (Càng cao càng giỏi nhưng càng chậm)
    // 4 là mức trung bình khá, 6 là rất khó.
    private static final int MAX_DEPTH = 4;
    
    private int aiColor; // Màu quân của AI (thường là TRẮNG)

    // BẢNG TRỌNG SỐ (HEURISTIC)
    // Góc (100) là quan trọng nhất. Ô sát hốc (-20, -50) là tử địa (X-square, C-square).
    private static final int[][] WEIGHTS = {
        {100, -20,  10,   5,   5,  10, -20, 100},
        {-20, -50,  -2,  -2,  -2,  -2, -50, -20},
        { 10,  -2,  -1,  -1,  -1,  -1,  -2,  10},
        {  5,  -2,  -1,  -1,  -1,  -1,  -2,   5},
        {  5,  -2,  -1,  -1,  -1,  -1,  -2,   5},
        { 10,  -2,  -1,  -1,  -1,  -1,  -2,  10},
        {-20, -50,  -2,  -2,  -2,  -2, -50, -20},
        {100, -20,  10,   5,   5,  10, -20, 100}
    };

    public ComputerPlayer(int color) {
        this.aiColor = color;
    }

    // --- HÀM CHÍNH: LẤY NƯỚC ĐI TỐT NHẤT ---
    public Point getBestMove(Boardgame board) {
        // Khởi tạo Alpha (tệ nhất có thể) và Beta (tốt nhất có thể)
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        
        Point bestMove = null;
        int maxEval = Integer.MIN_VALUE;

        // Lấy tất cả nước đi hợp lệ hiện tại
        List<Point> validMoves = getValidMoves(board, aiColor);

        for (Point move : validMoves) {
            // 1. Giả lập nước đi
            Boardgame clonedBoard = copyBoard(board);
            GameLogic.makeMove(clonedBoard, move.x, move.y, aiColor);

            // 2. Gọi đệ quy Minimax (Lượt sau là lượt đối thủ -> minimizing)
            int eval = minimax(clonedBoard, MAX_DEPTH - 1, false, alpha, beta);

            // 3. Chọn nước đi có điểm cao nhất
            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
            
            // Cập nhật Alpha (cho nước đi đầu tiên ở gốc cây)
            alpha = Math.max(alpha, eval);
        }
        
        return bestMove;
    }

    // --- THUẬT TOÁN MINIMAX VỚI ALPHA-BETA PRUNING ---
    private int minimax(Boardgame board, int depth, boolean isMaximizing, int alpha, int beta) {
        // Điều kiện dừng: Hết độ sâu hoặc Hết game
        if (depth == 0 || isGameOver(board)) {
            return evaluateBoard(board);
        }

        int currentPlayer = isMaximizing ? aiColor : Player.getOpponent(aiColor);
        List<Point> moves = getValidMoves(board, currentPlayer);

        // Trường hợp đặc biệt: Không có nước đi (Pass lượt)
        if (moves.isEmpty()) {
            // Vẫn giữ nguyên alpha, beta và đệ quy tiếp với lượt người kia
            return minimax(board, depth - 1, !isMaximizing, alpha, beta);
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Point move : moves) {
                // Mô phỏng
                Boardgame childBoard = copyBoard(board);
                GameLogic.makeMove(childBoard, move.x, move.y, currentPlayer);

                // Đệ quy
                int eval = minimax(childBoard, depth - 1, false, alpha, beta);
                
                maxEval = Math.max(maxEval, eval);
                
                // Cập nhật Alpha (Giá trị tốt nhất mà MAX tìm được)
                alpha = Math.max(alpha, eval);
                
                // --- CẮT TỈA (PRUNING) ---
                // Nếu Alpha >= Beta, nghĩa là nhánh này đối thủ (Min) sẽ không bao giờ chọn
                // vì họ đã có lựa chọn tốt hơn ở nhánh khác. Cắt luôn!
                if (beta <= alpha) {
                    break; 
                }
            }
            return maxEval;

        } else { // Minimizing (Lượt đối thủ)
            int minEval = Integer.MAX_VALUE;
            for (Point move : moves) {
                // Mô phỏng
                Boardgame childBoard = copyBoard(board);
                GameLogic.makeMove(childBoard, move.x, move.y, currentPlayer);

                // Đệ quy
                int eval = minimax(childBoard, depth - 1, true, alpha, beta);
                
                minEval = Math.min(minEval, eval);
                
                // Cập nhật Beta (Giá trị thấp nhất mà MIN tìm được)
                beta = Math.min(beta, eval);
                
                // --- CẮT TỈA (PRUNING) ---
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    // --- HÀM ĐÁNH GIÁ (HEURISTIC) ---
    private int evaluateBoard(Boardgame board) {
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int piece = board.getPiece(i, j);
                if (piece == aiColor) {
                    score += WEIGHTS[i][j]; // Cộng điểm vị trí tốt
                } else if (piece == Player.getOpponent(aiColor)) {
                    score -= WEIGHTS[i][j]; // Trừ điểm nếu đối thủ chiếm vị trí tốt
                }
            }
        }
        return score;
    }

    // --- CÁC HÀM HỖ TRỢ ---
    
    // Helper Class lưu tọa độ
    public static class Point {
        public int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
    }

    // Lấy danh sách nước đi hợp lệ
    private List<Point> getValidMoves(Boardgame board, int player) {
        List<Point> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (GameLogic.isValidMove(board, i, j, player)) {
                    moves.add(new Point(i, j));
                }
            }
        }
        return moves;
    }

    // Copy bàn cờ để mô phỏng (Deep Copy)
    private Boardgame copyBoard(Boardgame original) {
        Boardgame copy = new Boardgame();
        // Boardgame mặc định gọi reset(), ta cần ghi đè lại trạng thái hiện tại
        // Lưu ý: Class Boardgame cần có method setPiece hoặc truy cập được board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy.setPiece(i, j, original.getPiece(i, j));
            }
        }
        return copy;
    }
    
    private boolean isGameOver(Boardgame board) {
        return !GameLogic.hasAnyMove(board, Player.BLACK) && 
               !GameLogic.hasAnyMove(board, Player.WHITE);
    }
}