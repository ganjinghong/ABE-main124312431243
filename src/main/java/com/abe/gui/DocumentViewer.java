package com.abe.gui;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DocumentViewer extends JFrame {
    private JPanel documentPanel;

    private JButton prevButton;
    private JButton nextButton;

    private JLabel pageLabel;

    private int displayWidth;
    private int displayHeight;
    private int currentPage = 0;
    private int pageCount = 0;
    private PDDocument document;
    private PDFRenderer renderer;

    public DocumentViewer(int width, int height) {
        setTitle("文档查看器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        documentPanel = new JPanel();
        add(documentPanel, BorderLayout.CENTER);

        prevButton = new JButton("上一页");
        nextButton = new JButton("下一页");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        pageLabel = new JLabel();
        buttonPanel.add(pageLabel);
        add(buttonPanel, BorderLayout.SOUTH);

        prevButton.addActionListener(e -> showPreviousPage());

        nextButton.addActionListener(e -> showNextPage());

        displayWidth = width;
        displayHeight = height;
        setSize(displayWidth, displayHeight);
        setLocationRelativeTo(null);
    }

    public void showPDF(String filePath) {
        try {
            document = PDDocument.load(new File(filePath));
            renderer = new PDFRenderer(document);

            pageCount = document.getNumberOfPages();
            showPage(currentPage);

            revalidate();
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPage(int pageIndex) {
        try {
            BufferedImage image = renderer.renderImageWithDPI(pageIndex, 200);

            // 缩放图像以适应展示大小
            Image scaledImage = image.getScaledInstance(displayWidth, displayHeight - 100, Image.SCALE_SMOOTH);

            documentPanel.removeAll();

            ImageIcon icon = new ImageIcon(scaledImage);
            JLabel label = new JLabel(icon);
            documentPanel.add(label);

            currentPage = pageIndex;
            pageLabel.setText("当前页码: " + (currentPage + 1) + " / " + pageCount);
            revalidate();
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNextPage() {
        if (currentPage < pageCount - 1) {
            currentPage++;
            showPage(currentPage);
        } else {
            JOptionPane.showMessageDialog(this, "已经是最后一页了");
        }
    }

    public void showPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            showPage(currentPage);
        } else {
            JOptionPane.showMessageDialog(this, "已经是第一页了");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int displayWidth = 1200; // 设置展示宽度
            int displayHeight = 800; // 设置展示高度

            DocumentViewer viewer = new DocumentViewer(displayWidth, displayHeight);
            viewer.setVisible(true);
            viewer.showPDF("/Users/zhangxi/Downloads/WS23_IPCP_Lecture07_CNNs.pdf");
        });
    }
}