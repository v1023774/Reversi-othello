package client;

import clientresponse.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GetResponsesMethodsHuman {

    private static final Logger logger = LogManager.getLogger(GetResponsesMethodsHuman.class);

    private GetResponsesMethodsHuman() {
    }

    static <T extends Response> T getResponse(@NotNull final Class<T> responseType, @NotNull final String jsonResponse, @NotNull final Client client) {
        return client.gson.fromJson(jsonResponse, responseType);
    }
    /**
     * Метод слушает все входящие сообщения, пока сокет не разорвал соединение с сервером
     *
     * @param client - клиент
     */
    static void getMessageHuman(@NotNull final Client client) {
        new Thread(() -> {
            while (client.socket.isConnected()) {
                try {
                    final String line = client.bufferedReader.readLine();
                    if (line != null) {
                        GetResponsesMethodsHuman.viewOnInComeMessageHuman(client, line);
                    }
                } catch (IOException e) {
                    logger.log(Level.ERROR, "Reader Error");
                }
            }
        }).start();
    }
    /**
     * Метод обрабатывает все входящие сообщения, в режиме клиента для человека
     *
     * @param client - клиент
     * @param input - строка, котор
     */
    static void viewOnInComeMessageHuman(@NotNull final Client client, @NotNull final String input) throws IOException {
        final JsonObject request = JsonParser.parseString(input).getAsJsonObject();
        final String commandName = request.get("command").getAsString().toUpperCase();

        switch (commandName) {
            case "REGISTRATION" -> viewRegistration(client, input);

            case "AUTHORIZATION" -> viewAuthorization(client, input);

            case "CREATEROOM" -> viewCreateRoom(client, input);

            case "CONNECTTOROOM" -> viewConnectToRoom(client, input);

            case "LEAVEROOM" -> viewLeaveRoom(client, input);

            case "WHEREICANGORESPONSE" -> viewWhereICanGo(client, input);

            case "GAMEOVER" -> viewGameOver(client, input);

            case "MAKEMOVE" -> viewMakeMove(client, input);

            case "STARTGAME" -> viewStartGame(client, input);

            case "EXIT" -> viewExit(client);

            case "SURRENDER" -> viewSurrender(client, input);

            default -> viewDefault(commandName);
        }
    }
    private static void viewRegistration(@NotNull final Client client, @NotNull final String input) {
        final RegistrationResponse registrationResponse = getResponse(RegistrationResponse.class, input, client);
        System.out.println("Registration response: " + registrationResponse.message);

    }
    private static void viewAuthorization(@NotNull final Client client, @NotNull final String input) {
        final AuthorizationResponse authorizationResponse = getResponse(AuthorizationResponse.class, input, client);
        System.out.println("Authorization response: " + authorizationResponse.message);
    }
    private static void viewCreateRoom(@NotNull final Client client, @NotNull final String input) {
        final CreateRoomResponse createRoomResponse = getResponse(CreateRoomResponse.class, input, client);
        if (createRoomResponse.status.equals("fail")) {
            System.out.println("Create room response: " + createRoomResponse.message);
        }
        client.roomId = createRoomResponse.roomId;
        System.out.println("Create room response: " + createRoomResponse.message + ", Room ID: " + createRoomResponse.roomId);

    }
    private static void viewConnectToRoom(@NotNull final Client client, @NotNull final String input) {
        final ConnectToRoomResponse connectToRoomResponse = getResponse(ConnectToRoomResponse.class, input, client);
        System.out.println("Connect to room response: " + connectToRoomResponse.message);
    }
    private static void viewLeaveRoom(@NotNull final Client client, @NotNull final String input) {
        final LeaveRoomResponse leaveRoomResponse = getResponse(LeaveRoomResponse.class, input, client);
        System.out.println("Leave room response: " + leaveRoomResponse.message);
    }
    private static void viewWhereICanGo(@NotNull final Client client, @NotNull final String input) {
        final WhereIcanGoResponse whereIcanGoResponse = getResponse(WhereIcanGoResponse.class, input, client);
        System.out.println(whereIcanGoResponse.board);
        System.out.println( "Your available moves " + whereIcanGoResponse.availableMoves);
    }
    private static void viewGameOver(@NotNull final Client client, @NotNull final String input) {
        final GameOverResponse gameoverResponse = getResponse(GameOverResponse.class, input, client);
        System.out.println("Game over response " + gameoverResponse.message);
    }
    private static void viewMakeMove(@NotNull final Client client, @NotNull final String input) {
        final MakeMoveResponse makeMoveResponse = getResponse(MakeMoveResponse.class, input, client);
        System.out.println("MakeMove response " + makeMoveResponse.message);
    }
    private static void viewStartGame(@NotNull final Client client, @NotNull final String input) {
        final StartGameResponse startGameResponse = getResponse(StartGameResponse.class, input, client);
        System.out.println( "StartGame response " + startGameResponse.message);
    }
    private static void viewSurrender(@NotNull final Client client, @NotNull final String input) {
        final SurrenderResponse surrenderResponse = getResponse(SurrenderResponse.class, input, client);
        System.out.println(surrenderResponse.message);
    }
    private static void viewExit(@NotNull final Client client) throws IOException {
        client.close();
        System.exit(0);
    }
    private static void viewDefault(@NotNull final String commandName) {
        System.out.println("Unknown command: " + commandName);
    }
}
