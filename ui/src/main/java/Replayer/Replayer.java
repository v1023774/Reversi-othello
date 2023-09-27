package Replayer;

import database.Database;
import database.models.Boards;
import database.models.Moves;
import logic.Board;
import logic.Cell;
import logic.Move;
import org.jetbrains.annotations.NotNull;
import parsing.BoardParser;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Replayer extends JFrame {
    private JLabel gameId;
    private JTextField gameIdTextField;
    private JButton findGame;
    private JButton doNextMove;
    private JButton doPreviousMove;
    private JLabel turnColor;
    private JLabel blackPlayerId;
    private JLabel blackPlayerIdLabel;
    private JLabel blackPlayerName;
    private JLabel whitePlayerIdLabel;
    private JLabel whitePlayerId;
    private JLabel whitePlayerName;
    private JPanel boardPanel;
    private final int BOARD_SIZE = 8;
    private JButton[][] buttons;
    private List<Boards> boardFromDB;
    private List<Moves> moves;
    private Database database = new Database();
    private int numberOfTurn;
    private Container container;
    private String winner;

    private Replayer() {
        createUIComponents();
        createActionListeners(findGame, doNextMove, doPreviousMove);

        final JPanel mainPanel = new JPanel(new FlowLayout());
        addComponents(mainPanel);

        container = getContentPane();
        container.add(turnColor, BorderLayout.SOUTH);
        container.add(mainPanel, BorderLayout.CENTER);

        setTitle("Replayer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 630);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            var ex = new Replayer();
            ex.setVisible(true);
        });
    }

    private void createUIComponents() {
        gameId = new JLabel("Enter game Id");
        gameIdTextField = new JTextField(10);
        findGame = new JButton("Find game");
        doNextMove = new JButton("Next Move");
        doNextMove.setEnabled(false);
        doPreviousMove = new JButton("Previous Move");
        doPreviousMove.setEnabled(false);
        blackPlayerIdLabel = new JLabel("Black Player id and name ");
        blackPlayerId = new JLabel();
        blackPlayerName = new JLabel();
        whitePlayerIdLabel = new JLabel("White Player id and name ");
        whitePlayerId = new JLabel();
        whitePlayerName = new JLabel();
        turnColor = new JLabel("Black player turn " + (numberOfTurn + 1));
        turnColor.setVisible(false);

        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];

        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setPreferredSize(new Dimension(62, 62));
                boardPanel.add(buttons[row][col]);
            }
        }
    }

    private void addComponents(@NotNull final JPanel mainPanel) {
        mainPanel.add(gameId);
        mainPanel.add(gameIdTextField);
        mainPanel.add(findGame);
        mainPanel.add(doNextMove);
        mainPanel.add(doPreviousMove);
        mainPanel.add(blackPlayerIdLabel);
        mainPanel.add(blackPlayerId);
        mainPanel.add(blackPlayerName);
        mainPanel.add(boardPanel);
        mainPanel.add(whitePlayerIdLabel);
        mainPanel.add(whitePlayerId);
        mainPanel.add(whitePlayerName);
        mainPanel.add(turnColor);
    }

    private void createActionListeners(@NotNull final JButton findGame, @NotNull final JButton doNextMove, @NotNull final JButton doPreviousMove) {
        findGame.addActionListener(event -> {
                    if (!gameIdTextField.getText().matches("^(\\d{1,10})$")) {
                        JOptionPane.showMessageDialog(this, "Wrong input");
                        return;
                    }
                    final int gameId = Integer.parseInt(gameIdTextField.getText());
                    moves = database.getGameMoves(gameId);
                    boardFromDB = database.getGameBoards(gameId);
                    if (boardFromDB.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "There is no such game");
                    } else {
                        doNextMove.setEnabled(true);
                        doPreviousMove.setEnabled(false);

                        final int blackPlayerIdFromDB = database.getPlayerIdFromGame(gameIdTextField.getText(), 'b');
                        final int whitePlayerIdFromDB = database.getPlayerIdFromGame(gameIdTextField.getText(), 'w');

                        blackPlayerId.setText(String.valueOf(blackPlayerIdFromDB));
                        blackPlayerName.setText(database.getPlayerName(blackPlayerIdFromDB));
                        whitePlayerId.setText(String.valueOf(whitePlayerIdFromDB));
                        whitePlayerName.setText(database.getPlayerName(whitePlayerIdFromDB));

                        startBoardPosition();
                    }
                    numberOfTurn = 0;

                }
        );

        doNextMove.addActionListener(event -> {
                    numberOfTurn += 1;
                    updateBoard(boardFromDB.get(numberOfTurn - 1), numberOfTurn);
                    if (numberOfTurn < 1) {
                        doPreviousMove.setEnabled(false);
                    } else {
                        doPreviousMove.setEnabled(true);
                    }
                    if (numberOfTurn == boardFromDB.size()) {
                        doNextMove.setEnabled(false);
                        turnColor.setText(turnColor.getText() + winner);
                    }
                }
        );

        doPreviousMove.addActionListener(event -> {
                    if (numberOfTurn == 1) {
                        startBoardPosition();
                        numberOfTurn -= 1;
                        doPreviousMove.setEnabled(false);
                        return;
                    }
                    numberOfTurn -= 1;
                    updateBoard(boardFromDB.get(numberOfTurn - 1), numberOfTurn);

                    if (numberOfTurn == boardFromDB.size()) {
                        doNextMove.setEnabled(false);

                    } else {
                        doNextMove.setEnabled(true);
                    }
                    if (numberOfTurn < 1) {
                        doPreviousMove.setEnabled(false);
                    }
                }
        );
    }

    private void updateBoard(@NotNull final Boards boardFromDB, int numberOfTurnForTurn) {
        final Board board = BoardParser.parse(boardFromDB.board, 'b', 'w', ' ');
        updateBoardColors(board, BOARD_SIZE, buttons);
        final int blackCount = board.getQuantityOfBlack();
        final int whiteCount = board.getQuantityOfWhite();
        if (numberOfTurnForTurn == this.boardFromDB.size()) {
            if (blackCount > whiteCount) {
                winner = ". Winner is black. Total score is ";

            } else if (whiteCount > blackCount) {
                winner = ". Winner is white. Total score is ";

            } else {
                winner = ". It's tie. Total score is ";
            }
            winner += blackCount + " - " + whiteCount;
        }
        if (numberOfTurnForTurn != this.boardFromDB.size()) {

            final char colorForChangeTurnLabel = moves.get(numberOfTurnForTurn).color;
            for (Move move : board.getAllAvailableMoves(colorForChangeTurnLabel == 'b' ? Cell.BLACK : Cell.WHITE)) {
                buttons[move.row][move.col].setBackground(Color.GRAY);
            }
            turnColor.setText((colorForChangeTurnLabel == 'b' ? "Black player turn " + (numberOfTurn + 1) : "White player turn " + (numberOfTurn + 1)));
        }

    }

    public static void updateBoardColors(@NotNull final Board board, int board_size, JButton[][] buttons) {
        for (int row = 0; row < board_size; row++) {
            for (int col = 0; col < board_size; col++) {
                if (board.get(row, col).equals(Cell.BLACK)) {
                    buttons[row][col].setBackground(Color.black);
                } else if (board.get(row, col).equals(Cell.WHITE)) {
                    buttons[row][col].setBackground(Color.white);
                } else {
                    buttons[row][col].setBackground(null);
                }
            }
        }
    }

    private void startBoardPosition() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col].setBackground(null);
            }
        }
        buttons[3][3].setBackground(Color.white);
        buttons[4][4].setBackground(Color.white);
        buttons[3][4].setBackground(Color.black);
        buttons[4][3].setBackground(Color.black);
        buttons[2][3].setBackground(Color.GRAY);
        buttons[3][2].setBackground(Color.GRAY);
        buttons[4][5].setBackground(Color.GRAY);
        buttons[5][4].setBackground(Color.GRAY);
        turnColor.setVisible(true);
        turnColor.setText("Black player turn 1");
    }
}
