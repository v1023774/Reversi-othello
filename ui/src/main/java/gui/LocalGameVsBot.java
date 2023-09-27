package gui;

import guiClient.ClientGui;
import logic.*;

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
import java.util.List;

import static gui.LocalGameBotVsBot.updateBoardColors;
import static gui.LocalGameWindow.newMusicPlayer;

/**
 * Класс LocalGameBotVsBot представляет собой окно для локальной игры между человеком и ботом.
 * Он содержит игровое поле, кнопки управления звуком и другие элементы интерфейса.
 */
public class LocalGameVsBot extends JFrame {
    private final int BOARD_SIZE = 8;
    private final JButton[][] buttons;
    private Board board;
    Color liteBlue = new Color(147, 227, 255);
    Color pink = new Color(234, 147, 255);
    Color purple = new Color(132, 2, 182);
    Color blue = new Color(27, 61, 182);
    Cell playerColor = Cell.WHITE;
    Player bot;

    private ImageIcon playIcon;
    private ImageIcon pauseIcon;
    private boolean isPaused = false;

    public LocalGameVsBot(ClientGui clientGui) {
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

        ImageIcon frameIcon = new ImageIcon("resources/frame.gif");
        JLabel frameLabel = new JLabel(frameIcon);
        int fX = 1380;
        int fY = 60;
        frameLabel.setBounds(fX, fY, frameIcon.getIconWidth(), frameIcon.getIconHeight());
        layeredPane.add(frameLabel, Integer.valueOf(2));

        ImageIcon selectBotIcon = new ImageIcon("resources/botselect.png");
        JLabel selectBotLabel = new JLabel(selectBotIcon);
        int sbX = 130;
        int sbY = 130;
        selectBotLabel.setBounds(sbX, sbY, selectBotIcon.getIconWidth(), selectBotIcon.getIconHeight());
        layeredPane.add(selectBotLabel, Integer.valueOf(1));

        ImageIcon colorIcon = new ImageIcon("resources/color.png");
        JLabel colorLabel = new JLabel(colorIcon);
        int ccX = 100;
        int ccY = 490;
        colorLabel.setBounds(ccX, ccY, colorIcon.getIconWidth(), colorIcon.getIconHeight());
        layeredPane.add(colorLabel, Integer.valueOf(1));

        ImageIcon playersIcon = new ImageIcon("resources/players.gif");
        JLabel playersLabel = new JLabel(playersIcon);
        int plX = 1570;
        int plY = 60;
        playersLabel.setBounds(plX, plY, playersIcon.getIconWidth(), playersIcon.getIconHeight());
        layeredPane.add(playersLabel, Integer.valueOf(3));

        ImageIcon purpleIcon = new ImageIcon("resources/purple.png");
        JLabel purpleLabel = new JLabel(purpleIcon);
        int pX = 1410;
        int pY = 90;
        purpleLabel.setBounds(pX, pY, purpleIcon.getIconWidth(), purpleIcon.getIconHeight());
        layeredPane.add(purpleLabel, Integer.valueOf(3));

        ImageIcon blueIcon = new ImageIcon("resources/blue.png");
        JLabel blueLabel = new JLabel(blueIcon);
        int blX = 1650;
        int blY = 95;
        blueLabel.setBounds(blX, blY, blueIcon.getIconWidth(), blueIcon.getIconHeight());
        layeredPane.add(blueLabel, Integer.valueOf(3));

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


        ImageIcon setPurpleIcon = new ImageIcon("resources/setPurple.png");
        JLabel setPurpleLabel = new JLabel(setPurpleIcon);
        int sPX = 180;
        int sPY = 550;
        setPurpleLabel.setBounds(sPX, sPY, setPurpleIcon.getIconWidth(), setPurpleIcon.getIconHeight());
        setPurpleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                setPurpleLabel.setIcon(new ImageIcon("resources/setPurpleUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setPurpleLabel.setIcon(setPurpleIcon);
            }
        });
        layeredPane.add(setPurpleLabel, Integer.valueOf(1));


