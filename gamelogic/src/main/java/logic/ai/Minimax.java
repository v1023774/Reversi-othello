package logic.ai;

import logic.Board;
import logic.Cell;
import logic.Move;
import org.jetbrains.annotations.NotNull;

/**
 * Minimax Algorithm
 */
public class Minimax{

    private Minimax() {
    }

    /**
     * ограничение на время хода
     */
    private static final long maxCalculationTime = 5000;
    private static long startCalculationTime = 0;

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
        startCalculationTime = System.currentTimeMillis();

        for (Move m : board.getAllAvailableMoves(cell)) {
            final Board copyBoard = board.getBoardCopy();
            copyBoard.placePiece(m.row, m.col, cell);

            double childScore = minimax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, copyBoard, cell, false, he);

            if (childScore > bestScore) {
                bestScore = childScore;
                bestMove = m;
            }
        }
        return bestMove;
    }

    /**
     * Метод minmax находит наиболее выгодный ход(с наибольшей выгодой для себя, при самых лучших ходах оппонента), строя все дерево, на глубину depth
     *
     * @param depth глубина.
     * @param alpha альфа.
     * @param beta  бета.
     * @param board доска.
     * @param cell  цвет игрока.
     * @param max   максимизируем или минимизируем.
     * @param he    класс оценочной функции.
     */
    public static double minimax(final int depth, double alpha, double beta, @NotNull final Board board,
                                 @NotNull final Cell cell, final boolean max, @NotNull final HeuristicEvaluation he) {

        final Cell opponent = (cell == Cell.BLACK) ? Cell.WHITE : Cell.BLACK;

        if (depth == 0 || (board.getAllAvailableMoves(cell).isEmpty() && board.getAllAvailableMoves(opponent).isEmpty())
                || System.currentTimeMillis() - startCalculationTime >= maxCalculationTime) {
            return he.countValue(board, cell);
        }

        if ((max && board.getAllAvailableMoves(cell).isEmpty()) || (!max && board.getAllAvailableMoves(opponent).isEmpty())) {
            return minimax(depth - 1, alpha, beta, board, cell, !max, he);
        }

        double score;

        if (max) {
            score = Integer.MIN_VALUE;
            for (Move m : board.getAllAvailableMoves(cell)) {
                final Board copyBoard = board.getBoardCopy();
                copyBoard.placePiece(m.row, m.col, cell);

                double childScore = minimax(depth - 1, alpha, beta, copyBoard, cell, false, he);
                if (childScore > score) score = childScore;
                if (score > alpha) alpha = score;
                if (beta <= alpha) break;
            }
        } else {
            score = Integer.MAX_VALUE;
            for (Move m : board.getAllAvailableMoves(opponent)) {
                final Board copyBoard = board.getBoardCopy();
                copyBoard.placePiece(m.row, m.col, opponent);

                double childScore = minimax(depth - 1, alpha, beta, copyBoard, cell, true, he);
                if (childScore < score) score = childScore;
                if (score < beta) beta = score;
                if (beta <= alpha) break;
            }
        }
        return score;
    }
}
