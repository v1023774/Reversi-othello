package clientresponse;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос выхода из команты
 */
public class LeaveRoomResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "LEAVEROOM";
    /**
     * Сообщение
     */
    public final String message;
    /**
     * Статус
     */
    public final String status;

    public LeaveRoomResponse(@NotNull final String message, @NotNull final String status) {
        this.message = message;
        this.status = status;
    }
}
