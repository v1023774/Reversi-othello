package client;

import clientrequest.AuthorizationRequest;
import clientrequest.RegistrationRequest;
import clientrequest.ViewCreatedRoomsRequest;
import com.google.gson.Gson;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    public final Socket socket;
    public final BufferedReader bufferedReader;
    public final BufferedWriter bufferedWriter;
    public final Gson gson;
    public int roomId = 0;
    int countGame = 0;
    int winnerW = 0;
    int winnerB = 0;

    public Client(@NotNull final String host, final int port) throws IOException {
        socket = new Socket(host, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        gson = new Gson();
    }

    public void close() throws IOException {
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        final String host;
        final int port;
        final String player;
        final Properties appProps = new Properties();
        final File file = new File("client/file.properties");
        try (FileInputStream propertiesInput = new FileInputStream(file)) {
            appProps.load(propertiesInput);
            host = appProps.getProperty("host");
            port = Integer.parseInt(appProps.getProperty("port"));
            player = appProps.getProperty("player");
        } catch (IOException e) {
            logger.log(Level.ERROR, "Cannot read from file.properties");
            throw e;
        }
        Client client = new Client(host, port);
        try {
            switch (player) {
                case "bot" -> {
                    System.out.println("enter bot name");
                    final Scanner scanner = new Scanner(System.in);
                    final String botName = scanner.nextLine();
                    final RegistrationRequest registrationRequest = new RegistrationRequest(botName);
                    SendMethods.sendRequest(registrationRequest, client);
                    final AuthorizationRequest authorizationRequest = new AuthorizationRequest(botName);
                    SendMethods.sendRequest(authorizationRequest, client);
                    final ViewCreatedRoomsRequest viewCreatedRoomsRequest = new ViewCreatedRoomsRequest(1);
                    SendMethods.sendRequest(viewCreatedRoomsRequest, client);
                    client.bufferedReader.readLine();
                    client.bufferedReader.readLine();
                    final String line = client.bufferedReader.readLine();
                    if (line != null) {
                        GetResponsesMethodsBot.viewOnInComeMessageBot(client, line);
                        if (line.contains("GAMEOVER")) {
                            SendMethods.sendRequest(viewCreatedRoomsRequest, client);
                        }
                    }
                    GetResponsesMethodsBot.getMessageBot(client);
                    SendMethods.sendMessage(client);
                    client.close();
                }
                case "human" -> {
                    GetResponsesMethodsHuman.getMessageHuman(client);
                    SendMethods.sendMessage(client);
                    client.close();
                }
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, "Работа клиента была прервана");
            throw e;
        }
    }
}


