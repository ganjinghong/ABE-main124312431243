

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.util.Random;

//import org.junit.Test;

public class PdfWaterMarkUtil {

    public static void addWaterMark(String srcPdfPath,String tarPdfPath,String WaterMarkContent)throws Exception {
        PdfReader reader = new PdfReader(srcPdfPath);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(tarPdfPath));
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
            content.showTextAligned(Element.ALIGN_CENTER,WaterMarkContent, number, number, 30); //水印内容和水印位置
            content.endText();
        }
        stamper.close();

    }

    public static void main(String[] args) {
        PdfWaterMarkUtil pwm=new PdfWaterMarkUtil();
        String username = "gange";
        String baseSrcUrl ="C:\\Users\\57802\\Desktop\\秘钥策略属性基加密(KP-ABE).pdf";
        String baseOutUrl = "C:\\Users\\57802\\Desktop\\22.pdf";
        try {
            pwm.addWaterMark(baseSrcUrl, baseOutUrl,username);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}