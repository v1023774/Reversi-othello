package clientrequest;

/**
 * Класс запроса подключения к комнате
 */
public class ConnectToRoomRequest implements Request {
    /**
     * Название запроса
     */
    public final String command = "CONNECTTOROOM";

    /**
     * Id комнаты
     */
    public final int roomId;
    public ConnectToRoomRequest(final int roomId) {
        this.roomId = roomId;
    }
}