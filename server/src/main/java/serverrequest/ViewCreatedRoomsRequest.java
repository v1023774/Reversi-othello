package serverrequest;

/**
 * Класс запроса на просмотр созданной комнаты
 */
public class ViewCreatedRoomsRequest implements Request {
    /**
     * Название запроса
     */
    public final String command = "VIEWROOMS";
    /**
     * Id комнаты
     */
    public final int roomId;

    public ViewCreatedRoomsRequest(final int roomId) {
        this.roomId = roomId;
    }
}
