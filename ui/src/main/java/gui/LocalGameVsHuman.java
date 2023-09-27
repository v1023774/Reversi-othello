package gui;

import guiClient.ClientGui;
import logic.Board;
import logic.Cell;
import logic.Move;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static gui.LocalGameBotVsBot.updateBoardColors;
import static gui.LocalGameWindow.newMusicPlayer;


/**
 * Класс LocalGameBotVsBot представляет собой окно для локальной игры между человеком и человеком.
 * Он содержит игровое поле, кнопки управления звуком и другие элементы интерфейса.
 */
public class LocalGameVsHuman extends JFrame {
    private final int BOARD_SIZE = 8;
    private final JButton[][] buttons;
    private Board board;
    Color liteBlue = new Color(147, 227, 255);
    Color pink = new Color(234, 147, 255);
    Color purple = new Color(132, 2, 182);
    Color blue = new Color(27, 61, 182);
    private ImageIcon playIcon;
    private ImageIcon pauseIcon;
    private boolean isPaused = false;
    Cell currentPlayerColor = Cell.BLACK;
    private JTextArea movesTextArea;
    private JScrollPane scrollPane;
    private Font customFont;
    private Font playerFont;
    ImageIcon purplePlayerIcon = new ImageIcon("resources/purplemove.png");
    ImageIcon bluePlayerIcon = new ImageIcon("resources/bluemove.png");
    int num = 1;

