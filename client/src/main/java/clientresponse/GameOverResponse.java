package clientresponse;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос конца игры
 */
public class GameOverResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "GAMEOVER";
    /**
     * Статус
     */
    public final String status;
    /**
     * Сообщение
     */
    public final String message;

    public GameOverResponse(@NotNull final String status, @NotNull final String message) {
        this.status = status;
        this.message = message;
    }
}
