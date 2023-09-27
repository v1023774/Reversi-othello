package logic;

import logic.ai.MovesCornersEvaluate;
import org.jetbrains.annotations.NotNull;

import static logic.ai.Minimax.getBestMove;

/**
 * AIBotIlya - бот играющий с встроенным алгоритмом minmax
 */
public class AIBotIlya extends Player {

    private int depth;

    public AIBotIlya(@NotNull final Cell playerCell, final int depth) {
        super(playerCell);
        this.depth = depth;
    }

    /**
     * makeMove - метод, возвращающий ход
     *
     * @param board - доска.
     */
    @Override
    public Move makeMove(@NotNull final Board board) {
        final Board copy = board.getBoardCopy();
        final long start = System.currentTimeMillis();
        final Move move = getBestMove(copy, playerCell, depth, new MovesCornersEvaluate());
        final long finish = System.currentTimeMillis();
        final long elapsed = finish - start;
        System.out.println("time for move movesandcorners " + elapsed);
        return move;
    }

}
