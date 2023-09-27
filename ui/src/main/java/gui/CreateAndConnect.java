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
import java.io.IOException;

import static gui.MainGameWindow.musicPlayer;
import static guiClient.SendMethods.createJsonAndSendCommand;

/**
 * Класс CreateAndConnect представляет собой окно для создания и подключения к комнате в игре.
 * Он содержит элементы управления для создания комнаты и подключения к существующей комнате.
 */
public class CreateAndConnect extends JFrame {
    public JLabel ConnectLabelError;
    JTextField ConnectText;
    private Font customFont;

    /**
     * Конструктор класса CreateAndConnect.
     *
     * @param clientGui Объект класса clientGui, предоставляющий функциональность клиента.
     */
    public CreateAndConnect(ClientGui clientGui) {
        super("CreateAndConnect");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Realpolitik3D.otf"));
            customFont = customFont.deriveFont(Font.BOLD | Font.ITALIC, 24);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }


        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBackground(Color.BLACK);
        setContentPane(layeredPane);

        JLabel gifLabel = new JLabel(new ImageIcon("resources/game.gif"));
        gifLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        layeredPane.add(gifLabel, Integer.valueOf(0));


        ImageIcon CreateIcon = new ImageIcon("resources/createroomUP.png");
        JLabel CreateLabel = new JLabel(CreateIcon);
        int cX = 1190;
        int cY = 450;
        CreateLabel.setBounds(cX, cY, CreateIcon.getIconWidth(), CreateIcon.getIconHeight());

        CreateLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
                try {
                    createJsonAndSendCommand(clientGui, "CREATEROOM ");
                } catch (IOException event) {
                    throw new RuntimeException(event);
                }
                musicPlayer.stop();
                LocalGameWindow.newMusicPlayer = new MusicPlayer("resources/game.wav");
                LocalGameWindow.newMusicPlayer.play();
                clientGui.roomGui = new RoomGui(clientGui);
                clientGui.roomGui.setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                CreateLabel.setIcon(new ImageIcon("resources/createroom.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                CreateLabel.setIcon(CreateIcon);
            }
        });
        layeredPane.add(CreateLabel, Integer.valueOf(1));


        ImageIcon CreateInfoIcon = new ImageIcon("resources/press.png");
        JLabel CreateInfoLabel = new JLabel(CreateInfoIcon);
        int ciX = 750;
        int ciY = 385;
        CreateInfoLabel.setBounds(ciX, ciY, CreateInfoIcon.getIconWidth(), CreateInfoIcon.getIconHeight());
        layeredPane.add(CreateInfoLabel, Integer.valueOf(1));


        ImageIcon OrIcon = new ImageIcon("resources/OR.png");
        JLabel OrLabel = new JLabel(OrIcon);
        int orX = 900;
        int orY = 490;
        OrLabel.setBounds(orX, orY, OrIcon.getIconWidth(), OrIcon.getIconHeight());
        layeredPane.add(OrLabel, Integer.valueOf(1));


        ImageIcon ConnectIcon = new ImageIcon("resources/connectroomUP.png");
        JLabel ConnectLabel = new JLabel(ConnectIcon);
        int cnX = 1190;
        int cnY = 684;
        ConnectLabel.setBounds(cnX, cnY, ConnectIcon.getIconWidth(), ConnectIcon.getIconHeight());

        ConnectLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();

                try {
                    createJsonAndSendCommand(clientGui, "CONNECTTOROOM " + ConnectText.getText());
                } catch (IOException event) {
                    throw new RuntimeException(event);
                }
                musicPlayer.stop();
                LocalGameWindow.newMusicPlayer = new MusicPlayer("resources/game.wav");
                LocalGameWindow.newMusicPlayer.play();
                new RoomGui(clientGui).setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playTapSound();
                ConnectLabel.setIcon(new ImageIcon("resources/connectroom.png"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ConnectLabel.setIcon(ConnectIcon);
            }
        });
        layeredPane.add(ConnectLabel, Integer.valueOf(1));

        ImageIcon ConnectInfoIcon = new ImageIcon("resources/roomid.png");
        JLabel ConnectInfoLabel = new JLabel(ConnectInfoIcon);
        int cniX = 780;
        int cniY = 620;
        ConnectInfoLabel.setBounds(cniX, cniY, ConnectInfoIcon.getIconWidth(), ConnectInfoIcon.getIconHeight());
        layeredPane.add(ConnectInfoLabel, Integer.valueOf(1));

        ConnectText = new JTextField(10);
        ConnectText.setFont(customFont);
        ConnectText.setForeground(Color.CYAN);
        int ctx = 740;
        int cty = 696;
        int ctwidth = 400;
        int ctheight = 42;
        ConnectText.setBounds(ctx, cty, ctwidth, ctheight);
        ConnectText.setBackground(Color.BLACK);
        layeredPane.add(ConnectText, Integer.valueOf(1));
        ConnectLabelError = new JLabel();
        ConnectLabelError.setVisible(false);

        setVisible(true);
    }
    /**
     * Воспроизводит звук щелчка.
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
     * Воспроизводит звук нажатия.
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
