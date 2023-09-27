package logic.ai;

import logic.Board;
import logic.Cell;
import org.jetbrains.annotations.NotNull;

/**
 * QuantityEvaluate класс для оценки доски
 */
public class QuantityEvaluate implements HeuristicEvaluation {
    /**
     * Метод оценки доски, зависящий от количества камней
     *
     * @param board доска.
     * @param cell  цвет.
     */
    public double countValue(@NotNull final Board board, @NotNull final Cell cell) {
        final Cell opponent = (cell == Cell.BLACK) ? Cell.WHITE : Cell.BLACK;
        if (board.getAllAvailableMoves(opponent).isEmpty())
            return Integer.MAX_VALUE;
        return (cell == Cell.BLACK) ? board.getQuantityOfBlack() / board.getQuantityOfWhite() : board.getQuantityOfWhite() / board.getQuantityOfBlack();
    }
}
