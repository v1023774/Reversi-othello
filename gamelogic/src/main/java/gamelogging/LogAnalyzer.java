package gamelogging;

import database.Database;
import database.models.Boards;
import database.models.Game;
import database.models.Moves;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс LogAnalyzer дает возможность анализировать файл с записаью игр, показывать статистику по победам/поражениям,
 * количеству выигранных игр.
 */
public class LogAnalyzer {

    private LogAnalyzer() {
    }

    private static final Logger logger = LogManager.getLogger(LogAnalyzer.class);

    /**
     * Метод parseLog записывает статистику по каждой игре в файле с логами игры.
     *
     * @param fileNameReadLog  Название файла в который будет записан ход понятный человеку.
     * @param fileNameWriteLog Название файла с укороченной записью.
     */
    public static void parseLog(final String fileNameReadLog, final String fileNameWriteLog) {
        try (final FileReader file = new FileReader(fileNameReadLog)) {
            final BufferedReader reader = new BufferedReader(file);
            int currentNumberOfLine = 1;
            int idFirstPlayer = 0;
            int idSecondPlayer = 0;
            String line = reader.readLine();
            while (line != null) {
                if (currentNumberOfLine == 2) {
                    idFirstPlayer = calculatePlayerId(line, 'B');
                }
                if (currentNumberOfLine == 11) {
                    idSecondPlayer = calculatePlayerId(line, 'W');
                }
                if (line.startsWith("Winner")) {
                    final String winner = line.substring(8, 13);
                    if (winner.equals("Black")) {
                        addAnalysis(idFirstPlayer, winner, idSecondPlayer, fileNameWriteLog);
                    }
                    if (winner.equals("White")) {
                        addAnalysis(idSecondPlayer, winner, idFirstPlayer, fileNameWriteLog);
                    }

                    currentNumberOfLine = 0;
                }
                currentNumberOfLine++;
                line = reader.readLine();
            }

        } catch (IOException ex) {
            logger.log(Level.ERROR, "Ошибка при попытке парсирования логов.");
        }
    }

    /**
     * Метод calculatePlayerId парсит строку и выдает id игрока
     *
     * @param line  строка.
     * @param color цвет.
     */
    private static int calculatePlayerId(@NotNull final String line, final char color) {
        return Integer.parseInt(line.substring(10, line.indexOf(color) - 1));
    }

    /**
     * Метод writeAnalysis записывает статистику по игрокам, которые играют друг с дргуом первый раз.
     *
     * @param idPlayer         id игрока который победил.
     * @param color            цвет победившего игрока.
     * @param idOpponent       id проигравшего.
     * @param fileNameWriteLog Название файла с анализом логов.
     */
    private static void writeAnalysis(final int idPlayer, @NotNull final String color, final int idOpponent, @NotNull final String fileNameWriteLog) {
        try (final FileWriter writeForHuman = new FileWriter(fileNameWriteLog, true)) {
            final String textForHuman = String.format("PlayerId %d win vs PlayerId %d by %s %d times%n", idPlayer, idOpponent, color, 1);
            writeForHuman.write(textForHuman);
            writeForHuman.flush();
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Ошибка при попытке использовать метод writeAnalysis.");
        }
    }

