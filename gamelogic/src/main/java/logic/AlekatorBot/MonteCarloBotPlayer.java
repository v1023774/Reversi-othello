package logic.AlekatorBot;

import logic.Board;
import logic.Cell;
import logic.Move;
import logic.Player;

import java.util.List;

/**
 * Класс представляет бота, использующего алгоритм Monte Carlo Tree Search (MCTS) для принятия решений.
 */
public class MonteCarloBotPlayer extends Player {
    private static final int SIMULATION_COUNT = 1000;
    private static final double EXPLORATION_FACTOR = Math.sqrt(2.0);
    private final int maxDepth;
    private final long timeLimitMillis;
    private final long timeLimitMillisMC = 5000;

    /**
     * Конструктор класса MonteCarloBotPlayer.
     *
     * @param playerCell      Символ (Cell), представляющий игрока (черные или белые).
     * @param maxDepth        Максимальная глубина поиска в MCTS.
     * @param timeLimitMillis Временное ограничение на выполнение хода в миллисекундах.
     */
    public MonteCarloBotPlayer(Cell playerCell, int maxDepth, long timeLimitMillis) {
        super(playerCell);
        this.maxDepth = maxDepth;
        this.timeLimitMillis = timeLimitMillis;
    }

    /**
     * Выбирает лучший ход для текущего состояния доски, используя алгоритм Monte Carlo Tree Search (MCTS).
     *
     * @param board Текущее состояние доски.
     * @return Лучший ход для бота.
     */
    @Override
    public Move makeMove(Board board) {
        List<Move> legalMoves = board.getAllAvailableMoves(playerCell);

        if (legalMoves.isEmpty()) {
            return null;
        }

        Move bestMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeLimitMillisMC) {
            for (Move move : legalMoves) {
                Board newBoard = board.getBoardCopy();
                newBoard.placePiece(move.getRow(), move.getCol(), playerCell);

                double score = monteCarloSimulation(newBoard);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    /**
     * Выполняет одну итерацию симуляции MCTS для оценки позиции.
     *
     * @param board Текущее состояние доски.
     * @return Результат симуляции (вероятность выигрыша).
     */
    private double monteCarloSimulation(Board board) {
        int wins = 0;

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            Board copyBoard = board.getBoardCopy();
            Cell currentPlayer = playerCell;

            while (!copyBoard.isGameOver()) {
                List<Move> legalMoves = copyBoard.getAllAvailableMoves(currentPlayer);

                if (!legalMoves.isEmpty()) {
                    Move selectedMove = selectUCB1Move(copyBoard, legalMoves);
                    copyBoard.placePiece(selectedMove.getRow(), selectedMove.getCol(), currentPlayer);
                }

                currentPlayer = currentPlayer.reverse();
            }

            double score = evaluate(copyBoard);
            if (score > 0) {
                wins++;
            }
        }

        return (double) wins / SIMULATION_COUNT;
    }

    /**
     * Выбирает ход с использованием стратегии Upper Confidence Bound (UCB1).
     *
     * @param board      Текущее состояние доски.
     * @param legalMoves Список доступных ходов.
     * @return Лучший ход, выбранный на основе стратегии UCB1.
     */
    private Move selectUCB1Move(Board board, List<Move> legalMoves) {
        double bestUCB1 = Double.NEGATIVE_INFINITY;
        Move bestMove = null;
        int totalVisits = 0;

        for (Move move : legalMoves) {
            int visitCount = board.getVisitCount(move.getRow(), move.getCol());
            int actionCount = board.getActionCount(move.getRow(), move.getCol());

            double explorationWeight = EXPLORATION_FACTOR * Math.sqrt(Math.log(totalVisits + 1) / (actionCount + 1));
            double ucb1 = evaluate(board) + explorationWeight;

            if (ucb1 > bestUCB1) {
                bestUCB1 = ucb1;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * Оценивает текущее состояние доски для бота.
     *
     * @param board Текущее состояние доски.
     * @return Оценка состояния доски для бота.
     */
    private double evaluate(Board board) {
        int blackCount = 0;
        int whiteCount = 0;

        int blackCorners = 0;
        int whiteCorners = 0;

        int blackEdgeStability = 0;
        int whiteEdgeStability = 0;

        int blackMobility = 0;
        int whiteMobility = 0;

        int centerControl = 0;

        int potentialBlackMoves = 0;
        int potentialWhiteMoves = 0;

        int boardSize = board.getSize();
        int centerStart = boardSize / 4;
        int centerEnd = boardSize - centerStart;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Cell cell = board.get(row, col);

                if (cell == Cell.BLACK) {
                    blackCount++;
                } else if (cell == Cell.WHITE) {
                    whiteCount++;
                }

                if (row == 0 || row == boardSize - 1 || col == 0 || col == boardSize - 1) {
                    if (cell == Cell.BLACK) {
                        blackEdgeStability++;
                    } else if (cell == Cell.WHITE) {
                        whiteEdgeStability++;
                    }
                }

                if ((row == 0 || row == boardSize - 1) && (col == 0 || col == boardSize - 1)) {
                    if (cell == Cell.BLACK) {
                        blackCorners++;
                    } else if (cell == Cell.WHITE) {
                        whiteCorners++;
                    }
                }

                if (row >= centerStart && row < centerEnd && col >= centerStart && col < centerEnd) {
                    if (cell == Cell.BLACK) {
                        centerControl++;
                    } else if (cell == Cell.WHITE) {
                        centerControl--;
                    }
                }
            }
        }

        List<Move> blackMoves = board.getAllAvailableMoves(Cell.BLACK);
        List<Move> whiteMoves = board.getAllAvailableMoves(Cell.WHITE);

        blackMobility = blackMoves.size();
        whiteMobility = whiteMoves.size();

        for (Move move : blackMoves) {
            Board newBoard = board.getBoardCopy();
            newBoard.placePiece(move.row, move.col, Cell.BLACK);

            List<Move> opponentMoves = newBoard.getAllAvailableMoves(Cell.WHITE);
            potentialBlackMoves += opponentMoves.size();
        }

        for (Move move : whiteMoves) {
            Board newBoard = board.getBoardCopy();
            newBoard.placePiece(move.row, move.col, Cell.WHITE);

            List<Move> opponentMoves = newBoard.getAllAvailableMoves(Cell.BLACK);
            potentialWhiteMoves += opponentMoves.size();
        }

        double blackWeight = 1.0;
        double whiteWeight = -1.0;
        double cornerWeight = 5.0;
        double edgeStabilityWeight = 4.0;
        double mobilityWeight = 1;
        double centerControlWeight = 1.0;
        double potentialMovesWeight = 0.1;

        double totalScore =
                blackWeight * (blackCount - whiteCount) +
                        cornerWeight * (blackCorners - whiteCorners) +
                        edgeStabilityWeight * (blackEdgeStability - whiteEdgeStability) +
                        mobilityWeight * (blackMobility - whiteMobility) +
                        centerControlWeight * centerControl +
                        potentialMovesWeight * (potentialBlackMoves - potentialWhiteMoves);

        return totalScore;
    }
}