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
 * Класс MainStartWindow представляет собой главное окно запуска игры.
 */
public class MainStartWindow extends JFrame {
    /**
     * Конструирует объект MainStartWindow с заданным clientGui.
     *
     * @param clientGui Объект clientGui, связанный с окном.
     */
    public MainStartWindow(ClientGui clientGui) {
        super("Main Start");
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

        JLabel additionalGifLabel = new JLabel(new ImageIcon("resources/AF1.gif"));
        additionalGifLabel.setBounds(0, -200, screenSize.width, screenSize.height);
        layeredPane.add(additionalGifLabel, Integer.valueOf(1));


        ImageIcon StartIcon = new ImageIcon("resources/st.png");
        JLabel StartLabel = new JLabel(StartIcon);
        int StartX = (screenSize.width - StartIcon.getIconWidth()) / 2;
        int StartY = 600;
        StartLabel.setBounds(StartX, StartY, StartIcon.getIconWidth(), StartIcon.getIconHeight());

        StartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                MainMenuWindow gameWindow = new MainMenuWindow(clientGui);
                gameWindow.setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                StartLabel.setIcon(new ImageIcon("resources/str.png"));
                playTapSound();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                StartLabel.setIcon(StartIcon);
            }
        });


        layeredPane.add(StartLabel, Integer.valueOf(1));
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
