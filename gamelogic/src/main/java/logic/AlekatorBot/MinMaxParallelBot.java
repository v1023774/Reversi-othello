package logic.AlekatorBot;

import logic.Board;
import logic.Cell;
import logic.Move;
import logic.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Класс представляет бота, использующего алгоритм Minimax для принятия решений с параллельным выполнением.
 */
public class MinMaxParallelBot extends Player {
    private int maxDepth;
    private long timeLimitMillis;
    private int stableCoinWeight = 10;
    private int semiStableCoinWeight = 0;
    private int unstableCoinWeight = -10;
    private int cornerWeight = 300;
    private int mobilityWeight = 1;
    private int stabilityWeight = 14;
    private static final int MAX_SCORE = 100;

    /**
     * Конструктор класса MinMaxParallelBot.
     *
     * @param playerCell      Символ (Cell), представляющий игрока (черные или белые).
     * @param maxDepth        Максимальная глубина поиска в алгоритме Minimax.
     * @param timeLimitMillis Временное ограничение на выполнение хода в миллисекундах.
     */
    public MinMaxParallelBot(Cell playerCell, int maxDepth, long timeLimitMillis) {
        super(playerCell);
        this.maxDepth = maxDepth;
        this.timeLimitMillis = timeLimitMillis;
    }

    /**
     * Выбирает лучший ход для текущего состояния доски, используя алгоритм Minimax с параллельным выполнением.
     *
     * @param board Текущее состояние доски.
     * @return Лучший ход для бота.
     */
    @Override
    public Move makeMove(Board board) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimitMillis;

        Move bestMove = null;

        for (int depth = 1; depth <= maxDepth; depth++) {
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            List<Move> availableMoves = board.getAllAvailableMoves(playerCell);
            int numMoves = availableMoves.size();
            int movesPerThread = numMoves / ((ThreadPoolExecutor) executor).getMaximumPoolSize();

            List<MinimaxWorker> workers = new ArrayList<>();

            for (int i = 0; i < ((ThreadPoolExecutor) executor).getMaximumPoolSize(); i++) {
                int startIdx = i * movesPerThread;
                int endIdx = (i == ((ThreadPoolExecutor) executor).getMaximumPoolSize() - 1) ? numMoves : (i + 1) * movesPerThread;

                MinimaxWorker worker = new MinimaxWorker(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, startIdx, endIdx, endTime);
                executor.execute(worker);
                workers.add(worker);
            }

            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            for (MinimaxWorker worker : workers) {
                MinimaxResult result = worker.getResult();
                if (result.move != null) {
                    bestMove = result.move;
                }
            }

            if (System.currentTimeMillis() >= endTime) {
                break;
            }
        }

