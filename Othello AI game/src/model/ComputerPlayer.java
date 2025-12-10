package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ComputerPlayer {
    private int computerSide; // Máy cầm quân Đen hay Trắng
    private int maxDepth = 1 ; // Độ sâu tìm kiếm (càng cao càng thông minh nhưng chậm)

    public ComputerPlayer(int computerSide) {
        this.computerSide = computerSide;
    }

    // Hàm này được Controller gọi để lấy nước đi tốt nhất
    public Point getBestMove(Boardgame game) {
        int bestScore = Integer.MIN_VALUE;
        Point bestMove = null;
        
        // Lấy danh sách các nước đi hợp lệ hiện tại
        List<Point> validMoves = getValidMoves(game.getBoard(), computerSide);

        // Duyệt qua từng nước đi có thể để tìm nước tốt nhất (Lớp ngoài cùng của Minimax)
        for (Point move : validMoves) {
            // 1. Tạo bàn cờ giả lập
            int[][] tempBoard = cloneBoard(game.getBoard());
            // 2. Thử đi nước này trên bàn cờ giả
            simulateMove(tempBoard, move.x, move.y, computerSide);
            
            // 3. Gọi để quy Minimax để tính điểm cho nước đi này
            // depth - 1 vì đã đi 1 nước rồi. isMaximizing = false vì lươt sau là người chơi (Min)
            int score = minimax(tempBoard, maxDepth - 1, false, Player.getOpponent(computerSide));

            // 4. Nếu điểm cao hơn điểm tốt nhất hiện tại thì cập nhật
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    // --- THUẬT TOÁN MINIMAX THEO ẢNH CÔ GIÁO ---
    // maxmin trong ảnh tương ứng với biến isMaximizing ở đây
    private int minimax(int[][] currentBoard, int depth, boolean isMaximizing, int currentTurnPlayer) {
        // Cơ sở: Nếu hết độ sâu hoặc game kết thúc (ở đây mình đơn giản hóa là độ sâu = 0)
        if (depth == 0) {
            return heuristic(currentBoard);
        }

        List<Point> validMoves = getValidMoves(currentBoard, currentTurnPlayer);
        
        // Nếu không còn nước đi nào (Game over hoặc bị pass lượt) -> trả về điểm luôn
        if (validMoves.isEmpty()) {
            return heuristic(currentBoard);
        }

        if (isMaximizing) { // Máy tính (Max)
            int maxEval = Integer.MIN_VALUE; // Giống int temp = -99999999 trong ảnh
            for (Point move : validMoves) {
                // Tạo node con (bàn cờ mới)
                int[][] nextBoard = cloneBoard(currentBoard);
                simulateMove(nextBoard, move.x, move.y, currentTurnPlayer);

                // Gọi đệ quy: đến lượt người chơi (Min)
                int eval = minimax(nextBoard, depth - 1, false, Player.getOpponent(currentTurnPlayer));
                
                // Cập nhật max
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else { // Người chơi (Min)
            int minEval = Integer.MAX_VALUE; // Giống int temp = 99999999 trong ảnh
            for (Point move : validMoves) {
                // Tạo node con
                int[][] nextBoard = cloneBoard(currentBoard);
                simulateMove(nextBoard, move.x, move.y, currentTurnPlayer);

                // Gọi đệ quy: đến lượt máy (Max)
                int eval = minimax(nextBoard, depth - 1, true, Player.getOpponent(currentTurnPlayer));
                
                // Cập nhật min
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    // --- HÀM HEURISTIC (ĐÁNH GIÁ THẾ CỜ) ---
    private int heuristic(int[][] board) {
        int myScore = 0;
        int opScore = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == computerSide) {
                    myScore++;
                    // Cộng điểm cực lớn nếu chiếm được góc (Vị trí chiến lược trong Othello)
                    if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
                        myScore += 10; 
                    }
                } else if (board[i][j] == Player.getOpponent(computerSide)) {
                    opScore++;
                    if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
                        opScore += 10;
                    }
                }
            }
        }
        return myScore - opScore;
    }

    // --- CÁC HÀM PHỤ TRỢ ---

    // Copy mảng 2 chiều để không ảnh hưởng bàn cờ chính
    private int[][] cloneBoard(int[][] source) {
        int[][] newBoard = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(source[i], 0, newBoard[i], 0, 8);
        }
        return newBoard;
    }

    // Tìm các nước đi hợp lệ cho một trạng thái bàn cờ bất kỳ
    private List<Point> getValidMoves(int[][] board, int player) {
        List<Point> moves = new ArrayList<>();
        // Chúng ta cần một hàm kiểm tra valid move mà không phụ thuộc vào object Boardgame
        // Tuy nhiên để tận dụng code cũ, ta sẽ dùng Logic class nhưng cần sửa đổi một chút
        // Hoặc viết lại logic check đơn giản ở đây để giả lập:
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                 // Lưu ý: Ở đây ta cần gọi hàm Logic static. 
                 // Để đơn giản, ta cần sửa Logic.java để nhận int[][] thay vì Boardgame object
                 // Nhưng để tránh sửa nhiều, ta tạo 1 Boardgame giả tạm thời:
                 Boardgame tempGame = new Boardgame();
                 setBoardRaw(tempGame, board); // Hàm hack set board, xem bên dưới
                 if (Logic.isValidMove(tempGame, i, j, player)) {
                     moves.add(new Point(i, j));
                 }
            }
        }
        return moves;
    }
    
    // Giả lập nước đi trên bàn cờ ảo (Copy logic từ Logic.makeMove nhưng chạy trên int[][])
    private void simulateMove(int[][] board, int r, int c, int player) {
        // Để đơn giản hóa việc code lại logic lật cờ, ta dùng lại class Logic 
        // bằng cách tạo Boardgame tạm
        Boardgame tempGame = new Boardgame();
        setBoardRaw(tempGame, board);
        Logic.makeMove(tempGame, r, c, player);
        
        // Sau khi Logic xử lý xong, copy ngược lại vào board mảng
        for(int i=0; i<8; i++) {
            System.arraycopy(tempGame.getBoard()[i], 0, board[i], 0, 8);
        }
    }

    // Hàm phụ để nhét mảng int[][] vào object Boardgame (cần thêm method setBoard vào Boardgame)
    private void setBoardRaw(Boardgame game, int[][] data) {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                game.setPiece(i, j, data[i][j]);
            }
        }
    }
}