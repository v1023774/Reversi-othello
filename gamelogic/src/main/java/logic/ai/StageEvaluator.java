package logic.ai;

import logic.Board;
import logic.Cell;
import org.jetbrains.annotations.NotNull;

/**
 * StageEvaluator класс для оценки доски
 */
public class StageEvaluator implements HeuristicEvaluation {

    enum GamePhase {
        EARLY_GAME,
        MID_GAME,
        LATE_GAME
    }

    /**
     * Метод возвращающий стадию игры
     *
     * @param board доска.
     */
    private static GamePhase getGamePhase(@NotNull final Board board) {
        final int stones = 64 - board.getQuantityOfEmpty();
        if (stones < 20) return GamePhase.EARLY_GAME;
        else if (stones <= 58) return GamePhase.MID_GAME;
        else return GamePhase.LATE_GAME;
    }

    /**
     * Метод оценки доски, зависящий от занятых углов, количества камней, количества возможных ходов и стадии игры
     *
     * @param board доска.
     * @param cell  цвет.
     */
    public double countValue(@NotNull final Board board, @NotNull final Cell cell) {

        if (board.getAllAvailableMoves(Cell.WHITE).isEmpty() && board.getAllAvailableMoves(Cell.BLACK).isEmpty()) {
            return 1000 * MovesCornersEvaluate.countQuantityOfStones(board, cell);
        }

        switch (getGamePhase(board)) {
            case EARLY_GAME:
                return 1000 * MovesCornersEvaluate.countCapturedCorners(board, cell) +
                        50 * MovesCornersEvaluate.countAllAvailableMoves(board, cell);
            case MID_GAME:
                return 1000 * MovesCornersEvaluate.countCapturedCorners(board, cell) +
                        20 * MovesCornersEvaluate.countAllAvailableMoves(board, cell) +
                        10 * MovesCornersEvaluate.countQuantityOfStones(board, cell);
            case LATE_GAME:
            default:
                return 1000 * MovesCornersEvaluate.countCapturedCorners(board, cell) +
                        100 * MovesCornersEvaluate.countAllAvailableMoves(board, cell) +
                        500 * MovesCornersEvaluate.countQuantityOfStones(board, cell);
        }
    }
}
