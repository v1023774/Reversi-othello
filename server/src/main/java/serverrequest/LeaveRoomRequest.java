package serverrequest;
/**
 * Класс запроса на выход с комнаты
 */
public class LeaveRoomRequest implements Request {
    /**
     * Название запроса
     */
    public final String command = "LEAVEROOM";
}