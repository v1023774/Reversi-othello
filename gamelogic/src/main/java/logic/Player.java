package logic;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Абстрактный класс Player представляет игрока в игре.
 * У каждого игрока есть свой тип фишки (клетки) - Cell,
 * который представляет цвет игрока (BLACK или WHITE).
 * <p>
 * Каждый игрок получает уникальный идентификатор, который может быть использован
 * для статистики или для других целей, требующих идентификации игрока.
 */
public abstract class Player {
    private static final AtomicInteger playerCounter = new AtomicInteger(0);
    public final int playerId;
    public final Cell playerCell;


    /**
     * Конструктор создает объект игрока с указанным типом фишки (клетки) и присваивает
     * ему уникальный идентификатор.
     *
     * @param playerCell тип фишки (клетки) игрока (BLACK или WHITE).
     */
    protected Player(@NotNull final Cell playerCell) {
        this.playerId = playerCounter.incrementAndGet();
        this.playerCell = playerCell;
    }

    /**
     * Абстрактный метод makeMove, который должен быть реализован в подклассах.
     * Определяет ход игрока в зависимости от типа игрока (HumanPlayer или BotPlayer).
     *
     * @param board доска, на которой происходит игра.
     * @return возвращает объект Move, представляющий сделанный игроком ход.
     */
    public abstract Move makeMove(@NotNull final Board board);

    /**
     * Подкласс HumanPlayer представляет человеческого игрока, который делает ходы с помощью ввода с клавиатуры.
     */
    public static class HumanPlayer extends Player {
        public static final Scanner scanner = new Scanner(System.in);

        public HumanPlayer(@NotNull final Cell playerCell) {
            super(playerCell);
        }

        @Override
        public Move makeMove(@NotNull final Board board) {
            final List<Move> availableMoves = board.getAllAvailableMoves(playerCell);
            System.out.println("Доступные ходы: ");
            for (Move m : availableMoves) {
                System.out.println((m.row + 1) + " " + (m.col + 1));
            }
            boolean invalidInput = false;
            while (true) {

                System.out.print("Введите строку и столбец: ");
                final String input = scanner.nextLine();
                String[] inputArray = input.trim().split("\\s+");
                if (inputArray.length == 2) {
                    try {
                        final int row = Integer.parseInt(inputArray[0]) - 1;
                        final int col = Integer.parseInt(inputArray[1]) - 1;
                        final Move move = new Move(row, col);

                        if (availableMoves.contains(move)) {
                            board.placePiece(row, col, playerCell);
                            return move;
                        } else {
                            System.out.println("Недопустимый ход! Пожалуйста, выберите из доступных ходов.");
                            invalidInput = true; // Устанавливаем флаг в true при недопустимом ходе
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Недопустимый ввод! Пожалуйста, введите два числа через пробел.");
                        invalidInput = true; // Устанавливаем флаг в true при недопустимом вводе
                    }
                } else {
                    if (invalidInput) {
                        System.out.println("Недопустимый ввод! Пожалуйста, введите два числа через пробел.");
                    }
                }
            }
        }

    }


    /**
     * Подкласс BotPlayer представляет игрока-бота, который делает случайные ходы.
     */
    public static class BotPlayer extends Player {
        private final Random random;

        public BotPlayer(@NotNull final Cell playerCell) {
            super(playerCell);
            this.random = new Random();
        }

        @Override
        public Move makeMove(@NotNull final Board board) {
            final List<Move> availableMoves = board.getAllAvailableMoves(playerCell);
            final Move move = availableMoves.get(random.nextInt(availableMoves.size()));
            board.placePiece(move.row, move.col, playerCell);
            return move;
        }

    }


}