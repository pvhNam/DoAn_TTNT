package model;

public class GameLogic {
    
    // 8 hướng (Ngang, Dọc, Chéo)
    private static final int[] ROW = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] COL = {-1, 0, 1, -1, 1, -1, 0, 1};

    /**
     * Kiểm tra xem người chơi có thể đặt cờ vào (r, c) không
     */
    public static boolean isValidMove(Boardgame game, int r, int c, int player) {
        if (game.getPiece(r, c) != Player.EMPTY) return false;

        int opponent = Player.getOpponent(player);

        for (int i = 0; i < 8; i++) {
            int x = r + ROW[i];
            int y = c + COL[i];
            boolean hasOpponentBetween = false;

            while (isValidPos(x, y)) {
                int p = game.getPiece(x, y);
                if (p == opponent) {
                    hasOpponentBetween = true;
                } else if (p == player) {
                    if (hasOpponentBetween) return true; // Tìm thấy kẹp
                    else break; 
                } else {
                    break; // Gặp ô trống
                }
                x += ROW[i];
                y += COL[i];
            }
        }
        return false;
    }

    /**
     * Thực hiện nước đi và lật quân đối phương
     */
    public static void makeMove(Boardgame game, int r, int c, int player) {
        game.setPiece(r, c, player);
        int opponent = Player.getOpponent(player);

        for (int i = 0; i < 8; i++) {
            int x = r + ROW[i];
            int y = c + COL[i];
            boolean hasOpponentBetween = false;
            
            // Kiểm tra hướng này có lật được không
            int tempX = x, tempY = y;
            while (isValidPos(tempX, tempY)) {
                int p = game.getPiece(tempX, tempY);
                if (p == opponent) {
                    hasOpponentBetween = true;
                } else if (p == player) {
                    if (hasOpponentBetween) {
                        // Lật ngược lại từ vị trí hiện tại về vị trí đặt
                        int flipX = r + ROW[i];
                        int flipY = c + COL[i];
                        while (flipX != tempX || flipY != tempY) {
                            game.setPiece(flipX, flipY, player);
                            flipX += ROW[i];
                            flipY += COL[i];
                        }
                    }
                    break;
                } else {
                    break;
                }
                tempX += ROW[i];
                tempY += COL[i];
            }
        }
    }

    public static boolean hasAnyMove(Boardgame game, int player) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(game, i, j, player)) return true;
            }
        }
        return false;
    }

    private static boolean isValidPos(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}