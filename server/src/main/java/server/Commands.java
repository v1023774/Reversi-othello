package server;

import com.google.gson.Gson;
import database.Database;

import gamelogging.GameLogger;
import localgame.Game;
import logic.Board;
import logic.Cell;
import logic.Move;
import org.jetbrains.annotations.NotNull;
import serverrequest.AuthorizationRequest;
import serverrequest.RegistrationRequest;
import serverresponses.*;

import java.util.List;
import java.util.Random;
import java.util.UUID;


import static localgame.Game.displayResultOnClient;
import static server.Server.*;

public class Commands {
    private Commands() {
    }

    private static final Random random = new Random();
    private static final String ROOM_ID_STRING = "roomId";
    private static final String ROOM_WITH_ID = "Room with ID ";
    private static final String YOU_ARE_NOT_LOGGED_IN = "You are not logged in";
    private static final String PLAYER_COLOR_BLACK = "black";
    private static final String PLAYER_COLOR_WHITE = "white";
    private static final String ITS_NOT_YOUR_TURN = "It's not your turn";
    private static final String GAME_STARTED = "game started";
    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";
    private static final String YOU_ARE_NOT_IN_ROOM = "You are not in room.";

    /**
     * Метод добавляет все созданные команды в спикок команд на сервере
     *
     * @param database - база данных
     * @param commands - список команд
     */
    public static void addAllCommands(@NotNull final Database database, @NotNull final List<Command> commands) {
        addRegistrationCommand(database, commands);
        addAuthorizationCommand(database, commands);
        addCreateRoomCommand(database, commands);
        addConnectToRoomCommand(database, commands);

        addViewCreatedRoomsCommand(commands);
        addLeaveRoomCommand(commands);
        addGameOverCommand(commands);
        addStartGameCommand(commands);
        addSurrenderCommand(commands);
        addMakeMoveCommand(commands);
        addWhereICanGoCommand(commands);
    }

    /**
     * Добавляет команду регистрации в список команд на сервере.
     * Сама команда позволяет регистрировать пользователей и записывать их в базу данных
     *
     * @param database - база данных
     * @param commands - список команд
     */
    private static void addRegistrationCommand(@NotNull final Database database, @NotNull final List<Command> commands) {
        commands.add(Command.newCommand("REGISTRATION", (jsonRequest, uuid) -> {
            final Gson gson = new Gson();
            final RegistrationRequest request = gson.fromJson(jsonRequest, RegistrationRequest.class);
            final String nickname = request.nickname;
            if (nickname == null || !nickname.matches("^[\\w]+$")) {
                return new RegistrationResponse(FAIL_STATUS, "incorrect username");
            }
            if (database.addPlayerOnDatabase(nickname)) {
                return new RegistrationResponse(SUCCESS_STATUS, "You was successfully registered");
            } else {
                return new RegistrationResponse(FAIL_STATUS, "User with this nickname already registered");
            }
        }));
    }

    /**
     * Добавляет команду авторизации в список команд на сервере.
     * Сама команда позволяет авторизовываться и выполнять действия в игре по сети.
     *
     * @param database - база данных
     * @param commands - список команд
     */
    private static void addAuthorizationCommand(@NotNull final Database database, @NotNull final List<Command> commands) {
        commands.add(Command.newCommand("AUTHORIZATION", (jsonRequest, uuid) -> {
            final Gson gson = new Gson();
            final AuthorizationRequest request = gson.fromJson(jsonRequest, AuthorizationRequest.class);
            final String nickname = request.nickname;
            if (database.checkRegistration(nickname)) {
                if (onlineUsers.containsValue(nickname)) {
                    return new AuthorizationResponse(FAIL_STATUS, "user with this nickname already online");
                } else {
                    onlineUsers.put(uuid, nickname);
                    return new AuthorizationResponse(SUCCESS_STATUS, "You have successfully logged in");
                }
            } else {
                return new AuthorizationResponse(FAIL_STATUS, "You are not registered");
            }
        }));
    }

