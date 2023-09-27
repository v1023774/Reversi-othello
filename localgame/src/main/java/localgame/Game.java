package localgame;

import gamelogging.GameLogger;
import logic.Board;
import logic.Cell;
import logic.Move;
import logic.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Класс Game дает возможность запустить игру.
 */
public class Game {
    /**
     * номер хода в игре
     */
    public int numberOfTurn = 1;

    /**
     * цвет игрока который будет ходить следующим
     */
    public Cell nextTurnOfPlayerColor = Cell.BLACK;

    /**
     * Метод startGame запускает игру. По окончанию игры выводится результат.
     *
     * @param board             доска.
     * @param black             игрок черными.
     * @param white             игрок белыми.
     * @param gameId            id игры.
     * @param sessionPlayerFile файл в который будет записан лог игры.
     */
    public void startGame(@NotNull final Board board, @NotNull final Player black, @NotNull final Player white, final int gameId,
                          @NotNull final String sessionPlayerFile) {
        GameLogger.logStart(gameId, sessionPlayerFile);
        int moveNumber = 1;
        while (!board.getAllAvailableMoves(black.playerCell).isEmpty() || !board.getAllAvailableMoves(white.playerCell).isEmpty()) {
            Board copyBoard = board.getBoardCopy();
            moveNumber = makeMoveOnBoard(board, black, moveNumber, copyBoard, sessionPlayerFile);
            copyBoard = board.getBoardCopy();
            moveNumber = makeMoveOnBoard(board, white, moveNumber, copyBoard, sessionPlayerFile);
        }
        GameLogger.logEnd(board, sessionPlayerFile);
        displayResult(board);
    }

    /**
     * Метод makeMoveOnBoard ставит фишку на доске и увеличивает номер хода на 1
     *
     * @param board         доска.
     * @param player        игрок.
     * @param moveNumber    номер хода
     * @param copyBoard     копия доски.
     * @param writeForHuman файл в который будет записан лог игры.
     */
    private static int makeMoveOnBoard(@NotNull final Board board, @NotNull final Player player,
                                       int moveNumber, @NotNull final Board copyBoard, @NotNull final String writeForHuman) {
        if (!board.getAllAvailableMoves(player.playerCell).isEmpty()) {
            final Move blackMove = player.makeMove(copyBoard);
            board.placePiece(blackMove.row, blackMove.col, player.playerCell);
            GameLogger.logMove(board, blackMove.row, blackMove.col, player.playerId, String.valueOf(player.playerCell), writeForHuman, moveNumber);
            moveNumber++;
        }
        return moveNumber;
    }

    /**
     * Метод displayResult отображает результат партии.
     *
     * @param board доска.
     */
    private static void displayResult(@NotNull final Board board) {
        final int blackCount = board.getQuantityOfBlack();
        final int whiteCount = board.getQuantityOfWhite();

        System.out.println("Number of Black pieces: " + blackCount);
        System.out.println("Number of White pieces: " + whiteCount);

        if (blackCount > whiteCount) {
            System.out.println("Winner: Black");
        } else if (whiteCount > blackCount) {
            System.out.println("Winner: White");
        } else {
            System.out.println("It's a Tie");
        }
    }

    /**
     * Метод displayResultOnClient отображает результат партии.
     *
     * @param board доска.
     */
    public static String displayResultOnClient(@NotNull final Board board) {
        final int blackCount = board.getQuantityOfBlack();
        final int whiteCount = board.getQuantityOfWhite();
        String results = "";
        results += "Number of Black pieces: " + blackCount + "\n";
        results += "Number of White pieces: " + whiteCount + "\n";

        if (blackCount > whiteCount) {
            results += "Winner: Black" + "\n";
        } else if (whiteCount > blackCount) {
            results += "Winner: White" + "\n";
        } else {
            results += "It's a Tie" + "\n";
        }
        return results;
    }


}
