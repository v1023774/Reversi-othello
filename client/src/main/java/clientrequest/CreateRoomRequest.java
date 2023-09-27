package clientrequest;

/**
 * Класс запроса на создание комнаты
 */
public class CreateRoomRequest implements Request {
    /**
     * Название запроса
     */
    public final String command = "CREATEROOM";
}