    /**
     * Добавляет команду просмотра созданной комнаты по ее id.
     *
     * @param commands - список команд
     */
    private static void addViewCreatedRoomsCommand(@NotNull final List<Command> commands) {
        commands.add(Command.newCommand("VIEWROOMS", (jsonRequest, uuid) -> {
            final int roomId = jsonRequest.get(ROOM_ID_STRING).getAsInt();
            final Room room = roomList.stream().filter(r -> r.roomId == roomId).findFirst().orElse(null);

            if (room != null) {
                if (room.hasNoPlayers()) {
                    return new ViewCreatedRoomsResponse(SUCCESS_STATUS, ROOM_WITH_ID + roomId + " exists and has no players", roomId);
                } else if (!room.isFull()) {
                    return new ViewCreatedRoomsResponse(SUCCESS_STATUS, ROOM_WITH_ID + roomId + " exists but is not full", roomId);
                } else {
                    return new ViewCreatedRoomsResponse(FAIL_STATUS, ROOM_WITH_ID + roomId + " is already occupied", roomId);
                }
            } else {
                return new ViewCreatedRoomsResponse(FAIL_STATUS, ROOM_WITH_ID + roomId + " not found", roomId);
            }
        }));
    }

    /**
     * Добавляет команду создания комнаты для игры.
     *
     * @param commands - список команд
     */
    private static void addCreateRoomCommand(@NotNull final Database database, @NotNull final List<Command> commands) {
        commands.add(Command.newCommand("CREATEROOM", (jsonRequest, uuid) -> {
            if (onlineUsers.containsKey(uuid)) {
                Room room = new Room();
                room.setBlackPlayerUUID(uuid);
                room.blackPlayer = onlineUsers.get(uuid);
                String name = onlineUsers.get(uuid);
                room.blackPlayerId = database.getPlayerId(name);
                room.roomId = random.nextInt(Integer.MAX_VALUE);
                roomList.add(room);
                System.out.println(room.roomId);
                return new CreateRoomResponse(SUCCESS_STATUS, "Room was successfully registered", room.roomId);
            }
            return new AuthorizationResponse(FAIL_STATUS, YOU_ARE_NOT_LOGGED_IN);
        }));
    }

    /**
     * Добавляет команду подсоединения к уже существующей комнате.
     *
     * @param database - база данных
     * @param commands - список команд
     */
    private static void addConnectToRoomCommand(@NotNull final Database database, @NotNull final List<Command> commands) {
        commands.add(Command.newCommand("CONNECTTOROOM", (jsonRequest, uuid) -> {
            if (onlineUsers.containsKey(uuid)) {
                final int roomId = jsonRequest.get(ROOM_ID_STRING).getAsInt();
                final Room room = roomList.stream().filter(r -> r.roomId == roomId).findFirst().orElse(null);
                if (room != null && room.hasNoPlayers()) {
                    room.setBlackPlayerUUID(uuid);
                    room.blackPlayer = onlineUsers.get(uuid);
                    return new ConnectToRoomResponse(SUCCESS_STATUS, "Connected to room as BlackPlayer");
                } else if (room != null && !room.isFull()) {
                    room.setWhitePlayerUUID(uuid);
                    room.whitePlayer = onlineUsers.get(uuid);
                    final UUID opponent = room.getOpponentUUID(uuid);
                    final ClientProcessor opponentPlayer = clients.get(opponent);
                    room.whitePlayerId = database.getPlayerId(room.whitePlayer);
                    opponentPlayer.sendReply(new ConnectToRoomResponse(SUCCESS_STATUS, "White player connected"));
                    return new ConnectToRoomResponse(SUCCESS_STATUS, "Connected to room as WhitePlayer");
                } else if (room == null) {
                    return new ConnectToRoomResponse(FAIL_STATUS, ROOM_WITH_ID + roomId + " not found");
                } else {
                    return new ConnectToRoomResponse(FAIL_STATUS, ROOM_WITH_ID + roomId + " is already occupied");
                }
            }
            return new ConnectToRoomResponse(FAIL_STATUS, YOU_ARE_NOT_LOGGED_IN);
        }));
    }

