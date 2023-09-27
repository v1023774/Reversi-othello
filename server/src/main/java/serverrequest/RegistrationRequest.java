package serverrequest;
/**
 * Класс запроса на регистрацию
 */
public class RegistrationRequest implements Request {
    /**
     * Название запроса
     */
    protected final String command = "REGISTRATION";
    /**
     * имя игрока
     */
    public String nickname;

    public RegistrationRequest(String nickname) {
        this.nickname = nickname;
    }
}
