package clientresponse;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос регистрации
 */
public class RegistrationResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "REGISTRATION";
    /**
     * Статус
     */
    public  String status;
    /**
     * Сообщение
     */
    public  String message;

    public RegistrationResponse(@NotNull final String status, @NotNull final String message) {
        this.status = status;
        this.message = message;
    }
    public RegistrationResponse() {}
}
