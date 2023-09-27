package guiClient;

import client.Client;
import gui.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

import static gui.MainGameWindow.musicPlayer;
import static guiClient.GetResponsesMethodsHuman.getMessageHuman;
import static guiClient.SendMethods.sendMessage;

public class ClientGui extends Client {

    private static final Logger logger = LogManager.getLogger(ClientGui.class);

    public RegAndAuth regAndAuth;
    public CreateAndConnect createAndConnect;
    public RoomGui roomGui;

    public ClientGui(@NotNull String host, int port) throws IOException {
        super(host, port);
    }

    public static void main(String[] args) throws IOException {
        String host;
        int port;
        String player;
        Properties appProps = new Properties();
        File file = new File("ui/file.properties");

        try (FileInputStream propertiesInput = new FileInputStream(file)) {
            appProps.load(propertiesInput);
            host = appProps.getProperty("host");
            port = Integer.parseInt(appProps.getProperty("port"));
            player = appProps.getProperty("player");
        } catch (IOException e) {
            logger.log(Level.ERROR, "Cannot read from file.properties");
            throw e;
        }

        ClientGui clientGui = new ClientGui(host, port);
        SwingUtilities.invokeLater(() -> {
            musicPlayer = new MusicPlayer("resources/retro.wav");
                musicPlayer.play();
            new MainStartWindow(clientGui).setVisible(true);
        });

        try {
            switch (player) {
                case "human" -> {
                    getMessageHuman(clientGui);
                    sendMessage(clientGui);
                    clientGui.close();
                }
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, "Работа клиента была прервана");
            throw e;
        }
    }

}