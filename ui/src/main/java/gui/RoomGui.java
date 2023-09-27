package gui;

import guiClient.ClientGui;
import logic.Board;
import logic.Cell;
import logic.Move;
import parsing.BoardParser;

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
import java.util.ArrayList;
import java.util.List;

import static gui.LocalGameWindow.newMusicPlayer;
import static guiClient.SendMethods.createJsonAndSendCommand;

public class RoomGui extends JFrame {
    public JLabel roomId = new JLabel();
    private JLabel blackPlayerId;
    private JLabel blackPlayerIdLabel;
    private JLabel blackPlayerName;
    private JLabel whitePlayerIdLabel;
    private JLabel whitePlayerId;
    private JLabel whitePlayerName;
    private JPanel boardPanel;
    private JLabel turnColor;
    private final int BOARD_SIZE = 8;
    public JButton[][] buttons;
    private int numberOfTurn;
    private Container container;

    private String winner;
    private JButton surrender;
    private JButton tie;

    Board board;

    public String playerColor;
    List<Move> moves;
    private Font customFont;
    public JLabel StartGameLabel;
    Color liteBlue = new Color(147, 227, 255);
    Color pink = new Color(234, 147, 255);
    Color purple = new Color(132, 2, 182);
    Color blue = new Color(27, 61, 182);
    private ImageIcon playIcon;
    private ImageIcon pauseIcon;
    private boolean isPaused = false;
    JScrollPane scrollPane;
    JTextArea movesTextArea;

    int num = 1;
    public RoomGui(ClientGui clientGui) {

        super("RoomGui");
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

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Visitor Rus.ttf"));
            customFont = customFont.deriveFont(Font.BOLD, 14);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

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

        int boardSize = 8;
        buttons = new JButton[boardSize][boardSize];
        int buttonSize = 71;
        int boardWidth = boardSize * buttonSize;
        int boardHeight = boardSize * buttonSize;
        int boardX = (screenSize.width - boardWidth) / 2 - 11;
        int boardY = (screenSize.height - boardHeight) / 2 - 12;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                JButton button = new JButton();
                button.setBounds(boardX + col * buttonSize, boardY + row * buttonSize, buttonSize, buttonSize);
                buttons[row][col] = button;
                buttons[row][col].setBackground(pink);
                button.addActionListener(new ButtonListener(row, col, clientGui, this));
                layeredPane.add(button, Integer.valueOf(1));
            }
        }

        ImageIcon StartGameIcon = new ImageIcon("resources/startgame.png");
        StartGameLabel = new JLabel(StartGameIcon);
        int sgX = 830;
        int sgY = 150;
        StartGameLabel.setBounds(sgX, sgY, StartGameIcon.getIconWidth(), StartGameIcon.getIconHeight());

        StartGameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                clientGui.roomGui = new RoomGui(clientGui);
                try {
                    createJsonAndSendCommand(clientGui, "STARTGAME " + roomId + " gui");
                } catch (IOException event) {
                    throw new RuntimeException(event);
                }
                clientGui.roomGui.setVisible(true);
                dispose();
                if(clientGui.roomGui.playerColor.equals("white")){
                    buttons[3][3].setBackground(blue);
                    buttons[4][4].setBackground(blue);
                    buttons[3][4].setBackground(purple);
                    buttons[4][3].setBackground(purple);
                }
                StartGameLabel.setEnabled(false);

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                StartGameLabel.setIcon(new ImageIcon("resources/startgameUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                StartGameLabel.setIcon(StartGameIcon);
            }
        });
        layeredPane.add(StartGameLabel, Integer.valueOf(1));

        ImageIcon SurrenderIcon = new ImageIcon("resources/surrender.png");
        JLabel SurrenderLabel = new JLabel(SurrenderIcon);
        int surX = 1020;
        int surY = 885;
        SurrenderLabel.setBounds(surX, surY, SurrenderIcon.getIconWidth(), SurrenderIcon.getIconHeight());

        SurrenderLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                try {
                    createJsonAndSendCommand(clientGui, "SURRENDER");
                } catch (IOException event) {
                    throw new RuntimeException(event);
                }
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

        ImageIcon TieIcon = new ImageIcon("resources/tie.png");
        JLabel TieLabel = new JLabel(TieIcon);
        int tiX = 700;
        int tiY = 883;
        TieLabel.setBounds(tiX, tiY, TieIcon.getIconWidth(), TieIcon.getIconHeight());

        TieLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();

                try {
                    createJsonAndSendCommand(clientGui, "");
                } catch (IOException event) {
                    throw new RuntimeException(event);
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                TieLabel.setIcon(new ImageIcon("resources/tieUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                TieLabel.setIcon(TieIcon);
            }
        });
        layeredPane.add(TieLabel, Integer.valueOf(1));


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
        int pY = 90;
        purpleLabel.setBounds(pX, pY, purpleIcon.getIconWidth(), purpleIcon.getIconHeight());
        layeredPane.add(purpleLabel, Integer.valueOf(3));

        ImageIcon blueIcon = new ImageIcon("resources/blue.png");
        JLabel blueLabel = new JLabel(blueIcon);
        int blX = 1650;
        int blY = 95;
        blueLabel.setBounds(blX, blY, blueIcon.getIconWidth(), blueIcon.getIconHeight());
        layeredPane.add(blueLabel, Integer.valueOf(3));

        ImageIcon BackIcon = new ImageIcon("resources/backg.png");
        JLabel BackLabel = new JLabel(BackIcon);
        int backX = 0;
        int backY = 0;
        BackLabel.setBounds(backX, backY, BackIcon.getIconWidth(), BackIcon.getIconHeight());

        BackLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                MainMenuWindow mainMenuWindow = new MainMenuWindow(clientGui);
                mainMenuWindow.setVisible(true);
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

        movesTextArea = new JTextArea();
        movesTextArea.setEditable(true);
        movesTextArea.setLineWrap(true);
        movesTextArea.setWrapStyleWord(true);


        movesTextArea.setFont(customFont);
        //movesTextArea.setForeground(Color.white);
        // Создание JScrollPane для movesTextArea
        scrollPane = new JScrollPane(movesTextArea);
        scrollPane.setBounds(1410, 130, 440, 400);
