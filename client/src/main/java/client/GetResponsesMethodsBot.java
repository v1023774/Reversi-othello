package client;

import clientrequest.*;
import clientresponse.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import logic.AIBotIlya;
import logic.Cell;
import logic.Move;
import logic.Player;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import parsing.BoardParser;

import java.io.IOException;

import static client.GetResponsesMethodsHuman.getResponse;

public class GetResponsesMethodsBot {
    private static final Logger logger = LogManager.getLogger(GetResponsesMethodsBot.class);

    private GetResponsesMethodsBot() {
    }

    /**
     * Метод слушает все входящие сообщения от сервера, пока сокет не разорвал соединение с сервером
     *
     * @param client - клиент
     */
    public static void getMessageBot(@NotNull final Client client) {
        new Thread(() -> {
            while (client.socket.isConnected()) {
                try {
                    final String line = client.bufferedReader.readLine();
                    if (line != null) {
                        viewOnInComeMessageBot(client, line);
                    }
                } catch (IOException e) {
                    logger.log(Level.ERROR, "Обрыв канала чтения");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * Метод обрабатывает все входящие сообщения, в режиме клиента для бота
     *
     * @param client - клиент
     * @param input - строка, котор
     */
    public static void viewOnInComeMessageBot(@NotNull final Client client, @NotNull final String input) throws IOException {

        final JsonObject request = JsonParser.parseString(input).getAsJsonObject();
        final String commandName = request.get("command").getAsString().toUpperCase();

        switch (commandName) {

            case "REGISTRATION" -> commandRegistrationBot(client, input);

            case "AUTHORIZATION" -> commandAuthorizationBot(client, input);

            case "VIEWROOMS" -> commandViewRoomsBot(client, input);

            case "CREATEROOM" -> commandCreateRoomBot(client, input);

            case "CONNECTTOROOM" -> commandConnectToRoomBot(client, input);

            case "WHEREICANGORESPONSE" -> commandWhereICanGoGameBot(client, input);

            case "LEAVEROOM" -> commandLeaveRoomBot(client, input);

            case "GAMEOVER" -> commandGameOverBot(client, input);

            case "MAKEMOVE" -> commandMakeMoveBot(client, input);

            case "STARTGAME" -> commandStartGameBot(client, input);

            case "EXIT" -> commandExitBot(client);

            default -> commandDefaultBot(commandName);
        }
    }

    private static void commandRegistrationBot(@NotNull final Client client, @NotNull final String input) {
        final RegistrationResponse registrationResponse = getResponse(RegistrationResponse.class, input, client);
        logger.log(Level.INFO, () -> "Registration response: " + registrationResponse.message);
    }

    private static void commandAuthorizationBot(@NotNull final Client client, @NotNull final String input) {
        final AuthorizationResponse authorizationResponse = getResponse(AuthorizationResponse.class, input, client);
        logger.log(Level.INFO, () -> "Authorization response: " + authorizationResponse.message);
    }

    private static void commandViewRoomsBot(@NotNull final Client client, @NotNull final String input) {
        final ViewCreatedRoomsResponse viewCreatedRoomsResponse = getResponse(ViewCreatedRoomsResponse.class, input, client);
        if (viewCreatedRoomsResponse.status.equals("fail")) {
            final CreateRoomRequest createRoomRequest = new CreateRoomRequest();
            SendMethods.sendRequest(createRoomRequest, client);
            logger.log(Level.INFO, () -> "Room was created");
        } else {
            final ConnectToRoomRequest connectToRoomRequest = new ConnectToRoomRequest(1);
            SendMethods.sendRequest(connectToRoomRequest, client);
            logger.log(Level.INFO, () -> "Connected to room");
        }
    }

    private static void commandCreateRoomBot(@NotNull final Client client, @NotNull final String input) {
        final CreateRoomResponse createRoomResponse = getResponse(CreateRoomResponse.class, input, client);
        if (createRoomResponse.status.equals("fail")) {
            logger.log(Level.INFO, () -> "Create room response: " + createRoomResponse.message);
        }
        client.roomId = createRoomResponse.roomId;
        logger.log(Level.INFO, () -> "Create room response: " + createRoomResponse.message + ", Room ID: " + createRoomResponse.roomId);
    }

    private static void commandConnectToRoomBot(@NotNull final Client client, @NotNull final String input) {
        final ConnectToRoomResponse connectToRoomResponse = getResponse(ConnectToRoomResponse.class, input, client);
        if (connectToRoomResponse.message.equals("White player connected")) {
            final StartGameRequest startGameRequest = new StartGameRequest(client.roomId, false);
            SendMethods.sendRequest(startGameRequest, client);
            logger.log(Level.INFO, () -> "Room was created");
        }
        logger.log(Level.INFO, () -> "Connect to room response: " + connectToRoomResponse.message);
    }

    private static void commandGameOverBot(@NotNull final Client client, @NotNull final String input) {
        final GameOverResponse gameoverResponse = getResponse(GameOverResponse.class, input, client);
        System.out.println("Game over response " + gameoverResponse.message);
    }

    private static void commandLeaveRoomBot(@NotNull final Client client, @NotNull final String input) {
        final LeaveRoomResponse leaveRoomResponse = getResponse(LeaveRoomResponse.class, input, client);
        logger.log(Level.INFO, () -> "Leave room response: " + leaveRoomResponse.message);
    }

    private static void commandStartGameBot(@NotNull final Client client, @NotNull final String input) {
        final StartGameResponse startGameResponse = getResponse(StartGameResponse.class, input, client);
        logger.log(Level.INFO, () -> "StartGame response " + startGameResponse.message);
    }

    private static void commandWhereICanGoGameBot(@NotNull final Client client, @NotNull final String input) {
        final WhereIcanGoResponse whereIcanGoResponse = getResponse(WhereIcanGoResponse.class, input, client);
        if (whereIcanGoResponse.color.equals("black")) {
            final Player botPlayer = new AIBotIlya(Cell.BLACK, 8);
            final Move move = botPlayer.makeMove(BoardParser.parse(whereIcanGoResponse.boardStringWON, 'B', 'W', '-'));
            final MakeMoveRequest makeMoveRequest = new MakeMoveRequest(move.row + 1, move.col + 1);
            SendMethods.sendRequest(makeMoveRequest, client);
        } else {
            final Player botPlayer = new AIBotIlya(Cell.WHITE, 8);
            final Move move = botPlayer.makeMove(BoardParser.parse(whereIcanGoResponse.boardStringWON, 'B', 'W', '-'));
            final MakeMoveRequest makeMoveRequest = new MakeMoveRequest(move.row + 1, move.col + 1);
            SendMethods.sendRequest(makeMoveRequest, client);
        }
    }

    private static void commandMakeMoveBot(@NotNull final Client client, @NotNull final String input) {
        final MakeMoveResponse makeMoveResponse = getResponse(MakeMoveResponse.class, input, client);
        if (makeMoveResponse.status.equals("fail")) {
            final WhereICanGoRequest whereICanGoRequest = new WhereICanGoRequest();
            SendMethods.sendRequest(whereICanGoRequest, client);
        }
    }

    private static void commandExitBot(@NotNull final Client client) throws IOException {
        client.close();
        System.exit(0);
    }

    private static void commandDefaultBot(@NotNull final String commandName) {
        logger.log(Level.INFO, () -> "Unknown command: " + commandName);
    }
}
