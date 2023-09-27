package localgame;

import gamelogging.LogAnalyzer;
import logic.Board;
import logic.Cell;
import logic.Player;

import java.util.Random;
import java.util.Scanner;

/**
 * Класс для запуска локальной игры с текстовым интерфейсом, можно играть против бота, против друг друга, посомтреть игру 2 ботов или
 * проанализировать файл "fileForHuman" и записать игры в бд
 */
public class Application {
    private static final String FILENAME = "fileForHuman";
    private static Random random = new Random();

    public static void main(String[] args) {
        startConsoleInterface();
    }

    private static void startConsoleInterface() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите режим игры:");
        System.out.println("1. Human vs Human");
        System.out.println("2. Human vs Bot");
        System.out.println("3. Bot vs Bot");
        System.out.println("4. запись игр в бд");
        int choice = scanner.nextInt();

        if (choice == 1) {
            startHumanVsHumanGame();
        } else if (choice == 2) {
            startHumanVsBotGame();
        } else if (choice == 3) {
            startBotVsBotGame();
        } else if (choice == 4) {
            LogAnalyzer.parseLoginToBd(FILENAME);
        } else {
            System.out.println("Неверный выбор");
        }
    }

    /**
     * игра человек против человека
     */
    private static void startHumanVsBotGame() {
        final int stableId = random.nextInt(Integer.MAX_VALUE);
        new Game().startGame(new Board(), new Player.HumanPlayer(Cell.BLACK), new Player.BotPlayer(Cell.WHITE),
                stableId, FILENAME);
    }

    /**
     * игра человек против бота
     */
    private static void startHumanVsHumanGame() {
        final int stableId = random.nextInt(Integer.MAX_VALUE);
        new Game().startGame(new Board(), new Player.HumanPlayer(Cell.BLACK), new Player.HumanPlayer(Cell.WHITE),
                stableId, FILENAME);
    }

    /**
     * игра бот против бота
     */
    private static void startBotVsBotGame() {
        final int stableId = random.nextInt(Integer.MAX_VALUE);
        new Game().startGame(new Board(), new Player.BotPlayer(Cell.BLACK), new Player.BotPlayer(Cell.WHITE),
                stableId, FILENAME);
    }


}
