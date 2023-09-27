package guiClient;

import clientresponse.*;
import clientresponse.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

public class GetResponsesMethodsHuman {

    private static final Logger logger = LogManager.getLogger(GetResponsesMethodsHuman.class);

    private GetResponsesMethodsHuman() {
    }

    static <T extends Response> T getResponse(@NotNull final Class<T> responseType, @NotNull final String jsonResponse, @NotNull final ClientGui clientGui) {
        return clientGui.gson.fromJson(jsonResponse, responseType);
    }

    static void getMessageHuman(@NotNull final ClientGui clientGui) {
        new Thread(() -> {
            while (clientGui.socket.isConnected()) {
                try {
                    final String line = clientGui.bufferedReader.readLine();
                    if (line != null) {
                        GetResponsesMethodsHuman.viewOnInComeMessageHuman(clientGui, line);
                    }
                } catch (IOException e) {
                    logger.log(Level.ERROR, "Reader Error");
                }
            }
        }).start();
    }

    static void viewOnInComeMessageHuman(@NotNull final ClientGui clientGui, @NotNull final String input) throws IOException {
        final JsonObject request = JsonParser.parseString(input).getAsJsonObject();
        final String commandName = request.get("command").getAsString().toUpperCase();

        switch (commandName) {
            case "REGISTRATION" -> viewRegistration(clientGui, input);

            case "AUTHORIZATION" -> viewAuthorization(clientGui, input);

            case "CREATEROOM" -> viewCreateRoom(clientGui, input);

            case "CONNECTTOROOM" -> viewConnectToRoom(clientGui, input);

            case "LEAVEROOM" -> viewLeaveRoom(clientGui, input);

            case "WHEREICANGORESPONSE" -> viewWhereICanGo(clientGui, input);

            case "GAMEOVER" -> viewGameOver(clientGui, input);

            case "MAKEMOVE" -> viewMakeMove(clientGui, input);

            case "STARTGAME" -> viewStartGame(clientGui, input);

            case "EXIT" -> viewExit(clientGui);

            case "SURRENDER" -> viewSurrender(clientGui, input);

            default -> viewDefault(commandName);
        }
    }

    private static void viewRegistration(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final RegistrationResponse registrationResponse = getResponse(RegistrationResponse.class, input, clientGui);
        System.out.println("Registration response: " + registrationResponse.message);

        if (registrationResponse.status.equals("fail")) {
            clientGui.regAndAuth.registrationLabelError.setVisible(true);
            clientGui.regAndAuth.registrationLabelError.setText(registrationResponse.message);
        } else {
            JOptionPane.showMessageDialog(clientGui.regAndAuth, "Successfully registered");
        }
    }

    private static void viewAuthorization(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final AuthorizationResponse authorizationResponse = getResponse(AuthorizationResponse.class, input, clientGui);
        System.out.println("Authorization response: " + authorizationResponse.message);

        if (authorizationResponse.status.equals("fail")) {
            clientGui.regAndAuth.authorizationLabelError.setVisible(true);
            clientGui.regAndAuth.authorizationLabelError.setText(authorizationResponse.message);
        }
    }

    private static void viewCreateRoom(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final CreateRoomResponse createRoomResponse = getResponse(CreateRoomResponse.class, input, clientGui);
        if (createRoomResponse.status.equals("fail")) {
            System.out.println("Create room response: " + createRoomResponse.message);
        }
        clientGui.roomId = createRoomResponse.roomId;
        System.out.println("Create room response: " + createRoomResponse.message + ", Room ID: " + createRoomResponse.roomId);

//        clientGui.roomGui.roomId.setText(String.valueOf(createRoomResponse.roomId));
    }

    private static void viewConnectToRoom(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final ConnectToRoomResponse connectToRoomResponse = getResponse(ConnectToRoomResponse.class, input, clientGui);
        System.out.println("Connect to room response: " + connectToRoomResponse.message);

        if (connectToRoomResponse.status.equals("fail")) {
            clientGui.createAndConnect.ConnectLabelError.setVisible(true);
            clientGui.createAndConnect.ConnectLabelError.setText(connectToRoomResponse.message);
        }
    }

    private static void viewLeaveRoom(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final LeaveRoomResponse leaveRoomResponse = getResponse(LeaveRoomResponse.class, input, clientGui);
        System.out.println("Leave room response: " + leaveRoomResponse.message);
    }

    private static void viewWhereICanGo(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final WhereIcanGoResponse whereIcanGoResponse = getResponse(WhereIcanGoResponse.class, input, clientGui);
        System.out.println("Your available moves " + whereIcanGoResponse.availableMoves);
        System.out.println(whereIcanGoResponse.color + " " + whereIcanGoResponse.boardStringWON + " " + whereIcanGoResponse.availableMoves);
        clientGui.roomGui.playerColor = whereIcanGoResponse.color;
        clientGui.roomGui.updateBoard(whereIcanGoResponse.boardStringWON);
        clientGui.roomGui.updateAvailableMoves(whereIcanGoResponse.availableMoves);
    }


    private static void viewGameOver(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final GameOverResponse gameoverResponse = getResponse(GameOverResponse.class, input, clientGui);
        System.out.println("Game over response " + gameoverResponse.message);
        JOptionPane.showMessageDialog(clientGui.roomGui, gameoverResponse.message);
        clientGui.roomGui.StartGameLabel.setEnabled(true);
    }

    private static void viewMakeMove(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final MakeMoveResponse makeMoveResponse = getResponse(MakeMoveResponse.class, input, clientGui);
        System.out.println("MakeMove response " + makeMoveResponse.message);
    }

    private static void viewStartGame(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final StartGameResponse startGameResponse = getResponse(StartGameResponse.class, input, clientGui);
        System.out.println("StartGame response " + startGameResponse.message);
    }

    private static void viewSurrender(@NotNull final ClientGui clientGui, @NotNull final String input) {
        final SurrenderResponse surrenderResponse = getResponse(SurrenderResponse.class, input, clientGui);
        JOptionPane.showMessageDialog(clientGui.roomGui, surrenderResponse.message);
        System.out.println(surrenderResponse.message);
    }


    private static void viewExit(@NotNull final ClientGui clientGui) throws IOException {
        clientGui.close();
        System.exit(0);
    }

    private static void viewDefault(@NotNull final String commandName) {
        System.out.println("Unknown command: " + commandName);
    }
}
