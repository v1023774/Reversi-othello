package clientresponse;


import org.jetbrains.annotations.NotNull;
/**
 * Класс ответа на запрос просмотра существующй комнаты
 */
public class ViewCreatedRoomsResponse implements Response {
    /**
     * Название ответа
     */
    public final String command = "VIEWROOMS";

    /**
     * Id комнаты
     */
    public final int roomId;

    /**
     * Статус
     */
    public final String status;
    /**
     * Сообщение
     */
    public final String message;

    public ViewCreatedRoomsResponse(@NotNull final String status, @NotNull final String message, int roomId) {
        this.message = message;
        this.roomId = roomId;
        this.status = status;
    }
}
