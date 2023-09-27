package logic;

/**
 * Класс клетки, и всей связанной с ней логикой.
 */
public enum Cell {
    BLACK("b"), WHITE("w"), EMPTY(" ");

    private final String string;

    Cell(String name) {
        this.string = name;
    }

    /**
     * метод возвращающий клетку противоположного цвета.
     */
    public Cell reverse() {
        if (this == BLACK) {
            return Cell.WHITE;
        } else return Cell.BLACK;
    }

    @Override
    public String toString() {
        return string;
    }

}