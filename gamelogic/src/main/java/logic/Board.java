package logic;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Класс доски, и всей связанной с ней логикой.
 */
public class Board {
    private final Cell[][] board;
    private static final int BOARD_SIZE = 8;
    private final Logger logger = LogManager.getLogger(Board.class);
    private int[][] visitCounts;
    private int[][] actionCounts;
    /**
     * Создает доску и четыре фишки по центру карты.
     */
    public Board() {

        board = new Cell[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int cow = 0; cow < BOARD_SIZE; cow++) {
                board[row][cow] = Cell.EMPTY;
            }
        }

        board[3][3] = Cell.WHITE;
        board[4][4] = Cell.WHITE;
        board[3][4] = Cell.BLACK;
        board[4][3] = Cell.BLACK;
    }


    /**
     * Установка диска в указанное поле.
     *
     * @param row  - строка.
     * @param col  - колонка.
     * @param cell - клетка, который нужно поставить в поле.
     */
    public void set(final int row, final int col, @NotNull final Cell cell) {
        checkArgument(row, col);
        board[row][col] = cell;
    }

    /**
     * Возвращает значение cell, которое лежит в поле.
     *
     * @param row - строка.
     * @param col - колонка.
     * @return возвращает клетку.
     */
    public Cell get(final int row, final int col) {
        checkArgument(row, col);
        return board[row][col];
    }

    /**
     * метод проверки на возможность хода
     *
     * @param row - строка.
     * @param col - колонка.
     * @return true если ход возможен.
     */
    public boolean isValidMove(final int row, final int col, @NotNull final Cell cell) {

        checkArgument(row, col);
        if (board[row][col] != Cell.EMPTY) {
            return false;
        }

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }

                int r = row + dr;
                int c = col + dc;
                boolean isValidDirection = false;

                while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == cell.reverse()) {
                    r += dr;
                    c += dc;
                    isValidDirection = true;
                }

                if (isValidDirection && r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == cell) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Метод getAllAvailableMoves в классе Board предназначен для получения списка всех доступных ходов для указанной
     * фишки (цвета) на текущей доске.
     *
     * @param cell тип фишки (цвет), для которой нужно получить доступные ходы.
     * @return список ходов типа List<Move>, представляющий все доступные ходы для указанной фишки.
     */
    public List<Move> getAllAvailableMoves(@NotNull final Cell cell) {
        final List<Move> moves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isValidMove(i, j, cell)) {
                    moves.add(new Move(i, j));
                }
            }
        }
        return moves;
    }


    /**
     * Возвращает количество белых клеток.
     */
    public int getQuantityOfWhite() {
        int sum = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j] == Cell.WHITE) {
                    sum++;
                }
            }
        }
        return sum;

    }

    /**
     * Возвращает количество черных клеток.
     */
    public int getQuantityOfBlack() {
        int sum = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j] == Cell.BLACK) {
                    sum++;
                }
            }
        }
        return sum;
    }

    /**
     * Возвращает количество клеток цыета cell.
     *
     * @param cell - цвет клетки
     */
    public int getQuantityOfYourStones(@NotNull final Cell cell) {
        int sum = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j] == cell) {
                    sum++;
                }
            }
        }
        return sum;
    }

    /**
     * Возвращает количество пустых клеток.
     */
    public int getQuantityOfEmpty() {
        int sum = 64;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j] != Cell.EMPTY) {
                    sum--;
                }
            }
        }
        return sum;
    }

    private void checkArgument(final int row, final int col) {
        if (row >= BOARD_SIZE || row < 0 || col >= BOARD_SIZE || col < 0) {
            logger.log(Level.ERROR, "Ошибка в передачи координат на доску.");
            throw new IllegalArgumentException();
        }
    }

    /**
     * Возвращает размер доски.
     */
    public int getSize() {
        return BOARD_SIZE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board board1)) return false;
        return Arrays.deepEquals(board, board1.board);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(BOARD_SIZE);
        result = 31 * result + Arrays.deepHashCode(board);
        return result;
    }

    /**
     * Метод создает копию доски.
     *
     * @return копия доски.
     */
    public Board getBoardCopy() {
        Board copy = new Board();
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(this.board[i], 0, copy.board[i], 0, BOARD_SIZE);
        }
        return copy;
    }

    /**
     * Устанавливает фишку в указанное место, переворачивая другие фишки в соответсвии с логикой игры.
     *
     * @param row        - строка.
     * @param col        - колонна.
     * @param playerCell - цвет игрока.
     */
    public void placePiece(final int row, final int col, @NotNull final Cell playerCell) {
        set(row, col, playerCell);

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }

                int r = row + dr;
                int c = col + dc;
                boolean isValidDirection = false;
                boolean hasOpponentPiece = false;

                while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == playerCell.reverse()) {
                    r += dr;
                    c += dc;
                    isValidDirection = true;
                    hasOpponentPiece = true;
                }

                if (isValidDirection && r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == playerCell && hasOpponentPiece) {
                    while (r != row || c != col) {
                        r -= dr;
                        c -= dc;
                        set(r, c, playerCell);
                    }
                }
            }
        }
    }

    /**
     * Выпоняет метод placePiece, создает копию доски после хода.
     *
     * @param row        - строка.
     * @param col        - колонна.
     * @param playerCell - клетка цвета игрока, осуществляющего ход.
     * @return возвращает копию доски после хода.
     */
    public Board placePieceAndGetCopy(final int row, final int col, @NotNull final Cell playerCell) {
        final Board copy = getBoardCopy();
        copy.placePiece(row, col, playerCell);
        return copy;
    }

    /**
     * Метод проверяющий конец игры
     */
    public boolean isGameOver() {
        final List<Move> blackMoves = getAllAvailableMoves(Cell.BLACK);
        final List<Move> whiteMoves = getAllAvailableMoves(Cell.WHITE);

        return !blackMoves.isEmpty() || !whiteMoves.isEmpty();
    }

    /**
     * Метод выявляющий победителя
     */
    public Cell getWinner() {
        int blackCount = 0;
        int whiteCount = 0;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (get(row, col) == Cell.BLACK) {
                    blackCount++;
                } else if (get(row, col) == Cell.WHITE) {
                    whiteCount++;
                }
            }
        }

        if (blackCount > whiteCount) {
            return Cell.BLACK;
        } else if (whiteCount > blackCount) {
            return Cell.WHITE;
        } else {
            return Cell.EMPTY;
        }
    }

    /**
     * Метод превращает доску в string, без чисел ряда и колонки
     *
     * @param board - доска
     */
    public static String displayBoardOnClientWithoutNumbers(@NotNull final Board board) {
        final int size = board.getSize();

        StringBuilder boardInString = new StringBuilder();
        for (int row = 0; row < size; row++) {
            getBoardString(board, size, boardInString, row);
        }
        return boardInString.toString();
    }

    /**
     * Метод превращает доску в string с числами ряда и колонки
     *
     * @param board - доска
     */
    public static String displayBoardOnClient(@NotNull final Board board) {
        final int size = board.getSize();

        StringBuilder boardInSrting = new StringBuilder("  ");
        for (int i = 0; i < size; i++) {
            boardInSrting.append(i + 1).append(" ");
        }
        boardInSrting.append("\n");

        for (int row = 0; row < size; row++) {
            boardInSrting.append(row + 1).append(" ");
            getBoardString(board, size, boardInSrting, row);
        }
        return boardInSrting.toString();
    }

    private static void getBoardString(@NotNull final Board board, final int size, @NotNull StringBuilder boardInSrting, final int row) {
        for (int col = 0; col < size; col++) {
            final Cell cell = board.get(row, col);
            final String cellSymbol = cell == Cell.BLACK ? "B" : cell == Cell.WHITE ? "W" : "-";
            boardInSrting.append(cellSymbol).append(" ");
        }
        boardInSrting.append("\n");
    }
    /**
     * Устанавливает состояние доски на основе другого объекта Board.
     *
     * @param otherBoard Другой объект Board, состояние которого нужно установить на текущей доске.
     */
    public void setBoard(Board otherBoard) {
        if (otherBoard.getSize() != this.getSize()) {
            throw new IllegalArgumentException("Размеры досок не совпадают");
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                this.set(row, col, otherBoard.get(row, col));
            }
        }
    }
    /**
     * Возвращает копию доски с текущим состоянием.
     *
     * @return Копия доски с текущим состоянием.
     */
    public Board getBCopy() {
        Board copy = new Board();
        copy.setBoard(this);
        return copy;
    }
    /**
     * Получить количество посещений ячейки на доске по указанным координатам.
     *
     * @param row Строка на доске.
     * @param col Столбец на доске.
     * @return Количество посещений ячейки.
     */
    public int getVisitCount(int row, int col) {
        return visitCounts[row][col];
    }

    /**
     * Получить количество действий (действий агента или игроков) в ячейке на доске по указанным координатам.
     *
     * @param row Строка на доске.
     * @param col Столбец на доске.
     * @return Количество действий в ячейке.
     */
    public int getActionCount(int row, int col) {
        return actionCounts[row][col];
    }
    /**
     * Подсчитывает количество ячеек на доске, которые содержат указанный тип клетки (черные, белые или пустые).
     *
     * @param cell Тип клетки, для которой необходимо подсчитать количество.
     * @return Количество клеток на доске с указанным типом.
     */
    public int countCell(Cell cell) {
        int count = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == cell) {
                    count++;
                }
            }
        }
        return count;
    }
}



