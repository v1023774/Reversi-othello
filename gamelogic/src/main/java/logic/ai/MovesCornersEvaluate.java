package logic.ai;

import logic.Board;
import logic.Cell;
import org.jetbrains.annotations.NotNull;

/**
 * MovesCornersEvaluate класс для оценки доски
 */
public class MovesCornersEvaluate implements HeuristicEvaluation {

    /**
     * Метод оценки доски, зависящий от занятых углов, количества камней и количества возможных ходов
     *
     * @param board доска.
     * @param cell  цвет.
     */
    public double countValue(@NotNull final Board board, @NotNull final Cell cell) {
        final double moves = countAllAvailableMoves(board, cell);
        final double stones = countQuantityOfStones(board, cell);
        return 2 * moves + stones + 1000 * countCapturedCorners(board, cell);
    }
    /**
     * Метод countQuantityOfStones считает value отталкиваясь от количества камней
     *
     * @param board доска.
     * @param cell  цвет.
     */
    public static double countQuantityOfStones(@NotNull final Board board, @NotNull final Cell cell) {
        final Cell opponent = (cell == Cell.BLACK) ? Cell.WHITE : Cell.BLACK;

        final double myMoves = board.getQuantityOfYourStones(cell);
        final double opponentMoves = board.getQuantityOfYourStones(opponent);

        return 100 * (myMoves - opponentMoves) / (myMoves + opponentMoves);
    }
    /**
     * Метод countAllAvailableMoves считает value отталкиваясь от количества возможных ходов
     *
     * @param board доска.
     * @param cell  цвет.
     */
    public static double countAllAvailableMoves(@NotNull final Board board, @NotNull final Cell cell) {
        final Cell opponent = (cell == Cell.BLACK) ? Cell.WHITE : Cell.BLACK;

        final double myMoveCount = board.getAllAvailableMoves(cell).size();
        final double opponentMoveCount = board.getAllAvailableMoves(opponent).size();

        return 100 * (myMoveCount - opponentMoveCount) / (myMoveCount + opponentMoveCount + 1);
    }
    /**
     * Метод countCapturedCorners считает value отталкиваясь от количества возможных занятях углов
     *
     * @param board доска.
     * @param cell  цвет.
     */
    public static double countCapturedCorners(@NotNull final Board board, @NotNull final Cell cell) {
        final Cell opponent = (cell == Cell.BLACK) ? Cell.WHITE : Cell.BLACK;

        double myCorners = 0;
        double opponentCorners = 0;

        if (board.get(0, 0) == cell) myCorners++;
        if (board.get(7, 0) == cell) myCorners++;
        if (board.get(0, 7) == cell) myCorners++;
        if (board.get(7, 7) == cell) myCorners++;

        if (board.get(0, 0) == opponent) opponentCorners++;
        if (board.get(7, 0) == opponent) opponentCorners++;
        if (board.get(0, 7) == opponent) opponentCorners++;
        if (board.get(7, 7) == opponent) opponentCorners++;

        return 100 * (myCorners - opponentCorners) / (myCorners + opponentCorners + 1);
    }
}