    /**
     * Добавляет команду выхода из комнаты.
     *
     * @param commands - список команд
     */
    private static void addLeaveRoomCommand(@NotNull final List<Command> commands) {
        commands.add(Command.newCommand("LEAVEROOM", (jsonRequest, uuid) -> {
            if (onlineUsers.containsKey(uuid)) {
                final int roomId = jsonRequest.get(ROOM_ID_STRING).getAsInt();
                final Room room = roomList.stream().filter(r -> r.roomId == roomId).findFirst().orElse(null);
                if (room != null) {
                    room.removePlayer(uuid);
                    return new LeaveRoomResponse(SUCCESS_STATUS, "You successfully get out");
                }
                return new LeaveRoomResponse(FAIL_STATUS, YOU_ARE_NOT_IN_ROOM);
            }
            return new LeaveRoomResponse(FAIL_STATUS, YOU_ARE_NOT_LOGGED_IN);
        }));
    }

    /**
     * Добавляет команду завершения игры.
     *
     * @param commands - список команд
     */
    private static void addGameOverCommand(@NotNull final List<Command> commands) {
        commands.add(Command.newCommand("GAMEOVER", (jsonRequest, uuid) -> {
            if (onlineUsers.containsKey(uuid)) {
                final Room room = roomList.stream().filter(r -> r.hasPlayer(uuid)).findFirst().orElse(null);
                if (room != null) {
                    if (room.hasPlayer(room.getOpponentUUID(uuid))) {
                        room.removePlayer(uuid);
                    } else {
                        roomList.remove(room);
                    }
                    return new GameOverResponse(SUCCESS_STATUS, "Game is over. You are not in room anymore");
                }
                return new GameOverResponse(FAIL_STATUS, "You are not playing game");
            }
            return new GameOverResponse(FAIL_STATUS, YOU_ARE_NOT_LOGGED_IN);
        }));
    }

    /**
     * Добавляет команду начала игры
     *
     * @param commands - список команд
     */
    private static void addStartGameCommand(@NotNull final List<Command> commands) {
        commands.add(Command.newCommand("STARTGAME", (jsonRequest, uuid) -> {
            if (onlineUsers.containsKey(uuid)) {
                final int roomId = jsonRequest.get(ROOM_ID_STRING).getAsInt();
                boolean guiFlag = jsonRequest.get("guiFlag").getAsBoolean();
                final Room room = roomList.stream().filter(r -> r.roomId == roomId).findFirst().orElse(null);
                if (room != null) {
                    room.board = new Board();
                    room.game = new Game();
                    final ClientProcessor blackPlayer = clients.get(room.getBlackPlayerUUID());
                    final ClientProcessor whitePlayer = clients.get(room.getWhitePlayerUUID());

                    final List<Move> availableBlackMoves = room.board.getAllAvailableMoves(Cell.BLACK);
                    final String availableMovesString = availableBlackMoves.toString();
                    final String boardString = Board.displayBoardOnClient(room.board);
                    final String boardStringWON = Board.displayBoardOnClientWithoutNumbers(room.board);

                    room.gameId = random.nextInt(Integer.MAX_VALUE);

                    GameLogger.logStart(room.gameId, "games/" + room.gameId);
                    if (guiFlag) {
                        if (uuid == room.getBlackPlayerUUID()) {
                            return new WhereIcanGoResponse(availableMovesString, boardString, boardStringWON, PLAYER_COLOR_BLACK);
                        } else {
                            return new WhereIcanGoResponse("", boardString, boardStringWON, PLAYER_COLOR_WHITE);
                        }
                    } else {
                        if (uuid == room.getBlackPlayerUUID()) {
                            blackPlayer.sendReply(new StartGameResponse(SUCCESS_STATUS, GAME_STARTED));
                            whitePlayer.sendReply(new StartGameResponse(SUCCESS_STATUS, GAME_STARTED));
                            whitePlayer.sendReply(new WhereIcanGoResponse(availableMovesString, boardString, boardStringWON, PLAYER_COLOR_WHITE));
                            return new WhereIcanGoResponse(availableMovesString, boardString, boardStringWON, PLAYER_COLOR_BLACK);
                        } else {
                            blackPlayer.sendReply(new StartGameResponse(SUCCESS_STATUS, GAME_STARTED));
                            blackPlayer.sendReply(new WhereIcanGoResponse(availableMovesString, boardString, boardStringWON, PLAYER_COLOR_BLACK));
                            whitePlayer.sendReply(new WhereIcanGoResponse(availableMovesString, boardString, boardStringWON, PLAYER_COLOR_WHITE));
                            return new StartGameResponse(SUCCESS_STATUS, GAME_STARTED);
                        }
                    }
                }
                return new StartGameResponse(FAIL_STATUS, "No such room");
            }
            return new GameOverResponse(FAIL_STATUS, YOU_ARE_NOT_LOGGED_IN);
        }));
    }

