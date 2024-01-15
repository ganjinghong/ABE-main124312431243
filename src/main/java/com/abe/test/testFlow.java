package com.abe.test;

import com.abe.cpabeUtil;
import com.abe.util.getMultipartFileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class testFlow {
    public static void main(String[] args) throws Exception {
        String dir = "src/main/data/";
        String pkFileName = dir + "pk.properties";
        String mskFileName = dir + "msk.properties";

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
//        System.out.println(messagemultipartFile.getName());

        OutputStream ctOutputStream = cpabeUtil.encrypt(messagemultipartFile, jsonString, pkFileName);
        //OutputStream-->txt文件--》MultipartFile-->txt文件
        //将输出流转换为密文文件
        String outputStreamToCtFileName = "/Users/zhangxi/Desktop/outputStreamToCt.txt";
        byte[] data = ((ByteArrayOutputStream) ctOutputStream).toByteArray();
        File ctFile = new File(outputStreamToCtFileName);
        FileOutputStream ctfileOutputStream = new FileOutputStream(ctFile);
        ctfileOutputStream.write(data);
        ctfileOutputStream.close();

        //密文文件转成MultipartFile
        File outputStreamCtFile = new File("/Users/zhangxi/Desktop/outputStreamToCt.txt");
        MultipartFile ctMultipartFile = getMultipartFileUtil.getMultipartFile(outputStreamCtFile);

        String[] userAttList = {"1", "4", "5", "2", "3"};
        String userSk = cpabeUtil.keygen(userAttList, pkFileName, mskFileName);

        //解密文件，产生一个MultipartFile文件
        OutputStream messageOutputStream = cpabeUtil.decrypt(ctMultipartFile, userSk);

        //将解密出来的MultipartFile类型保存到本地看看正确不
        if (messageOutputStream != null) {
            //将输出流转换为明文文件
            String outputStreamToMessageFileName = "/Users/zhangxi/Desktop/outputStreamToMessage.pdf";
            byte[] messageData = ((ByteArrayOutputStream) messageOutputStream).toByteArray();
            File outputStreamToMessageFile = new File(outputStreamToMessageFileName);
            FileOutputStream messageFileOutputStream = new FileOutputStream(outputStreamToMessageFile);
            messageFileOutputStream.write(messageData);
            messageFileOutputStream.close();
        } else {
            System.out.println("The access tree is not satisfied.");
        }
    }
}
