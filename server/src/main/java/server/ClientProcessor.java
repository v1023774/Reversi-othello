package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import serverresponses.Response;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

class ClientProcessor extends Thread {
    private static final Logger logger = LogManager.getLogger(ClientProcessor.class);
    private final Map commands;
    public final BufferedReader socketBufferedReader;
    public final BufferedWriter socketBufferedWriter;
    public final UUID uuid;

    /**
     * @param socket - сокет
     * @param commands - список команд
     */
    ClientProcessor(@NotNull final Socket socket, @NotNull final List<Command> commands) throws IOException {
        this.commands = commands.stream().collect(Collectors.toMap(Command::getName, Function.identity()));
        this.uuid = UUID.randomUUID();
        this.socketBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socketBufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                final String line = socketBufferedReader.readLine();
                if (line == null) {
                    return;
                }
                System.out.println(line);
                final JsonObject request = JsonParser.parseString(line).getAsJsonObject();
                final String commandName = request.get("command").getAsString().toUpperCase();
                final Command command = (Command) commands.get(commandName);
                sendReply(command.process(request, uuid));
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
            Server.onlineUsers.remove(uuid);
        }
    }

    /**
     * Отправляет на клиент реплай в виде json строки.
     *
     * @param response - строка.
     */
    synchronized void sendReply(@NotNull final Response response) {
        try {
            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();
            final String jsonRequest = gson.toJson(response);
            socketBufferedWriter.write(jsonRequest);
            socketBufferedWriter.newLine();
            socketBufferedWriter.flush();
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
            throw new IllegalStateException(e);
        }

    }

}
