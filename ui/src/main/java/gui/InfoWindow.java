package gui;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InfoWindow extends JDialog {
    /**
     * Класс InfoWindow представляет диалоговое окно, которое отображает информацию из PDF-файла.
     * Он загружает документ PDF, отображает его страницы в виде изображений и выводит их в окне диалога.
     *
     * @param parent Родительское окно, из которого вызывается диалоговое окно информации.
     */
    public InfoWindow(Frame parent) {
        super(parent, "Information", true);
        setSize(630, 900);

        JPanel mainPanel = new JPanel(new BorderLayout());

        String pdfFilePath = "resources/Reversi.pdf";

        try {
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            JPanel pdfPanel = new JPanel();
            pdfPanel.setLayout(new BoxLayout(pdfPanel, BoxLayout.Y_AXIS));

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = pdfRenderer.renderImage(page);
                JLabel label = new JLabel(new ImageIcon(image));
                pdfPanel.add(label);
            }

            document.close();

            JScrollPane scrollPane = new JScrollPane(pdfPanel);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            closeButton.setFocusPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setOpaque(true);
            closeButton.setFont(new Font("Arial", Font.BOLD, 14));
            closeButton.setForeground(Color.WHITE);
            closeButton.setBackground(Color.BLACK);
            closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            closeButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    closeButton.setBackground(Color.DARK_GRAY);
                }

                public void mouseExited(MouseEvent evt) {
                    closeButton.setBackground(Color.BLACK);
                }
            });

            mainPanel.add(closeButton, BorderLayout.SOUTH);

            add(mainPanel);
            setLocationRelativeTo(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
