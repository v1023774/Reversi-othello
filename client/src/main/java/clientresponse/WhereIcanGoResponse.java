package clientresponse;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос просмотра доступных ходов
 */
public class WhereIcanGoResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "WHEREICANGORESPONSE";
    /**
     * возможные
     */
    public final String availableMoves;
    /**
     * возможные ходы
     */
    public final String board;
    /**
     * Цвет игрока
     */
    public final String color;
    /**
     * доска без вспомогательных цифр по бокам вверху
     */
    public final String boardStringWON;

    public WhereIcanGoResponse(@NotNull final String availableMoves, @NotNull final String board,
                               @NotNull final String boardStringWON, @NotNull final String color) {
        this.availableMoves = availableMoves;
        this.color = color;
        this.board = board;
        this.boardStringWON = boardStringWON;
    }
}
