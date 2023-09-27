package logic.ai;

import logic.Board;
import logic.Cell;

/**
 *  interface HeuristicEvaluation - интерфейс для создания оценочных фукнций доски
 */
public interface HeuristicEvaluation {
    double countValue(Board board, Cell cell);
}
