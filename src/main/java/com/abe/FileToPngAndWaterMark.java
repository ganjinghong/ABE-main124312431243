package com.abe;

import com.abe.util.StreamUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;
import com.liumapp.workable.converter.WorkableConverter;
import com.liumapp.workable.converter.core.ConvertPattern;
import com.liumapp.workable.converter.factory.CommonConverterManager;
import com.liumapp.workable.converter.factory.ConvertPatternManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jodconverter.document.DefaultDocumentFormatRegistry;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author zhangxi
 */
public class FileToPngAndWaterMark {

    public static void wordToPdf(String wordUrl, String pdfUrl)throws Exception{
        WorkableConverter converter = new WorkableConverter();//实例化的同时，初始化配置项，配置项的校验通过Decorator装饰

        ConvertPattern pattern = ConvertPatternManager.getInstance();
        pattern.fileToFile(wordUrl, pdfUrl); //待转换文件路径，转换结果存储路径
        pattern.setSrcFilePrefix(DefaultDocumentFormatRegistry.DOC);
        pattern.setDestFilePrefix(DefaultDocumentFormatRegistry.PDF);

        converter.setConverterType(CommonConverterManager.getInstance());//策略模式，后续实现了新的转换策略后，在此处更换，图片转换将考虑使用新的策略来完成
        converter.convert(pattern.getParameter());
    }

    public static void addWaterMark(String srcPdfPath,String tarPdfPath,String waterMarkContent)throws Exception {
        PdfReader reader = new PdfReader(srcPdfPath);
        PdfStamper stamper = new PdfStamper(reader, Files.newOutputStream(Paths.get(tarPdfPath)));
        PdfGState gs = new PdfGState();
        BaseFont font =  BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        gs.setFillOpacity(0.4f);// 设置透明度

        int total = reader.getNumberOfPages() + 1;
        PdfContentByte content;
        for (int i = 1; i < total; i++) {
            content = stamper.getOverContent(i);
            content.beginText();
            content.setGState(gs);
            content.setColorFill(BaseColor.DARK_GRAY); //水印颜色
            Random random = new Random();
            float number = random.nextFloat();
            number *= 500;

            content.setFontAndSize(font, number); //水印字体样式和大小
            content.showTextAligned(Element.ALIGN_CENTER,waterMarkContent, number, number, 30); //水印内容和水印位置
            content.endText();
        }
        stamper.close();

    }

    public static void pdfFileToImage(File pdffile, String targetPath, int heightOffset) {
        FileInputStream instream =null;
        FileOutputStream fops = null;
        try {
            instream = new FileInputStream(pdffile);
            InputStream byteInputStream = null;
            try {
                PDDocument doc = PDDocument.load(instream);
                PDFRenderer renderer = new PDFRenderer(doc);
                int pageCount = doc.getNumberOfPages();
                List<BufferedImage> list = new ArrayList<>();
                if (pageCount > 0) {

                    int totalHeight = 0;
                    int width = 0;

                    for (int i = 0; i < pageCount; i++) {
                        BufferedImage image = renderer.renderImage(i, 1.25f);
                        list.add(image);
                        totalHeight += image.getHeight();
                        if (width < image.getWidth()) {
                            width = image.getWidth();
                        }
                        image.flush();
                    }

                    BufferedImage tag = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);

                    tag.getGraphics();

                    Graphics g = tag.createGraphics();

                    int startHeight = 0;
                    for (BufferedImage image : list) {
                        g.drawImage(image, 0, startHeight, width, image.getHeight(), null);
                        g.drawImage(image, 0, startHeight, width, image.getHeight(), null);
                        startHeight += image.getHeight() + heightOffset;
                    }
                    g.dispose();

                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    ImageOutputStream imOut;
                    imOut = ImageIO.createImageOutputStream(bs);
                    ImageIO.write(tag, "png", imOut);
                    byteInputStream = new ByteArrayInputStream(bs.toByteArray());
                    byteInputStream.close();
                    doc.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (byteInputStream != null) {
                    StreamUtil.safeCloseInputStream(byteInputStream);
                }
            }

            File uploadFile = new File(targetPath);
            fops = new FileOutputStream(uploadFile);
            fops.write(StreamUtil.readInputStream(byteInputStream));
            fops.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (instream != null) {
                StreamUtil.safeCloseInputStream(instream);
            }
            if(fops!=null){
                StreamUtil.safeCloseOutputStream(fops);
            }
        }
    }

}