        ImageIcon setBlueIcon = new ImageIcon("resources/setBlue.png");
        JLabel setBlueLabel = new JLabel(setBlueIcon);
        int sBX = 194;
        int sBY = 590;
        setBlueLabel.setBounds(sBX, sBY, setBlueIcon.getIconWidth(), setBlueIcon.getIconHeight());
        setBlueLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                setBlueLabel.setIcon(new ImageIcon("resources/setBlueUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBlueLabel.setIcon(setBlueIcon);
            }
        });
        layeredPane.add(setBlueLabel, Integer.valueOf(1));


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


        ImageIcon easy2xIcon = new ImageIcon("resources/easy2x.png");
        JLabel easy2xLabel = new JLabel(easy2xIcon);
        int exX = 185;
        int exY = 200;
        easy2xLabel.setBounds(exX, exY, easy2xIcon.getIconWidth(), easy2xIcon.getIconHeight());

        easy2xLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();


            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                easy2xLabel.setIcon(new ImageIcon("resources/easy2xUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                easy2xLabel.setIcon(easy2xIcon);
            }
        });
        layeredPane.add(easy2xLabel, Integer.valueOf(1));


        ImageIcon easyIcon = new ImageIcon("resources/easy.png");
        JLabel easyLabel = new JLabel(easyIcon);
        int eX = 195;
        int eY = 246;
        easyLabel.setBounds(eX, eY, easyIcon.getIconWidth(), easy2xIcon.getIconHeight());

        easyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();


            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                easyLabel.setIcon(new ImageIcon("resources/easyUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                easyLabel.setIcon(easyIcon);
            }
        });
        layeredPane.add(easyLabel, Integer.valueOf(1));


        ImageIcon mediumIcon = new ImageIcon("resources/medium.png");
        JLabel mediumLabel = new JLabel(mediumIcon);
        int mX = 192;
        int mY = 300;
        mediumLabel.setBounds(mX, mY, mediumIcon.getIconWidth(), mediumIcon.getIconHeight());

        mediumLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();


            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                mediumLabel.setIcon(new ImageIcon("resources/mediumUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mediumLabel.setIcon(mediumIcon);
            }
        });
        layeredPane.add(mediumLabel, Integer.valueOf(1));

        ImageIcon hardIcon = new ImageIcon("resources/hard.png");
        JLabel hardLabel = new JLabel(hardIcon);
        int hX = 196;
        int hY = 347;
        hardLabel.setBounds(hX, hY, hardIcon.getIconWidth(), hardIcon.getIconHeight());

        hardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();


            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                hardLabel.setIcon(new ImageIcon("resources/hardUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hardLabel.setIcon(hardIcon);
            }
        });
        layeredPane.add(hardLabel, Integer.valueOf(1));

        ImageIcon killerIcon = new ImageIcon("resources/killer.png");
        JLabel killerLabel = new JLabel(killerIcon);
        int kX = 190;
        int kY = 398;
        killerLabel.setBounds(kX, kY, killerIcon.getIconWidth(), killerIcon.getIconHeight());

        killerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();


            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                killerLabel.setIcon(new ImageIcon("resources/killerUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                killerLabel.setIcon(killerIcon);
            }
        });
        layeredPane.add(killerLabel, Integer.valueOf(1));


        ImageIcon murdererIcon = new ImageIcon("resources/murder.png");
        JLabel murdererLabel = new JLabel(murdererIcon);
        int mrX = 165;
        int mrY = 442;
        murdererLabel.setBounds(mrX, mrY, murdererIcon.getIconWidth(), murdererIcon.getIconHeight());

        murdererLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();


            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                murdererLabel.setIcon(new ImageIcon("resources/murderUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                murdererLabel.setIcon(murdererIcon);
            }
        });
        layeredPane.add(murdererLabel, Integer.valueOf(2));


        int boardSize = 8;
        buttons = new JButton[boardSize][boardSize];
        int buttonSize = 71;
        int boardWidth = boardSize * buttonSize;
        int boardHeight = boardSize * buttonSize;
        int boardX = (screenSize.width - boardWidth) / 2 - 11;
        int boardY = (screenSize.height - boardHeight) / 2 - 12;

        Color buttonsBackground = new Color(250, 98, 236);

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


        bot = new AIBotIlya(playerColor.reverse(), 2);

        if (playerColor.equals(Cell.WHITE)) {

            Move move = bot.makeMove(board.getBoardCopy());
            board.placePiece(move.row, move.col, playerColor.reverse());
            updateBoardColors(board, BOARD_SIZE, buttons);
            for (Move m : board.getAllAvailableMoves(playerColor)) {
                buttons[m.row][m.col].setBackground(liteBlue);
            }
        }
        setVisible(true);
    }

    /**
     * Метод startBoardPosition() устанавливает начальную позицию игровой доски, расставляя фишки игроков.
     * Также устанавливает фон кнопок на начальное состояние.
     */
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
     * Обрабатывает ходы игрока и бота.
     */
    private class BoardButtonListener implements ActionListener {
        private final int row;
        private final int col;

        private LocalGameVsBot lgvb;

        public BoardButtonListener(int row, int col, LocalGameVsBot lgvb) {
            this.row = row;
            this.col = col;
            this.lgvb = lgvb;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!buttons[row][col].getBackground().equals(liteBlue)) {
                return;
            } else {
                board.placePiece(row, col, playerColor);
                updateBoardColors(board, BOARD_SIZE, buttons);

                while (true) {
                    Move move = bot.makeMove(board);
                    board.placePiece(move.row, move.col, playerColor.reverse());
                    updateBoardColors(board, BOARD_SIZE, buttons);
                    List<Move> availableMoves = board.getAllAvailableMoves(playerColor);
                    if (!availableMoves.isEmpty()) {
                        for (Move m : availableMoves) {
                            buttons[m.row][m.col].setBackground(liteBlue);
                        }
                        break;
                    }
                    if ((board.getAllAvailableMoves(Cell.WHITE).isEmpty() &&
                            board.getAllAvailableMoves(Cell.BLACK).isEmpty())) {
                        String winner = getWinnerString();
                        JOptionPane.showMessageDialog(lgvb, winner);
                        return;
                    }
                }
            }
            if ((board.getAllAvailableMoves(Cell.WHITE).isEmpty() &&
                    board.getAllAvailableMoves(Cell.BLACK).isEmpty())) {
                String winner = getWinnerString();
                JOptionPane.showMessageDialog(lgvb, winner);
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
