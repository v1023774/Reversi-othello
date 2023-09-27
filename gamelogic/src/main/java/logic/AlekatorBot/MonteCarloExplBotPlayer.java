package logic.AlekatorBot;

import logic.Board;
import logic.Cell;
import logic.Move;
import logic.Player;

import java.util.List;


/**
 * Класс представляет бота, использующего алгоритм Monte Carlo Tree Search (MCTS) для принятия решений.
 */
public class MonteCarloExplBotPlayer extends Player {
    private static final int SIMULATION_COUNT = 1000;
    private final int maxDepth;
    private final long timeLimitMillis;
    private final long timeLimitMillisMC = 5000;
    private int stableCoinWeight = 10;
    private int semiStableCoinWeight = 0;
    private int unstableCoinWeight = -10;
    private int cornerWeight = 300;
    private int mobilityWeight = 1;
    private int stabilityWeight = 14;
    private static final int MAX_SCORE = 100;

    /**
     * Конструктор класса MonteCarloBotPlayer.
     *
     * @param playerCell      Символ (Cell), представляющий игрока (черные или белые).
     * @param maxDepth        Максимальная глубина поиска в MCTS.
     * @param timeLimitMillis Временное ограничение на выполнение хода в миллисекундах.
     */
    public MonteCarloExplBotPlayer(Cell playerCell, int maxDepth, long timeLimitMillis) {
        super(playerCell);
        this.maxDepth = maxDepth;
        this.timeLimitMillis = timeLimitMillis;
    }

    /**
     * Выбирает ход для текущего состояния доски, используя алгоритм Monte Carlo Tree Search (MCTS).
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
                    Move selectedMove = selectExplorationExploitationMove(copyBoard, legalMoves);
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
     * Выбирает ход, учитывая стратегию Exploration-Exploitation.
     *
     * @param board      Текущее состояние доски.
     * @param legalMoves Список доступных ходов.
     * @return Лучший ход, выбранный на основе стратегии Exploration-Exploitation.
     */
    private Move selectExplorationExploitationMove(Board board, List<Move> legalMoves) {
        Move bestMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        int totalVisits = 0;

        for (Move move : legalMoves) {
            int visitCount = board.getVisitCount(move.getRow(), move.getCol());
            int stabilityEvaluation = evaluateStability(board);
            int cornersEvaluation = evaluateCorners(board);
            int mobilityEvaluation = evaluateMobility(board);

            double exploration = Math.sqrt(Math.log(totalVisits + 1) / (visitCount + 1));
            double explorationExploitationScore = (stabilityEvaluation * stableCoinWeight +
                    cornersEvaluation * cornerWeight +
                    mobilityEvaluation * mobilityWeight) +
                    exploration;

            if (explorationExploitationScore > bestScore) {
                bestScore = explorationExploitationScore;
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