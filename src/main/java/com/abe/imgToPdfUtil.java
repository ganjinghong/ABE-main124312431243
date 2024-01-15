package com.abe;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class imgToPdfUtil {

    public static void imgToPdf(String imgPath, String pdfPath){
        try {
            BufferedImage img = ImageIO.read(new File(imgPath));
            FileOutputStream fos = new FileOutputStream(pdfPath);
            Document doc = new Document(null, 0, 0, 0, 0);
            doc.setPageSize(new Rectangle(img.getWidth(), img.getHeight()));
            Image image = Image.getInstance(imgPath);
            PdfWriter.getInstance(doc, fos);
            doc.open();
            doc.add(image);
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws BadElementException {
        String imagePath = "C:\\Users\\57802\\Desktop\\11.png";
        String pdfPath = "C:\\Users\\57802\\Desktop\\11.pdf";
        imgToPdf(imagePath, pdfPath);
    }
}