//        scrollPane.setOpaque(false);
//        scrollPane.getViewport().setOpaque(false);
        layeredPane.add(scrollPane, Integer.valueOf(3));


        blackPlayerId = new JLabel();
        blackPlayerName = new JLabel();
        whitePlayerId = new JLabel();
        whitePlayerName = new JLabel();

        setVisible(true);
        setVisible(true);
    }

    public void updateAvailableMoves(String availableMoves) {
        moves = new ArrayList<>();
        int nMoves = (availableMoves.length()) / 6;
        for (int i = 0; i < nMoves; i++) {

            int i1 = availableMoves.charAt(1 + i * 6) - '1';
            int i3 = availableMoves.charAt(3 + i * 6) - '1';

            buttons[i1][i3].setBackground(liteBlue);
            moves.add(new Move(i1,i3));
        }
    }

    public void updateBoard(String board) {
        this.board = BoardParser.parse(board, 'B', 'W', '-');
        System.out.println(board);
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (this.board.get(row, col).equals(Cell.BLACK)) {
                    buttons[row][col].setBackground(purple);
                } else if (this.board.get(row, col).equals(Cell.WHITE)) {
                    buttons[row][col].setBackground(blue);
                } else {
                    buttons[row][col].setBackground(null);
                }
            }
        }
    }

    private class ButtonListener implements ActionListener {
        private final int row;
        private final int col;
        private ClientGui clientGui;

        RoomGui roomGui;

        public ButtonListener(int row, int col, ClientGui clientGui, RoomGui roomGui) {
            this.row = row;
            this.col = col;
            this.clientGui = clientGui;
            this.roomGui = roomGui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(!buttons[row][col].getBackground().equals(liteBlue)){
                    JOptionPane.showMessageDialog(roomGui, "wrong move");
                    return;
                }
                createJsonAndSendCommand(clientGui, "MAKEMOVE " + (row + 1) + " " + (col + 1));

                System.out.println(playerColor);
                board.placePiece(row, col, playerColor.equals("black")? Cell.BLACK : Cell.WHITE);
                movesTextArea.setText(movesTextArea.getText() + num++ + ". Player " + "blackPlayerName.getText()" +
                        " placed his piece on " + (row + 1) + " " + (col + 1) + "\n");
                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int col = 0; col < BOARD_SIZE; col++) {
                        if (board.get(row, col).equals(Cell.BLACK)) {
                            buttons[row][col].setBackground(purple);
                        } else if (board.get(row, col).equals(Cell.WHITE)) {
                            buttons[row][col].setBackground(blue);
                        } else {
                            buttons[row][col].setBackground(null);
                        }
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

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
