package clientresponse;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос авторизации
 */
public class AuthorizationResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "AUTHORIZATION";
    /**
     * Сообщение
     */
    public final String message;
    /**
     * Статус овета
     */
    public final String status;

    public AuthorizationResponse(@NotNull final String message, @NotNull final String status) {
        this.message = message;
        this.status = status;
    }
}
