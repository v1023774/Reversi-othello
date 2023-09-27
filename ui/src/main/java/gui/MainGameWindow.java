package gui;

import guiClient.ClientGui;

import javax.swing.*;
import java.awt.*;

/**
 * Класс MainGameWindow представляет основное игровое окно.
 */
public class MainGameWindow extends JFrame {
    public static MusicPlayer musicPlayer;

    /**
     * Конструктор класса MainGameWindow.
     *
     * @param clientGui Объект клиентского GUI.
     */
    public MainGameWindow(ClientGui clientGui) {
        super("Reversi Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBackground(Color.BLACK);
        setContentPane(layeredPane);

        JLabel gifLabel = new JLabel(new ImageIcon("resources/retro.gif"));
        gifLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        layeredPane.add(gifLabel, Integer.valueOf(0));

        JButton backButton = new JButton("Back");
        backButton.setBounds(100, 100, 100, 50);
        layeredPane.add(backButton, Integer.valueOf(1));

        setVisible(true);
    }
}
