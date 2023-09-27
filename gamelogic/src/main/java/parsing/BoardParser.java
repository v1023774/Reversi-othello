package parsing;

import logic.Board;
import logic.Cell;
import org.jetbrains.annotations.NotNull;

public class BoardParser {

    private BoardParser() {
    }

    /**
     * Возвращает объект типа Board, конвертируя String boardAsString.
     *
     * @param boardAsString - строка, содержащая поле и разсоположенные на ней фишки, вида
     *                      "_ _ _ _ _ _ _ _ \n" +
     *                      "_ _ _ _ _ _ _ _ \n" +
     *                      "_ _ _ _ _ _ _ _ \n" +
     *                      "_ _ _ - + _ _ _ \n" +
     *                      "_ _ _ + - _ _ _ \n" +
     *                      "_ _ _ _ _ _ _ _ \n" +
     *                      "_ _ _ _ _ _ _ _ \n" +
     *                      "_ _ _ _ _ _ _ _ \n"
     * @param black - черная фишка, отображаемая на доске
     * @param white - белая фишка, отображаемая на доске
     * @param empty - пустая фишка, отображаемая на доске
     * @return возвращает доску.
     */
    public static Board parse(@NotNull final String boardAsString, final char black, final char white, final char empty) {
        final Board board = new Board();

        if (boardAsString.length() < (board.getSize() * 2 + 1) * board.getSize() - 2) {
            throw new IllegalArgumentException();
        }

        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize() * 2; col += 2) {
                char currentCell = boardAsString.charAt((board.getSize() * 2 + 1) * row + col);
                if (currentCell == black) {
                    board.set(row, col / 2, Cell.BLACK);
                } else if (currentCell == white) {
                    board.set(row, col / 2, Cell.WHITE);
                } else if (currentCell == empty) {
                    board.set(row, col / 2, Cell.EMPTY);
                }
            }
        }
        return board;
    }
}

