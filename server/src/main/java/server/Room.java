package server;


import localgame.Game;
import logic.Board;
import logic.Cell;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
/**
 * Класс комнаты, создаваемой для игры на сервере
 */
public class Room {

    /**
     * id комнаты
     */
    public int roomId = 0;
    /**
     * имя игрока белыми фиками
     */
    public String whitePlayer = "";
    /**
     * имя игрока черными фиками
     */
    public String blackPlayer = "";
    /**
     * Id белыми фиками
     */
    public int whitePlayerId;
    /**
     * Id черными фиками
     */
    public int blackPlayerId;
    /**
     * UUID игрока белыми фиками
     */
    private UUID whitePlayerUUID;
    /**
     * UUID игрока черными фиками
     */
    private UUID blackPlayerUUID;
    /**
     * доска, на которой играют
     */
    public Board board = new Board();
    /**
     * игра
     */
    public Game game = new Game();
    /**
     * id игры
     */
    public int gameId;
    /**
     * Метод возвращает цвет игрока по UUID
     */
    public Cell getCell(@NotNull final UUID uuid) {
        if(uuid.equals(whitePlayerUUID)){
            return Cell.WHITE;
        }
        return Cell.BLACK;
    }
    /**
     * Метод возвращает UUID оппонента по UUID игрока
     */
    public UUID getOpponentUUID(@NotNull final UUID uuid) {
        if (uuid == whitePlayerUUID) {
            return blackPlayerUUID;
        } else {
            return whitePlayerUUID;
        }
    }

    /**
     * Проверяет наличие места в комнате.
     *
     * @return возвращает true при наличии мест и false при отсутствии.
     */
    public boolean checkHavePlace() {
        if ((whitePlayer.isEmpty() && blackPlayer.isEmpty()) || (whitePlayer.isEmpty() || blackPlayer.isEmpty())) {
            return true;
        }
        return false;
    }
    /**
     * Меняет в комнате игроков цветами
     */
    public void changeColor() {
        final String whitePlayerSave = whitePlayer;
        final UUID whitePlayerUUIDSave = whitePlayerUUID;
        whitePlayer = blackPlayer;
        blackPlayer = whitePlayerSave;
        whitePlayerUUID = blackPlayerUUID;
        blackPlayerUUID = whitePlayerUUIDSave;
    }
    /**
     * Проверяет наличие конкретного игрока в комнате.
     *
     * @return возвращает true при наличии и false при отсутствии.
     */
    public boolean hasPlayer(UUID uuid) {
        return whitePlayerUUID.equals(uuid) || blackPlayerUUID.equals(uuid);
    }

    /**
     * Удаляет игрока из комнаты.
     */
    public void removePlayer(@NotNull final UUID uuid) {
        if (whitePlayerUUID.equals(uuid)) {
            whitePlayer = "";
            whitePlayerUUID = null;
        } else if (blackPlayerUUID.equals(uuid)) {
            blackPlayer = "";
            blackPlayerUUID = null;
        }
    }
    /**
     * Метод возвращает UUID игрока с белыми фишками
     */
    public UUID getWhitePlayerUUID() {
        return whitePlayerUUID;
    }
    /**
     * Метод возвращает UUID игрока с черными фишками
     */
    public UUID getBlackPlayerUUID() {
        return blackPlayerUUID;
    }
    /**
     * Метод устанавливает UUID игрока с белыми фишками
     */
    public void setWhitePlayerUUID(UUID player) {
        this.whitePlayerUUID = player;
    }
    /**
     * Метод устанавливает UUID игрока с черными фишками
     */
    public void setBlackPlayerUUID(UUID player) {
        this.blackPlayerUUID = player;
    }

    /**
     * Проверяет, пустая ли комната.
     *
     * @return возвращает true, если пустая, и false, если кто-то есть.
     */
    public boolean hasNoPlayers() {
        return whitePlayer == null && blackPlayer == null;
    }

    /**
     * Проверяет, полная ли комната
     *
     * @return возвращает true если полная, и false если никого нет или игрок только один.
     */
    public boolean isFull() {
        return whitePlayerUUID != null && blackPlayerUUID != null;
    }


}