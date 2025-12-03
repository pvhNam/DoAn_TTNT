package model;

public class Logic {
	private static final int[] ROW = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] COL = {-1, 0, 1, -1, 1, -1, 0, 1};
    // kiểm tra nước đi hợp lệ
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
                    if (hasOpponentBetween) return true;
                    else break; 
                } else {
                    break;
                }
                x += ROW[i];
                y += COL[i];
            }
        }
        return false;
    }
    // thực hiện lật quân cờ 
    public static void makeMove(Boardgame game, int r, int c, int player) {
        game.setPiece(r, c, player);
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
                    if (hasOpponentBetween) {
                        int flipX = r + ROW[i];
                        int flipY = c + COL[i];
                        while (flipX != x || flipY != y) {
                            game.setPiece(flipX, flipY, player);
                            flipX += ROW[i];
                            flipY += COL[i];
                        }
                    }
                    break;
                } else {
                    break;
                }
                x += ROW[i];
                y += COL[i];
            }
        }
    }
    // kiểm tra vị trí đặt quân cờ
    private static boolean isValidPos(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
