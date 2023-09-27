package clientresponse;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос сдачи
 */
public class SurrenderResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "SURRENDER";
    /**
     * Статус
     */
    public final String status;
    /**
     * Сообщение
     */
    public final String message;

    public SurrenderResponse(@NotNull final String status, @NotNull final String message) {
        this.status = status;
        this.message = message;
    }
}
