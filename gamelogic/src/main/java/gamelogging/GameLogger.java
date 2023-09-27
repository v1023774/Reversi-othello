package gamelogging;

import logic.Board;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Класс Logging дает возможность записывть ход игры.
 */
public class GameLogger {
    private static final Logger logger = LogManager.getLogger(GameLogger.class);

    private GameLogger() {
    }

    /**
     * Метод logMove записыват в файлы ходы белых, черных и положение доски после сделанного хода.
     *
     * @param board        доска.
     * @param row          строка.
     * @param col          колонка.
     * @param id           id игрока.
     * @param color        цвет игрока.
     * @param fileForHuman файл человекочитаемых записей
     * @param numberOfTurn номер хода
     */
    public static void logMove(final Board board, final int row, final int col, int id, String color,
                               final String fileForHuman, int numberOfTurn) {
        try (FileWriter writeForHuman = new FileWriter(fileForHuman, true)) {
            StringBuilder textForHuman = new StringBuilder();
            textForHuman.append(constructStringForLogMove(color, id, row + 1, col + 1, numberOfTurn));
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    textForHuman.append(board.get(i, j).toString()).append(" ");
                }
                textForHuman.append("\n");
            }
            writeForHuman.write(textForHuman.toString());
            writeForHuman.flush();
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Ошибка в логировании хода.");
        }
    }

    /**
     * Метод constructStringForLogMove создает строку для записи хода игрока
     *
     * @param color        цвет игрока.
     * @param playerId     id игрока.
     * @param row          строка игры.
     * @param col          колонка.
     * @param numberOfTurn номер хода.
     */

    private static String constructStringForLogMove(String color, final int playerId, final int row, final int col, int numberOfTurn) {
        final String line;
        if (color.equals("BLACK")) {
            line = String.format("PlayerId: %d BLACK placed his piece on %d %d turn №%d%n", playerId, row, col, numberOfTurn);
        } else {
            line = String.format("PlayerId: %d WHITE placed his piece on %d %d turn №%d%n", playerId, row, col, numberOfTurn);
        }
        return line;
    }

    /**
     * Метод logStart записыват в файл дату игры.
     *
     * @param gameId       id игры.
     * @param fileForHuman файл записи игр.
     */
    public static void logStart(final int gameId, final String fileForHuman) {
        try (FileWriter writeForHuman = new FileWriter(fileForHuman, true)) {
            final String text = "Game id " + gameId + " \n";
            writeForHuman.write(text);
            writeForHuman.flush();
        } catch (IOException ex) {
            logger.log(Level.ERROR, () -> "ошибка в начале логирования, gameID: " + gameId);
        }
    }

    /**
     * Метод logEnd записывает результат игры.
     *
     * @param board        доска.
     * @param fileForHuman файл записи игры
     */
    public static void logEnd(final Board board, final String fileForHuman) {
        try (FileWriter writeForHuman = new FileWriter(fileForHuman, true)) {
            final int blackCount = board.getQuantityOfBlack();
            final int whiteCount = board.getQuantityOfWhite();
            String textForHuman = String.format("Number of Black pieces: %d%nNumber of white pieces: %d.%nWinner: ", blackCount, whiteCount);
            if (blackCount > whiteCount) {
                textForHuman += "Black\n";
            } else if (whiteCount > blackCount) {
                textForHuman += "White\n";
            } else {
                textForHuman += "It's tie\n";
            }
            writeForHuman.write(textForHuman);
            writeForHuman.flush();
        } catch (IOException ex) {
            logger.log(Level.ERROR, "ошибка в завершении логирования.");
        }
    }
}
