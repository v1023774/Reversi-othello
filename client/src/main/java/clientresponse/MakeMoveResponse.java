package clientresponse;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос хода
 */
public class MakeMoveResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "MAKEMOVE";
    /**
     * Статус
     */
    public final String status;
    /**
     * Сообщение
     */
    public final String message;

    public MakeMoveResponse(@NotNull final String status, @NotNull final String message) {
        this.message = message;
        this.status = status;
    }
}
