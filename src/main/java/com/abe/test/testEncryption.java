package com.abe.test;

import com.abe.cpabeUtil;
import com.abe.util.getMultipartFileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;

public class testEncryption {
    public static void main(String[] args) throws Exception {
        String dir = "src/main/data/";
        String pkFileName = dir + "pk.properties";
        File messageFile = new File("");


        String jsonString = "{\n" +
                "  \"name\": 0,\n" +
                "  \"value\": \"(2,3)\",\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"name\": 2292,\n" +
                "      \"value\": \"1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": 6667,\n" +
                "      \"value\": \"2\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": 183,\n" +
                "      \"value\": \"3\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        MultipartFile messagemultipartFile = getMultipartFileUtil.getMultipartFile(messageFile);
        OutputStream ctOutputStream = cpabeUtil.encrypt(messagemultipartFile, jsonString, pkFileName);
        System.out.println(ctOutputStream);
//        //将输出流转换为密文文件, OutputStream-->txt文件
//        String outputStreamToCtFileName = "src/main/java/com/abe/data/outputStreamToCt.txt";
//        byte[] data = ((ByteArrayOutputStream) ctOutputStream).toByteArray();
//        File ctFile = new File(outputStreamToCtFileName);
//        FileOutputStream ctfileOutputStream = new FileOutputStream(ctFile);
//        ctfileOutputStream.write(data);
//        ctfileOutputStream.close();

    }
}