        return bestMove;
    }

    /**
     * Внутренний класс для хранения результатов алгоритма Minimax.
     */
    private static class MinimaxResult {
        Move move;
        int score;

        MinimaxResult(Move move, int score) {
            this.move = move;
            this.score = score;
        }
    }

    /**
     * Внутренний класс для выполнения вычислений Minimax в отдельном потоке.
     */
    private class MinimaxWorker implements Runnable {
        private Board board;
        private int depth;
        private int alpha;
        private int beta;
        private boolean maximizingPlayer;
        private int startIdx;
        private int endIdx;
        private long endTime;
        private MinimaxResult result;

        private MinimaxResult minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer, long endTime) {
            if (depth == 0 || System.currentTimeMillis() >= endTime) {
                return new MinimaxResult(null, evaluate(board));
            }

            List<Move> availableMoves = board.getAllAvailableMoves(maximizingPlayer ? playerCell : playerCell.reverse());

            if (availableMoves.isEmpty()) {
                return new MinimaxResult(null, evaluate(board));
            }

            Move bestMove = null;

            if (maximizingPlayer) {
                int maxScore = Integer.MIN_VALUE;
                for (Move move : availableMoves) {
                    Board newBoard = board.placePieceAndGetCopy(move.row, move.col, playerCell);
                    int score = minimax(newBoard, depth - 1, alpha, beta, false, endTime).score;
                    if (score > maxScore) {
                        maxScore = score;
                        bestMove = move;
                    }
                    alpha = Math.max(alpha, maxScore);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return new MinimaxResult(bestMove, maxScore);
            } else {
                int minScore = Integer.MAX_VALUE;
                for (Move move : availableMoves) {
                    Board newBoard = board.placePieceAndGetCopy(move.row, move.col, playerCell.reverse());
                    int score = minimax(newBoard, depth - 1, alpha, beta, true, endTime).score;
                    if (score < minScore) {
                        minScore = score;
                        bestMove = move;
                    }
                    beta = Math.min(beta, minScore);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return new MinimaxResult(bestMove, minScore);
            }
        }

        /**
         * Конструктор MinimaxWorker.
         *
         * @param board            Текущее состояние доски.
         * @param depth            Текущая глубина поиска в алгоритме.
         * @param alpha            Альфа-значение для обрезания Alpha-Beta.
         * @param beta             Бета-значение для обрезания Alpha-Beta.
         * @param maximizingPlayer true, если это ход бота, иначе false.
         * @param startIdx         индекс начала подсчета ходов для потока.
         * @param endIdx           индекс окончания подсчета ходов для потока.
         * @param endTime          Время, к которому нужно закончить поиск (в миллисекундах).
         */
        MinimaxWorker(Board board, int depth, int alpha, int beta, boolean maximizingPlayer, int startIdx, int endIdx, long endTime) {
            this.board = board;
            this.depth = depth;
            this.alpha = alpha;
            this.beta = beta;
            this.maximizingPlayer = maximizingPlayer;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
            this.endTime = endTime;
        }

        /**
         * Запускает вычисления Minimax в отдельном потоке.
         */
        @Override
        public void run() {
            int maxScore = Integer.MIN_VALUE;
            Move localBestMove = null;

            List<Move> availableMoves = board.getAllAvailableMoves(playerCell);

            for (int i = startIdx; i < endIdx; i++) {
                Move move = availableMoves.get(i);
                Board newBoard = board.placePieceAndGetCopy(move.row, move.col, playerCell);
                int score = minimax(newBoard, depth - 1, alpha, beta, false, endTime).score;
                if (score > maxScore) {
                    maxScore = score;
                    localBestMove = move;
                }
                alpha = Math.max(alpha, maxScore);
                if (beta <= alpha) {
                    break;
                }
            }

            result = new MinimaxResult(localBestMove, maxScore);
        }

        /**
         * Получает результаты вычислений Minimax.
         *
         * @return Результаты вычислений Minimax.
         */
        public MinimaxResult getResult() {
            return result;
        }
    }

    /**
     * Оценивает текущее состояние доски для бота.
     *
     * @param board Текущее состояние доски.
     * @return Оценка состояния доски для бота.
     */
    private int evaluate(Board board) {
        int cornerEvaluation = evaluateCorners(board);
        int stabilityEvaluation = evaluateStability(board);
        int mobilityEvaluation = evaluateMobility(board);

        int totalEvaluation = cornerEvaluation * cornerWeight +
                stabilityEvaluation * (stableCoinWeight + semiStableCoinWeight + unstableCoinWeight) +
                mobilityEvaluation * mobilityWeight;
        totalEvaluation = (totalEvaluation * MAX_SCORE) /
                (cornerWeight + stabilityWeight + mobilityWeight);
        return totalEvaluation;
    }

    /**
     * Оценивает стабильность позиции на доске.
     *
     * @param board Текущее состояние доски.
     * @return Оценка стабильности позиции.
     */
    private int evaluateStability(Board board) {
        int maxPlayerStableCoins = countStableCoins(board, playerCell);
        int minPlayerStableCoins = countStableCoins(board, playerCell.reverse());

        int maxPlayerSemiStableCoins = countSemiStableCoins(board, playerCell);
        int minPlayerSemiStableCoins = countSemiStableCoins(board, playerCell.reverse());

        int maxPlayerUnstableCoins = countUnstableCoins(board, playerCell);
        int minPlayerUnstableCoins = countUnstableCoins(board, playerCell.reverse());

        int stabilityValue = (maxPlayerStableCoins - minPlayerStableCoins) * stableCoinWeight
                + (maxPlayerSemiStableCoins - minPlayerSemiStableCoins) * semiStableCoinWeight
                + (maxPlayerUnstableCoins - minPlayerUnstableCoins) * unstableCoinWeight;
        return stabilityValue;
    }

    /**
     * Оценивает угловые позиции на доске.
     *
     * @param board Текущее состояние доски.
     * @return Оценка угловых позиций.
     */
    private int evaluateCorners(Board board) {
        int maxPlayerCorners = countCorners(board, playerCell);
        int minPlayerCorners = countCorners(board, playerCell.reverse());

        int cornerValue = 0;
        if (maxPlayerCorners + minPlayerCorners != 0) {
            cornerValue = cornerWeight * (maxPlayerCorners - minPlayerCorners) / (maxPlayerCorners + minPlayerCorners);
        }
        return cornerValue;
    }

    /**
     * Оценивает подвижность на доске.
     *
     * @param board Текущее состояние доски.
     * @return Оценка подвижности.
     */
    private int evaluateMobility(Board board) {
        int maxPlayerActualMobility = calculateActualMobility(board, playerCell);
        int minPlayerActualMobility = calculateActualMobility(board, playerCell.reverse());

        int maxPlayerPotentialMobility = calculatePotentialMobility(board, playerCell);
        int minPlayerPotentialMobility = calculatePotentialMobility(board, playerCell.reverse());

        int actualMobilityValue = 0;
        if ((maxPlayerActualMobility + minPlayerActualMobility) != 0) {
            actualMobilityValue = 100 * (maxPlayerActualMobility - minPlayerActualMobility) /
                    (maxPlayerActualMobility + minPlayerActualMobility);
        }

        int potentialMobilityValue = 0;
        if ((maxPlayerPotentialMobility + minPlayerPotentialMobility) != 0) {
            potentialMobilityValue = 100 * (maxPlayerPotentialMobility - minPlayerPotentialMobility) /
                    (maxPlayerPotentialMobility + minPlayerPotentialMobility);
        }
        return (actualMobilityValue + potentialMobilityValue) * mobilityWeight;

    }

    /**
     * Рассчитывает актуальную подвижность для данной ячейки.
     *
     * @param board Текущее состояние доски.
     * @param cell  Символ (Cell), представляющий игрока (черные или белые).
     * @return Актуальная подвижность для данной ячейки.
     */
    private int calculateActualMobility(Board board, Cell cell) {
        List<Move> availableMoves = board.getAllAvailableMoves(cell);
        return availableMoves.size();
    }

    /**
     * Рассчитывает потенциальную подвижность для данной ячейки.
     *
     * @param board Текущее состояние доски.
     * @param cell  Символ (Cell), представляющий игрока (черные или белые).
     * @return Потенциальная подвижность для данной ячейки.
     */
    private int calculatePotentialMobility(Board board, Cell cell) {
        int potentialMobility = 0;
        int size = board.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.get(row, col) == Cell.EMPTY) {
                    int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

                    for (int[] dir : directions) {
                        int dr = dir[0];
                        int dc = dir[1];
                        int r = row + dr;
                        int c = col + dc;

                        while (r >= 0 && r < size && c >= 0 && c < size) {
                            if (board.get(r, c) == cell) {
                                potentialMobility++;
                                break;
                            } else if (board.get(r, c) == cell.reverse()) {
                                break;
                            }
                            r += dr;
                            c += dc;
                        }
                    }
                }
            }
        }

        return potentialMobility;
    }

    /**
     * Подсчитывает количество стабильных монет на доске.
     *
     * @param board Текущее состояние доски.
     * @param cell  Символ (Cell), представляющий игрока (черные или белые).
     * @return Количество стабильных монет.
     */
    private int countStableCoins(Board board, Cell cell) {
        int stableCoins = 0;
        int size = board.getSize();

        for (int row = 1; row < size - 1; row++) {
            for (int col = 1; col < size - 1; col++) {
                if (board.get(row, col) == cell) {
                    boolean isStable = true;

                    int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

                    for (int[] dir : directions) {
                        int dr = dir[0];
                        int dc = dir[1];
                        int r = row + dr;
                        int c = col + dc;

                        while (r >= 0 && r < size && c >= 0 && c < size && board.get(r, c) != Cell.EMPTY) {
                            if (board.get(r, c) == cell.reverse()) {
                                isStable = false;
                                break;
                            }
                            r += dr;
                            c += dc;
                        }

                        if (!isStable) {
                            break;
                        }
                    }

                    if (isStable) {
                        stableCoins++;
                    }
                }
            }
        }

        return stableCoins;
    }

    /**
     * Подсчитывает количество полустабильных монет на доске.
     *
     * @param board Текущее состояние доски.
     * @param cell  Символ (Cell), представляющий игрока (черные или белые).
     * @return Количество полустабильных монет.
     */
    private int countSemiStableCoins(Board board, Cell cell) {
        int semiStableCoins = 0;
        int size = board.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (row == 0 || row == size - 1 || col == 0 || col == size - 1) {
                    if (board.get(row, col) == cell) {
                        boolean isSemiStable = true;

                        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

                        for (int[] dir : directions) {
                            int dr = dir[0];
                            int dc = dir[1];
                            int r = row + dr;
                            int c = col + dc;

                            while (r >= 0 && r < size && c >= 0 && c < size && board.get(r, c) != Cell.EMPTY) {
                                if (board.get(r, c) == cell.reverse()) {
                                    isSemiStable = false;
                                    break;
                                }
                                r += dr;
                                c += dc;
                            }

                            if (!isSemiStable) {
                                break;
                            }
                        }

                        if (isSemiStable) {
                            semiStableCoins++;
                        }
                    }
                }
            }
        }

        return semiStableCoins;
    }

    /**
     * Подсчитывает количество нестабильных монет на доске.
     *
     * @param board Текущее состояние доски.
     * @param cell  Символ (Cell), представляющий игрока (черные или белые).
     * @return Количество нестабильных монет.
     */
    private int countUnstableCoins(Board board, Cell cell) {
        int totalCoins = board.countCell(cell);
        int stableCoins = countStableCoins(board, cell);
        int semiStableCoins = countSemiStableCoins(board, cell);

        return totalCoins - stableCoins - semiStableCoins;
    }

    /**
     * Подсчитывает количество углов на доске, занятых определенным символом.
     *
     * @param board Текущее состояние доски.
     * @param cell  Символ (Cell), представляющий игрока (черные или белые).
     * @return Количество углов, занятых данным символом.
     */

    private int countCorners(Board board, Cell cell) {
        int corners = 0;
        if (board.get(0, 0) == cell) corners++;
        if (board.get(0, board.getSize() - 1) == cell) corners++;
        if (board.get(board.getSize() - 1, 0) == cell) corners++;
        if (board.get(board.getSize() - 1, board.getSize() - 1) == cell) corners++;
        return corners;
    }
}
