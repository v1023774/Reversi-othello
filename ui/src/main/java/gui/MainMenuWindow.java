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

/**
 * Класс MainMenuWindow представляет главное меню игры.
 */
public class MainMenuWindow extends JFrame {
    /**
     * Конструктор класса MainMenuWindow.
     *
     * @param clientGui Объект клиентского GUI.
     */
    public MainMenuWindow(ClientGui clientGui) {
        super("Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);


        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        JLabel gifLabel = new JLabel(new ImageIcon("resources/A3.gif"));
        gifLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        layeredPane.add(gifLabel, Integer.valueOf(0));

        JLabel menuLabel = new JLabel(new ImageIcon("resources/menu.png"));
        menuLabel.setBounds(10, -170, screenSize.width, screenSize.height);
        layeredPane.add(menuLabel, Integer.valueOf(1));

        ImageIcon MultiplayerIcon = new ImageIcon("resources/multiplayer.png");
        JLabel MultiplayerLabel = new JLabel(MultiplayerIcon);
        int multiplayerX = (screenSize.width - MultiplayerIcon.getIconWidth()) / 2 + 10;
        int multiplayerY = 460;
        MultiplayerLabel.setBounds(multiplayerX, multiplayerY, MultiplayerIcon.getIconWidth(), MultiplayerIcon.getIconHeight());

        MultiplayerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                clientGui.regAndAuth = new RegAndAuth(clientGui);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                MultiplayerLabel.setIcon(new ImageIcon("resources/multiplayerUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                MultiplayerLabel.setIcon(MultiplayerIcon);
            }
        });
        layeredPane.add(MultiplayerLabel, Integer.valueOf(1));

        ImageIcon localGameIcon = new ImageIcon("resources/localgame.png");
        JLabel localGameLabel = new JLabel(localGameIcon);

        int localgameX = (screenSize.width - localGameIcon.getIconWidth()) / 2;
        int localgameY = 530;
        localGameLabel.setBounds(localgameX, localgameY, localGameIcon.getIconWidth(), localGameIcon.getIconHeight());

        localGameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                LocalGameWindow localGameWindow = new LocalGameWindow(clientGui);
                localGameWindow.setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                localGameLabel.setIcon(new ImageIcon("resources/localgameUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                localGameLabel.setIcon(localGameIcon);
            }
        });
        layeredPane.add(localGameLabel, Integer.valueOf(1));

        ImageIcon InfoIcon = new ImageIcon("resources/info.png");
        JLabel InfoLabel = new JLabel(InfoIcon);
        int infoX = (screenSize.width - localGameIcon.getIconWidth()) / 2;
        int infoY = 590;
        InfoLabel.setBounds(infoX, infoY, InfoIcon.getIconWidth(), InfoIcon.getIconHeight());

        InfoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                InfoWindow infoWindow = new InfoWindow(MainMenuWindow.this);
                infoWindow.setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                InfoLabel.setIcon(new ImageIcon("resources/infoUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                InfoLabel.setIcon(InfoIcon);
            }
        });
        layeredPane.add(InfoLabel, Integer.valueOf(1));


        ImageIcon quitGameIcon = new ImageIcon("resources/quit.png");
        JLabel quitGameLabel = new JLabel(quitGameIcon);
        int quitX = (screenSize.width - localGameIcon.getIconWidth()) / 2;
        int quitY = 650;
        quitGameLabel.setBounds(quitX, quitY, quitGameIcon.getIconWidth(), quitGameIcon.getIconHeight());

        quitGameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                quitGameLabel.setIcon(new ImageIcon("resources/quitUP.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                quitGameLabel.setIcon(quitGameIcon);
            }
        });
        layeredPane.add(quitGameLabel, Integer.valueOf(1));


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
