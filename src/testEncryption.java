import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class testEncryption {
    public static void main(String[] args) throws Exception {
        String dir = "data/";
        String pkFileName = dir + "pk.properties";
        String ctFileName = dir + "ct.properties";
        String messageFileName = "C:\\Users\\57802\\Desktop\\解密出来的文件.pdf";
        File mmessageFile = new File("C:\\Users\\57802\\Desktop\\22.doc");
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


        File messageFile = new File("C:\\Users\\57802\\Desktop\\老师的权重.pdf");
        MultipartFile messagemultipartFile = getMultipartFileUtil.getMultipartFile(messageFile);




        OutputStream ctOutputStream = cpabe_NewUtil.encrypt(messagemultipartFile, mmessageFile.toString(), jsonString, pkFileName, ctFileName);
        //OutputStream-->txt文件--》MultipartFile-->txt文件
        //将输出流转换为密文文件
        String outputStreamToCtFileName = "C:\\Users\\57802\\Desktop\\outputStreamToCt.txt";
        byte[] data = ((ByteArrayOutputStream) ctOutputStream).toByteArray();
        File ctFile = new File(outputStreamToCtFileName);
        FileOutputStream ctfileOutputStream = new FileOutputStream(ctFile);
        ctfileOutputStream.write(data);
        ctfileOutputStream.close();

        //密文文件转成MultipartFile
        File outputStreamCtFile = new File("C:\\Users\\57802\\Desktop\\outputStreamToCt.txt");
        MultipartFile ctMultipartFile = getMultipartFileUtil.getMultipartFile(outputStreamCtFile);


    }
}