    /**
     * Добавляет команду сдаться.
     *
     * @param commands - список команд
     */
    private static void addSurrenderCommand(@NotNull final List<Command> commands) {
        commands.add(Command.newCommand("SURRENDER", (jsonRequest, uuid) -> {
            if (onlineUsers.containsKey(uuid)) {
                final Room room = roomList.stream().filter(r -> r.hasPlayer(uuid)).findFirst().orElse(null);
                if (room != null) {
                    final ClientProcessor opponentClientProcessor = clients.get(room.getOpponentUUID(uuid));
                    final int blackCount = room.board.getQuantityOfBlack();
                    final int whiteCount = room.board.getQuantityOfWhite();
                    final String stringReply = String.format("Number of Black pieces: %d %n Number of White pieces: %d ", blackCount, whiteCount);
                    opponentClientProcessor.sendReply(new SurrenderResponse(SUCCESS_STATUS, "Your opponent has surrendered \n" + stringReply));
                    return new SurrenderResponse(SUCCESS_STATUS, "You surrendered %n" + stringReply);
                } else {
                    return new SurrenderResponse(FAIL_STATUS, YOU_ARE_NOT_IN_ROOM);
                }
            }
            return new GameOverResponse(FAIL_STATUS, YOU_ARE_NOT_LOGGED_IN);
        }));
    }

    private static void addMakeMoveCommand(@NotNull final List<Command> commands) {
        commands.add(Command.newCommand("MAKEMOVE", (jsonRequest, uuid) -> {
            if (onlineUsers.containsKey(uuid)) {
                final int row = jsonRequest.get("row").getAsInt() - 1;
                final int col = jsonRequest.get("col").getAsInt() - 1;
                final Room room = roomList.stream().filter(r -> r.hasPlayer(uuid)).findFirst().orElse(null);

                if (room != null) {
                    if (uuid == room.getBlackPlayerUUID()) {
                        return makeMoveResponse(Cell.BLACK, uuid, room, row, col);
                    } else if (uuid == room.getWhitePlayerUUID()) {
                        return makeMoveResponse(Cell.WHITE, uuid, room, row, col);
                    }
                }
                return new MakeMoveResponse(FAIL_STATUS, YOU_ARE_NOT_IN_ROOM);
            }
            return new GameOverResponse(FAIL_STATUS, YOU_ARE_NOT_LOGGED_IN);
        }));
    }

