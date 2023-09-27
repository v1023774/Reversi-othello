package gui;

import guiClient.ClientGui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static gui.MainGameWindow.musicPlayer;
/**
 * Класс LocalGameWindow представляет окно для выбора локальной игры с разными режимами.
 */
public class LocalGameWindow extends JFrame {
    static MusicPlayer newMusicPlayer;
    /**
     * Конструктор класса LocalGameWindow.
     * @param clientGui Объект клиентского GUI.
     */
    public LocalGameWindow(ClientGui clientGui) {
        super("Local Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBackground(Color.BLACK);
        setContentPane(layeredPane);

        JLabel gifLabel = new JLabel(new ImageIcon("resources/A3.gif"));
        gifLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        layeredPane.add(gifLabel, Integer.valueOf(0));

        JLabel menuLabel = new JLabel(new ImageIcon("resources/menu.png"));
        menuLabel.setBounds(10, -170, screenSize.width, screenSize.height);
        layeredPane.add(menuLabel, Integer.valueOf(1));


        ImageIcon PlayerVsPlayerIcon = new ImageIcon("resources/pvp.png");
        JLabel PlayerVsPlayerLabel = new JLabel(PlayerVsPlayerIcon);
        int pvpX = (screenSize.width - PlayerVsPlayerIcon.getIconWidth()) / 2 + 10;
        int pvpY = 500;
        PlayerVsPlayerLabel.setBounds(pvpX, pvpY, PlayerVsPlayerIcon.getIconWidth(), PlayerVsPlayerIcon.getIconHeight());

        PlayerVsPlayerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                musicPlayer.stop();
                LocalGameVsHuman localGameVsHuman = new LocalGameVsHuman(clientGui);
                localGameVsHuman.setVisible(true);

                LocalGameWindow.newMusicPlayer = new MusicPlayer("resources/game.wav");
                LocalGameWindow.newMusicPlayer.play();

                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                PlayerVsPlayerLabel.setIcon(new ImageIcon("resources/pvpUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                PlayerVsPlayerLabel.setIcon(PlayerVsPlayerIcon);
            }
        });
        layeredPane.add(PlayerVsPlayerLabel, Integer.valueOf(1));


        ImageIcon PlayerVsBotIcon = new ImageIcon("resources/pve.png");
        JLabel PlayerVsBotLabel = new JLabel(PlayerVsBotIcon);
        int pvbX = (screenSize.width - PlayerVsBotIcon.getIconWidth()) / 2 + 10;
        int pvbY = 590;
        PlayerVsBotLabel.setBounds(pvbX, pvbY, PlayerVsBotIcon.getIconWidth(), PlayerVsBotIcon.getIconHeight());

        PlayerVsBotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                musicPlayer.stop();
                LocalGameVsBot localGameVsBot = new LocalGameVsBot(clientGui);
                localGameVsBot.setVisible(true);

                LocalGameWindow.newMusicPlayer = new MusicPlayer("resources/game.wav");
                LocalGameWindow.newMusicPlayer.play();
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                PlayerVsBotLabel.setIcon(new ImageIcon("resources/pveUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                PlayerVsBotLabel.setIcon(PlayerVsBotIcon);
            }
        });
        layeredPane.add(PlayerVsBotLabel, Integer.valueOf(1));

        ImageIcon WatchBotVsBotIcon = new ImageIcon("resources/bvb.png");
        JLabel WatchBotVsBotLabel = new JLabel(WatchBotVsBotIcon);
        int wbvbX = (screenSize.width - WatchBotVsBotIcon.getIconWidth()) / 2 + 10;
        int wbvbY = 680;
        WatchBotVsBotLabel.setBounds(wbvbX, wbvbY, WatchBotVsBotIcon.getIconWidth(), WatchBotVsBotIcon.getIconHeight());

        WatchBotVsBotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                musicPlayer.stop();
                LocalGameBotVsBot localGameBotVsBot = new LocalGameBotVsBot(clientGui);
                localGameBotVsBot.setVisible(true);

                LocalGameWindow.newMusicPlayer = new MusicPlayer("resources/game.wav");
                LocalGameWindow.newMusicPlayer.play();
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                WatchBotVsBotLabel.setIcon(new ImageIcon("resources/bvbUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                WatchBotVsBotLabel.setIcon(WatchBotVsBotIcon);
            }
        });
        layeredPane.add(WatchBotVsBotLabel, Integer.valueOf(1));

        ImageIcon BackIcon = new ImageIcon("resources/back.png");
        JLabel BackLabel = new JLabel(BackIcon);
        int backX = (screenSize.width - BackIcon.getIconWidth()) / 2 + 10;
        int backY = 770;
        BackLabel.setBounds(backX, backY, BackIcon.getIconWidth(), BackIcon.getIconHeight());

        BackLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                MainMenuWindow gameWindow = new MainMenuWindow(clientGui);
                gameWindow.setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                BackLabel.setIcon(new ImageIcon("resources/backUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                BackLabel.setIcon(BackIcon);
            }
        });
        layeredPane.add(BackLabel, Integer.valueOf(1));

        setVisible(true);
    }

    /**
     * Метод для воспроизведения звука при клике.
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
     * Метод для воспроизведения звука при наведении курсора на элемент.
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