    public LocalGameVsHuman(ClientGui clientGui) {
        super("Local Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBackground(pink);
        setContentPane(layeredPane);

        JLabel gifLabel = new JLabel(new ImageIcon("resources/GBG.gif"));
        gifLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        layeredPane.add(gifLabel, Integer.valueOf(0));


        ImageIcon circleBIcon = new ImageIcon("resources/circleB1.png");
        JLabel circleBLabel = new JLabel(circleBIcon);
        int ciBX = 1750;
        int ciBY = 908;
        circleBLabel.setBounds(ciBX, ciBY, circleBIcon.getIconWidth(), circleBIcon.getIconHeight());
        layeredPane.add(circleBLabel, Integer.valueOf(1));

        ImageIcon circleIcon = new ImageIcon("resources/circle.gif");
        JLabel circleLabel = new JLabel(circleIcon);
        int ciX = 1734;
        int ciY = 889;
        circleLabel.setBounds(ciX, ciY, circleIcon.getIconWidth(), circleIcon.getIconHeight());
        layeredPane.add(circleLabel, Integer.valueOf(2));

        playIcon = new ImageIcon("resources/playPause.png");
        pauseIcon = new ImageIcon("resources/playPauseUP.png");

        ImageIcon PlayPauseIcon = new ImageIcon("resources/playPause.png");
        JLabel PlayPauseLabel = new JLabel(PlayPauseIcon);
        int ppaX = 1804;
        int ppaY = 935;
        PlayPauseLabel.setBounds(ppaX, ppaY, PlayPauseIcon.getIconWidth(), PlayPauseIcon.getIconHeight());

        PlayPauseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isPaused) {
                    newMusicPlayer.play();
                    PlayPauseLabel.setIcon(playIcon);
                } else {
                    newMusicPlayer.pause();
                    PlayPauseLabel.setIcon(pauseIcon);
                }
                isPaused = !isPaused;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isPaused) {
                    PlayPauseLabel.setIcon(pauseIcon);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isPaused) {
                    PlayPauseLabel.setIcon(playIcon);
                }
            }
        });

        layeredPane.add(PlayPauseLabel, Integer.valueOf(3));


        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setForeground(Color.BLUE);
        volumeSlider.setBackground(Color.BLUE);
        int vsX = 1784;
        int vsY = 1005;
        int vsW = 100;
        int vsH = 20;
        volumeSlider.setBounds(vsX, vsY, vsW, vsH);

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int volumeValue = volumeSlider.getValue();
                float volume = volumeValue / 100.0f;
                newMusicPlayer.setVolume(volume);
            }
        });
        layeredPane.add(volumeSlider, Integer.valueOf(3));

        JLabel currentPlayerLabel = new JLabel();

        currentPlayerLabel.setIcon(purplePlayerIcon);
        int cplX = 845;
        int cplY = 805;
        currentPlayerLabel.setBounds(cplX, cplY, purplePlayerIcon.getIconWidth(), purplePlayerIcon.getIconHeight());
        layeredPane.add(currentPlayerLabel, Integer.valueOf(3));

        if (currentPlayerColor == Cell.BLACK) {
            currentPlayerLabel.setIcon(bluePlayerIcon);
        } else {
            currentPlayerLabel.setIcon(purplePlayerIcon);
        }

        try {
            playerFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Visitor Rus.ttf"));
            playerFont = playerFont.deriveFont(Font.BOLD, 22);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Visitor Rus.ttf"));
            customFont = customFont.deriveFont(Font.BOLD | Font.ITALIC, 14);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        ImageIcon frameIcon = new ImageIcon("resources/frame.gif");
        JLabel frameLabel = new JLabel(frameIcon);
        int fX = 1380;
        int fY = 60;
        frameLabel.setBounds(fX, fY, frameIcon.getIconWidth(), frameIcon.getIconHeight());
        layeredPane.add(frameLabel, Integer.valueOf(2));

        ImageIcon playersIcon = new ImageIcon("resources/players.gif");
        JLabel playersLabel = new JLabel(playersIcon);
        int plX = 1570;
        int plY = 60;
        playersLabel.setBounds(plX, plY, playersIcon.getIconWidth(), playersIcon.getIconHeight());
        layeredPane.add(playersLabel, Integer.valueOf(3));

        ImageIcon purpleIcon = new ImageIcon("resources/purple.png");
        JLabel purpleLabel = new JLabel(purpleIcon);
        int pX = 1410;
        int pY = 100;
        purpleLabel.setBounds(pX, pY, purpleIcon.getIconWidth(), purpleIcon.getIconHeight());
        layeredPane.add(purpleLabel, Integer.valueOf(3));


        JLabel purpleInfoLabel = new JLabel("Player 1: Purple");
        purpleInfoLabel.setFont(playerFont);
        purpleInfoLabel.setForeground(Color.magenta);
        int infoX = pX + purpleIcon.getIconWidth();
        int infoY = pY - 4;
        purpleInfoLabel.setBounds(infoX, infoY, 200, 30);
        layeredPane.add(purpleInfoLabel, Integer.valueOf(3));


        ImageIcon blueIcon = new ImageIcon("resources/blue.png");
        JLabel blueLabel = new JLabel(blueIcon);
        int blX = 1660;
        int blY = 105;
        blueLabel.setBounds(blX, blY, blueIcon.getIconWidth(), blueIcon.getIconHeight());
        layeredPane.add(blueLabel, Integer.valueOf(3));

        JLabel blueInfoLabel = new JLabel("Player 2: Blue");
        blueInfoLabel.setFont(playerFont);
        blueInfoLabel.setForeground(Color.blue);
        int infoX2 = blX + blueIcon.getIconWidth() + 10;
        int infoY2 = blY - 9;
        blueInfoLabel.setBounds(infoX2, infoY2, 200, 30);
        layeredPane.add(blueInfoLabel, Integer.valueOf(3));


        movesTextArea = new JTextArea();
        movesTextArea.setEditable(true);
        movesTextArea.setLineWrap(true);
        movesTextArea.setWrapStyleWord(true);


        movesTextArea.setFont(customFont);
        scrollPane = new JScrollPane(movesTextArea);
        scrollPane.setBounds(1410, 130, 440, 400);
        layeredPane.add(scrollPane, Integer.valueOf(3));

        ImageIcon NewGameIcon = new ImageIcon("resources/newgame.png");
        JLabel NewGameLabel = new JLabel(NewGameIcon);
        int ngX = 845;
        int ngY = 180;
        NewGameLabel.setBounds(ngX, ngY, NewGameIcon.getIconWidth(), NewGameIcon.getIconHeight());

        NewGameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                MainGameWindow gameWindow = new MainGameWindow(clientGui);
                gameWindow.setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                NewGameLabel.setIcon(new ImageIcon("resources/newgameUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                NewGameLabel.setIcon(NewGameIcon);
            }
        });
        layeredPane.add(NewGameLabel, Integer.valueOf(1));

        ImageIcon SurrenderIcon = new ImageIcon("resources/surrender.png");
        JLabel SurrenderLabel = new JLabel(SurrenderIcon);
        int surX = 1020;
        int surY = 818;
        SurrenderLabel.setBounds(surX, surY, SurrenderIcon.getIconWidth(), SurrenderIcon.getIconHeight());

        SurrenderLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                SurrenderLabel.setIcon(new ImageIcon("resources/surrenderUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                SurrenderLabel.setIcon(SurrenderIcon);
            }
        });
        layeredPane.add(SurrenderLabel, Integer.valueOf(1));

        ImageIcon BackIcon = new ImageIcon("resources/backg.png");
        JLabel BackLabel = new JLabel(BackIcon);
        int backX = 700;
        int backY = 818;
        BackLabel.setBounds(backX, backY, BackIcon.getIconWidth(), BackIcon.getIconHeight());

        BackLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                LocalGameWindow localGameWindow = new LocalGameWindow(clientGui);
                localGameWindow.setVisible(true);
                LocalGameWindow.newMusicPlayer.stop();

                MainGameWindow.musicPlayer = new MusicPlayer("resources/retro.wav");
                MainGameWindow.musicPlayer.play();
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                BackLabel.setIcon(new ImageIcon("resources/backgUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                BackLabel.setIcon(BackIcon);
            }
        });
        layeredPane.add(BackLabel, Integer.valueOf(1));

        int boardSize = 8;
        buttons = new JButton[boardSize][boardSize];
        int buttonSize = 71;
        int boardWidth = boardSize * buttonSize;
        int boardHeight = boardSize * buttonSize;
        int boardX = (screenSize.width - boardWidth) / 2 - 11;
        int boardY = (screenSize.height - boardHeight) / 2 - 12;


        ImageIcon buttonNormalIcon = new ImageIcon("resources/button_normal.png");
        ImageIcon buttonHoverIcon = new ImageIcon("resources/button_hover.png");
        ImageIcon buttonPressedIcon = new ImageIcon("resources/button_pressed.png");


        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                JButton button = new JButton();
                button.setBounds(boardX + col * buttonSize, boardY + row * buttonSize, buttonSize, buttonSize);
                button.setRolloverIcon(buttonHoverIcon);
                button.setPressedIcon(buttonPressedIcon);
                buttons[row][col] = button;
                button.addActionListener(new BoardButtonListener(row, col, this));
                layeredPane.add(button, Integer.valueOf(1));
            }
        }
        board = new Board();
        startBoardPosition();

        setVisible(true);
    }


    private void startBoardPosition() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col].setBackground(null);
            }
        }
        buttons[3][3].setBackground(blue);
        buttons[4][4].setBackground(blue);
        buttons[3][4].setBackground(purple);
        buttons[4][3].setBackground(purple);
        buttons[2][3].setBackground(liteBlue);
        buttons[3][2].setBackground(liteBlue);
        buttons[4][5].setBackground(liteBlue);
        buttons[5][4].setBackground(liteBlue);
    }

    /**
     * Внутренний класс BoardButtonListener реализует слушатель для кнопок на игровой доске.
     * Обрабатывает ходы игрока и обновляет состояние игровой доски.
     */
    private class BoardButtonListener implements ActionListener {
        private final int row;
        private final int col;

        private LocalGameVsHuman lgvh;

        public BoardButtonListener(int row, int col, LocalGameVsHuman lgvh) {
            this.row = row;
            this.col = col;
            this.lgvh = lgvh;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!buttons[row][col].getBackground().equals(liteBlue)) {
                return;
            } else {
                board.placePiece(row, col, currentPlayerColor);
                movesTextArea.setText(movesTextArea.getText() + num++ + ". Player " + currentPlayerColor +
                        " placed his piece on " + (row + 1) + " " + (col + 1) + "\n");
                updateBoardColors(board, BOARD_SIZE, buttons);
                Cell opponent = currentPlayerColor == Cell.BLACK ? Cell.WHITE : Cell.BLACK;
                if (!board.getAllAvailableMoves(opponent).isEmpty()) {
                    currentPlayerColor = opponent;
                }
                final List<Move> availableMoves = board.getAllAvailableMoves(currentPlayerColor);
                for (Move m : availableMoves) {
                    buttons[m.row][m.col].setBackground(liteBlue);
                }
            }
            if ((board.getAllAvailableMoves(Cell.WHITE).isEmpty() &&
                    board.getAllAvailableMoves(Cell.BLACK).isEmpty())) {
                String winner = getWinnerString();
                JOptionPane.showMessageDialog(lgvh, winner);
            }
        }
    }

    /**
     * Метод getWinnerString() возвращает строку с информацией о победителе и общем счете.
     *
     * @return Строка с информацией о победителе и счете.
     */
    private String getWinnerString() {
        final int blackCount = board.getQuantityOfBlack();
        final int whiteCount = board.getQuantityOfWhite();
        String winner;
        if (blackCount > whiteCount) {
            winner = "Winner is black. Total score is ";

        } else if (whiteCount > blackCount) {
            winner = "Winner is white. Total score is ";

        } else {
            winner = "It's tie. Total score is ";
        }
        winner += blackCount + " - " + whiteCount;
        return winner;
    }

    /**
     * Метод playClickSound() воспроизводит звук при клике.
     */
    public void playClickSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("resources/click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод playTapSound() воспроизводит звук при наведении курсора на элемент.
     */
    public void playTapSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("resources/tap.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}