    private static void addWhereICanGoCommand(@NotNull final List<Command> commands) {
        commands.add(Command.newCommand("WHEREICANGORESPONSE", (jsonRequest, uuid) -> {
            if (onlineUsers.containsKey(uuid)) {
                final Room room = roomList.stream().filter(r -> r.hasPlayer(uuid)).findFirst().orElse(null);
                System.out.println(room.roomId);
                if (room != null) {
                    Cell playerCell = room.getCell(uuid);
                    if (room.game.nextTurnOfPlayerColor.equals(playerCell.reverse())) {
                        return new MakeMoveResponse(FAIL_STATUS, ITS_NOT_YOUR_TURN);
                    }
                    List<Move> availableMoves = room.board.getAllAvailableMoves(playerCell);
                    String availableMovesString = availableMoves.toString();
                    String boardString = Board.displayBoardOnClient(room.board);
                    String boardStringWON = Board.displayBoardOnClientWithoutNumbers(room.board);
                    return new WhereIcanGoResponse(availableMovesString, boardString, boardStringWON,
                            playerCell == Cell.BLACK ? PLAYER_COLOR_WHITE : PLAYER_COLOR_BLACK);
                }
                return new MakeMoveResponse(FAIL_STATUS, YOU_ARE_NOT_IN_ROOM);
            }
            return new GameOverResponse(FAIL_STATUS, YOU_ARE_NOT_LOGGED_IN);
        }));
    }

    private static Response makeMoveResponse(Cell playerCell, UUID uuid, Room room, int row, int col) {
        if (room.game.nextTurnOfPlayerColor.equals(playerCell.reverse())) {
            return new MakeMoveResponse(FAIL_STATUS, ITS_NOT_YOUR_TURN);
        }
        final UUID opponent = room.getOpponentUUID(uuid);
        final ClientProcessor opponentClientProcessor = Server.clients.get(opponent);
        final List<Move> availableMoves = room.board.getAllAvailableMoves(playerCell);

        if (!availableMoves.contains(new Move(row, col))) {
            return new MakeMoveResponse(FAIL_STATUS, "Wrong move");
        }
        room.board.placePiece(row, col, playerCell);
        GameLogger.logMove(room.board, row, col, room.blackPlayerId, "BLACK", "games/" + room.gameId, room.game.numberOfTurn++);

        final List<Move> opponentAvailableMoves = room.board.getAllAvailableMoves(playerCell.reverse());
        final String availableMovesString = opponentAvailableMoves.toString();
        final String boardString = Board.displayBoardOnClient(room.board);
        final String boardStringWON = Board.displayBoardOnClientWithoutNumbers(room.board);

        if (room.board.getAllAvailableMoves(Cell.BLACK).isEmpty() && room.board.getAllAvailableMoves(Cell.WHITE).isEmpty()) {
            return getGameOverResponse(uuid, room);
        }
        if (!opponentAvailableMoves.isEmpty()) {
            room.game.nextTurnOfPlayerColor = playerCell.reverse();
            System.out.println(availableMovesString);
            opponentClientProcessor.sendReply(new WhereIcanGoResponse(availableMovesString, boardString, boardStringWON, PLAYER_COLOR_WHITE));
        } else {
            return new WhereIcanGoResponse(room.board.getAllAvailableMoves(playerCell).toString(), boardString, boardStringWON, PLAYER_COLOR_BLACK);
        }
        return new MakeMoveResponse(SUCCESS_STATUS, "You did your turn.");
    }

    private static GameOverResponse getGameOverResponse(UUID uuid, Room room) {
        final String gameOverMsg = displayResultOnClient(room.board);
        Server.clients.get(room.getOpponentUUID(uuid)).sendReply(new GameOverResponse(SUCCESS_STATUS, gameOverMsg));
        GameLogger.logEnd(room.board, "fileForHuman");
        room.changeColor();
        return new GameOverResponse(SUCCESS_STATUS, gameOverMsg);
    }
}
