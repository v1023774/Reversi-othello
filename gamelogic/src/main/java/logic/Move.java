package logic;

import java.util.Objects;

/**
 * Класс Move представляет ход в игре. Каждый ход характеризуется строкой и столбцом на доске,
 * куда игрок помещает свою фишку.
 */
public final class Move {
    public final int row;
    public final int col;

    /**
     * Конструктор создает объект хода с указанными координатами строки и столбца.
     *
     * @param row строка на доске.
     * @param col столбец на доске.
     */
    public Move(final int row, final int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Метод переопределяет метод equals, чтобы сравнивать объекты ходов на равенство.
     *
     * @param o объект, с которым происходит сравнение.
     * @return true, если объекты равны (имеют одинаковые координаты строки и столбца), иначе false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return row == move.row && col == move.col;
    }

    /**
     * Метод переопределяет метод hashCode, чтобы объекты ходов, которые равны по equals,
     * имели одинаковый хэш-код.
     *
     * @return хэш-код объекта хода.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
    /**
     * Получить строку хода.
     *
     * @return Строка хода.
     */
    public int getRow() {
        return row;
    }
    /**
     * Получить столбец хода.
     *
     * @return Столбец хода.
     */
    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return (row + 1) + " " + (col + 1) + " ";
    }
}
