package clientresponse;


import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос старта игры
 */
public class StartGameResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "STARTGAME";
    /**
     * Статус
     */
    public final String status;
    /**
     * Сообщение
     */
    public final String message;

    public StartGameResponse(@NotNull final String status, @NotNull final String message) {
        this.message = message;
        this.status = status;
    }
}