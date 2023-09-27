package serverrequest;

/**
 * Класс запроса на начало игры
 */
public class StartGameRequest implements Request {
    /**
     * Название запроса
     */
    public final String command = "STARTGAME";
    /**
     * id комнаты
     */
    public final int roomId;
    /**
     * Флаг запуска игры (консолькое гуи/текстовое)
     */
    public final boolean guiFlag;

    public StartGameRequest(final int roomId, final boolean guiFlag) {
        this.roomId = roomId;
        this.guiFlag = guiFlag;
    }
}
