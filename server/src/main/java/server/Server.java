package server;

import com.google.gson.JsonObject;
import database.Database;
import org.jetbrains.annotations.NotNull;
import serverresponses.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import static server.Commands.addAllCommands;

class Server {

    static final ConcurrentMap<UUID, ClientProcessor> clients = new ConcurrentHashMap<>();
    protected static final ConcurrentMap<UUID, String> onlineUsers = new ConcurrentHashMap<>();
    static final List<Room> roomList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        final int port;
        final Properties appProps = new Properties();
        final File file = new File("server/file.properties");

        try (FileInputStream propertiesInput = new FileInputStream(file)) {
            appProps.load(propertiesInput);
            port = Integer.parseInt(appProps.getProperty("port"));
        }
        final Database database = new Database();

        try (ServerSocket server = new ServerSocket(port)) {
            final List<Command> commands = new ArrayList<>();
            addAllCommands(database, commands);

            while (!server.isClosed()) {
                final Socket socket = server.accept();
                try {
                    final ClientProcessor thisClient = new ClientProcessor(socket, commands);
                    clients.put(thisClient.uuid, thisClient);
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }
}

interface Command {
    String getName();

    Response process(@NotNull final JsonObject var1, @NotNull final UUID var2);

    static Command newCommand(@NotNull final String name, @NotNull final BiFunction<JsonObject, UUID, Response> process) {
        return new Command() {
            public String getName() {
                return name;
            }

            public Response process(@NotNull final JsonObject request, @NotNull final UUID uuid) {
                return process.apply(request, uuid);
            }
        };
    }
}