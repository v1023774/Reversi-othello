package logic.ai;

import logic.Board;
import logic.Cell;
import logic.Move;
import org.jetbrains.annotations.NotNull;

/**
 * Expectimax Algorithm
 */
public class Expectimax{
    private Expectimax() {
    }

    /**
     * Метод getBestMove находит лучших ход
     *
     * @param board доска.
     * @param cell  цвет игрока.
     * @param depth глубина.
     * @param he    класс оценочной функции.
     */
    public static Move getBestMove(@NotNull final Board board, @NotNull final Cell cell, final int depth, @NotNull final HeuristicEvaluation he) {

        double bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (Move m : board.getAllAvailableMoves(cell)) {
            final Board copyBoard = board.getBoardCopy();
            copyBoard.placePiece(m.row, m.col, cell);

            final double childScore = expectimax(depth - 1, copyBoard, cell, false, he);

            if (childScore > bestScore) {
                bestScore = childScore;
                bestMove = m;
            }
        }
        return bestMove;
    }

    /**
     * Метод expectimax находит наиболее выгодный ход(самый ценный ход) , строя все дерево, на глубину depth
     *
     * @param depth глубина.
     * @param board доска.
     * @param cell  цвет игрока.
     * @param max   максимизируем или минимизируем.
     * @param he    класс оценочной функции.
     */
    private static double expectimax(final int depth, @NotNull final Board board,
                                     @NotNull final Cell cell, final boolean max, @NotNull final HeuristicEvaluation he) {

        final Cell opponent = (cell == Cell.BLACK) ? Cell.WHITE : Cell.BLACK;

        if (depth == 0 || (board.getAllAvailableMoves(cell).isEmpty() && board.getAllAvailableMoves(opponent).isEmpty())) {
            return he.countValue(board, cell);
        }

        if ((max && board.getAllAvailableMoves(cell).isEmpty()) || (!max && board.getAllAvailableMoves(opponent).isEmpty())) {
            return expectimax(depth - 1, board, cell, !max, he);
        }

        double score;

        if (max) {
            score = Integer.MIN_VALUE;
            for (Move m : board.getAllAvailableMoves(cell)) {
                Board copyBoard = board.getBoardCopy();
                copyBoard.placePiece(m.row, m.col, cell);

                score = Math.max(expectimax(depth - 1, copyBoard, cell, false, he), score);

            }
        } else {
            score = 0;
            for (Move m : board.getAllAvailableMoves(opponent)) {
                Board copyBoard = board.getBoardCopy();
                copyBoard.placePiece(m.row, m.col, opponent);

                score += expectimax(depth - 1, copyBoard, cell, true, he);

            }
            score = score / board.getAllAvailableMoves(opponent).size();
        }
        return score;
    }
}
