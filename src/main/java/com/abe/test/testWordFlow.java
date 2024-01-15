package com.abe.test;

import com.abe.FileToPngAndWaterMark;

import java.io.File;

/**
 * @author zhangxi
 */
public class testWordFlow {
    public static void main(String[] args) {
        String wordUrl = "";
        String pdfUrl = "";
        String pdfWithWaterMarkUrl = "";
        String pngUrl = "";

        try {
            FileToPngAndWaterMark.wordToPdf(wordUrl, pdfUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileToPngAndWaterMark.addWaterMark(pdfUrl, pdfWithWaterMarkUrl, "gange");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            FileToPngAndWaterMark.pdfFileToImage(new File(pdfWithWaterMarkUrl), pngUrl, 10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
