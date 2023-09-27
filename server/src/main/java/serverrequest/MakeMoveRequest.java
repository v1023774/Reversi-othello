package serverrequest;
/**
 * Класс запроса на отправку хода
 */
public class MakeMoveRequest implements Request {
    /**
     * Название запроса
     */
    public final String command = "MAKEMOVE";
    /**
     * координата по строке
     */
    public final int row;
    /**
     * координата по столбцу
     */
    public final int col;

    public MakeMoveRequest(final int row, final int col) {
        this.row = row;
        this.col = col;
    }
}
