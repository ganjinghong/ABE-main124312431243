package com.abe.test;

import com.abe.cpabeUtil;
import com.abe.util.getMultipartFileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class testDecrypt {
    public static void main(String[] args) throws Exception {
        //密文文件转成MultipartFile
        String[] userAttList = {"1", "2","3"};
        String dir = "src/main/data/";
        String pkFileName = dir + "pk.properties";
        String mskFileName = dir + "msk.properties";
        String userSk = cpabeUtil.keygen(userAttList, pkFileName, mskFileName);
        File outputStreamCtFile = new File("src/main/java/com/abe/data/outputStreamToCt.txt");
        MultipartFile ctMultipartFile = getMultipartFileUtil.getMultipartFile(outputStreamCtFile);

        //解密文件，产生一个MultipartFile文件
        OutputStream messageOutputStream = cpabeUtil.decrypt(ctMultipartFile, userSk);

        //将解密出来的MultipartFile类型保存到本地看看正确不
        if (messageOutputStream != null) {
            //将输出流转换为明文文件
            String outputStreamToMessageFileName = "src/main/java/com/abe/data1/outputStreamToMessage.pdf";
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
