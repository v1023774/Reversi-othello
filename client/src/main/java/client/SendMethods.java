package client;

import clientrequest.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Scanner;

public class SendMethods {
    private static final Logger logger = LogManager.getLogger(SendMethods.class);

    private SendMethods() {
    }

    /**
     * Метод отправляет любое сообещение на сервер
     *
     * @param client - клиент
     */
    public static void sendMessage(@NotNull final Client client) throws IOException {
        final Scanner scanner = new Scanner(System.in);
        while (client.socket.isConnected()) {
            final String msg = scanner.nextLine();
            SendMethods.createJsonAndSendCommand(client, msg);
        }
    }
    /**
     * Метод отправляет любой запрос на сервер
     *
     * @param client - клиент
     */
    static void sendRequest(@NotNull final Request request, @NotNull final Client client) {
        final String jsonRequest = client.gson.toJson(request);
        try {
            client.bufferedWriter.write(jsonRequest);
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Usage: register <nickname>");
        }
    }

    /**
     * Метод определяет, что за запрос ввел клиент, сериализует его, и отправляет на сервер
     *
     * @param client - клиент
     * @param input - запрос
     */
    public static void createJsonAndSendCommand(@NotNull final Client client, @NotNull final String input) throws IOException {
        final String[] commandParts = input.split("\\s+");
        final String command = commandParts[0];

        switch (command) {
            case "REGISTRATION" -> commandRegistration(client, commandParts);

            case "AUTHORIZATION" -> commandAuthorization(client, commandParts);

            case "CREATEROOM" -> commandCreateRoom(client);

            case "CONNECTTOROOM" -> commandConnectToRoom(client, commandParts);

            case "LEAVEROOM" -> commandLeaveRoom(client);

            case "STARTGAME" -> commandStartGame(client);

            case "MAKEMOVE" -> commandMakeMove(client, commandParts);

            case "SURRENDER" -> commandSurrender(client);

            case "EXIT" -> commandExit(client);

            default -> commandDefault(command);
        }
    }

    private static void commandRegistration(@NotNull final Client client, @NotNull final String[] commandParts) {
        if (commandParts.length > 1) {
            final String nickname = commandParts[1];
            final RegistrationRequest registrationRequest = new RegistrationRequest(nickname);
            sendRequest(registrationRequest, client);
        } else {
            logger.log(Level.INFO, "Usage: register <nickname>");
        }
    }

    private static void commandAuthorization(@NotNull final Client client, @NotNull final String[] commandParts) {
        if (commandParts.length > 1) {
            final String nickname = commandParts[1];
            final AuthorizationRequest authorizationRequest = new AuthorizationRequest(nickname);
            sendRequest(authorizationRequest, client);
        } else {
            logger.log(Level.INFO, "Usage: login <nickname>");
        }
    }

    private static void commandCreateRoom(@NotNull final Client client) {
        if (client.roomId != 0) {
            logger.log(Level.INFO, "You can not create another room because you are already in room.");
        } else {
            final CreateRoomRequest createRoomRequest = new CreateRoomRequest();
            sendRequest(createRoomRequest, client);
        }
    }

    private static void commandConnectToRoom(@NotNull final Client client, @NotNull final String[] commandParts) {
        if (client.roomId != 0) {
            logger.log(Level.INFO, "You can not connect to another room because you are already in room.");
        }
        if (commandParts.length > 1) {
            final int roomId = Integer.parseInt(commandParts[1]);
            final ConnectToRoomRequest connectToRoomRequest = new ConnectToRoomRequest(roomId);
            sendRequest(connectToRoomRequest, client);
            client.roomId = roomId;
        } else {
            logger.log(Level.INFO, "Usage: connect to room <room_id>.");
        }
    }

    private static void commandLeaveRoom(@NotNull final Client client) {
        if (client.roomId == 0) {
            logger.log(Level.INFO, "You can not leave room because you are not in room.");
        } else {
            final LeaveRoomRequest leaveRoomRequest = new LeaveRoomRequest();
            sendRequest(leaveRoomRequest, client);
            client.roomId = 0;
        }
    }

    private static void commandStartGame(@NotNull final Client client) {
        if (client.roomId == 0) {
            logger.log(Level.INFO, "You can not start game because you are not in room.");
        } else {
            final StartGameRequest startGameRequest = new StartGameRequest(client.roomId, false);
            sendRequest(startGameRequest, client);
        }
    }

    private static void commandMakeMove(@NotNull final Client client, @NotNull final String[] commandParts) {
        if (commandParts.length > 2) {
            final int row = Integer.parseInt(commandParts[1]);
            final int col = Integer.parseInt(commandParts[2]);
            final MakeMoveRequest makeMoveRequest = new MakeMoveRequest(row, col);
            sendRequest(makeMoveRequest, client);
        } else {
            logger.log(Level.INFO, "Wrong coordinates.");
        }
    }

    private static void commandSurrender(@NotNull final Client client) {
        final SurrenderRequest surrenderRequest = new SurrenderRequest();
        sendRequest(surrenderRequest, client);
    }

    private static void commandExit(@NotNull final Client client) throws IOException {
        client.close();
        System.exit(0);
    }

    private static void commandDefault(@NotNull final String commandName) {
        logger.log(Level.INFO, () -> "Unknown command: " + commandName);
    }
}