    /**
     * Метод addAnalysis записывает статистику по игрокам, которые уже играли друг с дргуом.
     *
     * @param playerId         id игрока который победил.
     * @param color            цвет победившего игрока.
     * @param opponentId       id проигравшего.
     * @param fileNameWriteLog Название файла с анализом логов.
     */
    private static void addAnalysis(final int playerId, @NotNull final String color, final int opponentId, @NotNull final String fileNameWriteLog) {
        final Path path = Paths.get(fileNameWriteLog);

        try (final Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8);
             final FileReader file = new FileReader(fileNameWriteLog)) {
            boolean flag = false;
            final BufferedReader reader = new BufferedReader(file);
            String lineFromAnalysis = reader.readLine();
            while (lineFromAnalysis != null) {
                if (lineFromAnalysis.startsWith(String.format("PlayerId %d win vs PlayerId %d", playerId, opponentId))) {
                    flag = true;
                }
                lineFromAnalysis = reader.readLine();
            }
            if (flag) {
                final String lineStart = String.format("PlayerId %d win vs PlayerId %d", playerId, opponentId);
                List<String> list = stream.map(line -> line.startsWith(lineStart) ? changedLine(playerId, opponentId, line, color) : line)
                        .collect(Collectors.toList());
                Files.write(path, list, StandardCharsets.UTF_8);
            } else {
                writeAnalysis(playerId, color, opponentId, fileNameWriteLog);
            }
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Ошибка при попытке использовать метод addAnalysis.");
        }
    }

    /**
     * Метод changedLine создает люнию для перезаписи в файл анализатора логов.
     *
     * @param playerId   id игрока который победил.
     * @param opponentId id проигравшего.
     * @param line       линия для измененияю.
     * @param color      цвет победившег.
     */
    private static String changedLine(int playerId, int opponentId, @NotNull final String line, @NotNull final String color) {
        final int parseId = (Integer.parseInt(line.substring(38, line.indexOf("times") - 1)) + 1);
        return String.format("PlayerId %d win vs PlayerId %d by %s %d times", playerId, opponentId, color, parseId);
    }

    /**
     * Метод parseLoginToBd подробные записи игр в базе данных, создает записи с ходами и досками.
     *
     * @param fileNameReadLog файл для анализа и последующего парсинга для записи в базу данных.
     */
    public static void parseLoginToBd(@NotNull final String fileNameReadLog) {
        try (final FileReader file = new FileReader(fileNameReadLog)) {
            final Database db = new Database();
            final BufferedReader reader = new BufferedReader(file);
            int currentNumberOfLine = 0;
            int idBlackPlayer = 0;
            int idWhitePlayer = 0;
            int gameId = 0;
            int block = -1;
            int nom = 0;
            String line = reader.readLine();
            StringBuilder board = new StringBuilder();

            while (line != null) {
                if (line.startsWith("Number of")) {
                    line = reader.readLine();
                    continue;
                }
                if (line.startsWith("Winner")) {
                    final String winnerColor = line.substring(8, 13);
                    db.setWinnerInGameWithId(idBlackPlayer, idWhitePlayer, gameId, winnerColor);
                    block = -1;
                    currentNumberOfLine = 0;
                    line = reader.readLine();
                    continue;
                }
                if (line.startsWith("It's")) {
                    db.setWinnerInGameWithId(idBlackPlayer, idWhitePlayer, gameId, "tie");
                    block = -1;
                    currentNumberOfLine = 0;
                    line = reader.readLine();
                    continue;
                }
                if (currentNumberOfLine == 0) {
                    gameId = Integer.parseInt(line.substring(8));
                    Game game = new Game(0, 0, ' ', gameId);

                    db.addGame(game);
                }
                if (block == 0) {
                    Moves move = createMove(line, gameId);
                    nom = move.numberOfMove;
                    db.addMoves(move);
                }
                if (currentNumberOfLine == 1) {
                    idBlackPlayer = calculatePlayerId(line, 'B');
                }
                if (currentNumberOfLine == 10) {
                    idWhitePlayer = calculatePlayerId(line, 'W');
                }
                if (block > 0) {
                    board.append(line).append("\n");
                }
                if (block == 8) {
                    db.addBoards(createBoard(board.toString(), gameId, nom));
                    block = -1;
                    board = new StringBuilder();
                }
                currentNumberOfLine++;
                block++;
                line = reader.readLine();
            }
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Ошибка при попытке парсирования логов.");
        }
    }

    /**
     * Метод createMove создает объект moves(хода) для записи его в бд.
     *
     * @param line   линия для создания хода.
     * @param gameId id игры.
     */
    private static Moves createMove(@NotNull final String line, final int gameId) {
        final int row = Integer.parseInt(line.substring(line.indexOf("on") + 3, line.indexOf("on") + 4));
        final int col = Integer.parseInt(line.substring(line.indexOf("on") + 5, line.indexOf("on") + 6));
        final int nom = Integer.parseInt(line.substring(line.indexOf("turn") + 8));
        final char color = line.startsWith("BLACK", 12) ? 'b' : 'w';
        return new Moves(row, col, nom, color, gameId);

    }

    /**
     * Метод createBoard создает объект Boards(доски после хода) для записи его в бд.
     *
     * @param line   линия для создания доски.
     * @param gameId id игры.
     * @param nom    номер хода.
     */
    private static Boards createBoard(@NotNull final String line, final int gameId, final int nom) {
        return new Boards(line, nom, gameId);
    }

}
