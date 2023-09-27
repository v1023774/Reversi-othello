package clientresponse;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос подключения к комнате
 */
public class ConnectToRoomResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "CONNECTTOROOM";
    /**
     * Сообщение
     */
    public final String message;
    /**
     * Статус овета
     */
    public final String status;

    public ConnectToRoomResponse(@NotNull final String status, @NotNull final String message) {
        this.status = status;
        this.message = message;
    }
}
