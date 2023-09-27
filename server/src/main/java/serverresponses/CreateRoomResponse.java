package serverresponses;

import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос создания команты
 */
public class CreateRoomResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "CREATEROOM";
    /**
     * Сообщение
     */
    public final String message;
    /**
     * Статус
     */
    public final String status;
    /**
     * Id комнаты
     */
    public final int roomId;

    public CreateRoomResponse(@NotNull final String message, @NotNull final String status, int roomId) {
        this.message = message;
        this.status = status;
        this.roomId = roomId;
    }


}
