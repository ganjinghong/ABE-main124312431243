package com.abe.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

/***
 * @author lqx
 * 为WEBSERVICE 准备 PDF的二进制流的字符串
 * 文件在 XXX转成二进制流的字符串，及将二进制流反转成PDF\PDFBinary.jar
 * xxx转成二进制流的字符串，及将二进制流反转成PDF\PDFBinary.jar
 */
public class binaryUtil {
    /*BASE64Encoder和BASE64Decoder这两个方法是sun公司的内部方法，并没有在java api中公开过，所以使用这些方法是不安全的，
     * 将来随时可能会从中去除，所以相应的应该使用替代的对象及方法，建议使用apache公司的API---可引用 import org.apache.commons.codec.binary.Base64;进行替换*/
    static Base64.Encoder encoder = Base64.getEncoder();
    static Base64.Decoder decoder = Base64.getDecoder();

//    public static void main(String[] args) {
//        //将PDF格式文件转成base64编码
//        String base64String = getPDFBinary("C:\\Users\\57802\\Desktop\\test.pdf");
//        System.out.println(base64String);
//        //将base64的编码转成PDF格式文件,，保存到XXX
//        base64StringToPDF(base64String,"C:\\Users\\57802\\Desktop\\test1.pdf");
//    }

    /**
     *  将PDF转换成base64编码
     *  1.使用BufferedInputStream和FileInputStream从File指定的文件中读取内容；
     *  2.然后建立写入到ByteArrayOutputStream底层输出流对象的缓冲输出流BufferedOutputStream
     *  3.底层输出流转换成字节数组，然后由BASE64Encoder的对象对流进行编码
     * */
    public static String getBinaryString(MultipartFile file) throws IOException {
        return encoder.encodeToString(file.getBytes()).trim();
    }

    /**
     * 将base64编码转换成PDF，保存到
     * @param binaryString
     * 1.使用BASE64Decoder对编码的字符串解码成字节数组
     *  2.使用底层输入流ByteArrayInputStream对象从字节数组中获取数据；
     *  3.建立从底层输入流中读取数据的BufferedInputStream缓冲输出流对象；
     *  4.使用BufferedOutputStream和FileOutputSteam输出数据到指定的文件中
     */
    public static OutputStream binaryStringRecover(String binaryString) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(decoder.decode(binaryString));
        outputStream.flush();
        return outputStream;
    }

    /**
     * 给单机版用的版本，待优化
     *
     */
    public static void binaryStringRecover(String binaryString, String filePath) {
        BufferedInputStream bin = null;
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            //将base64编码的字符串解码成字节数组
            byte[] bytes = decoder.decode(binaryString);
            //apache公司的API
            //byte[] bytes = Base64.decodeBase64(base64sString);
            //创建一个将bytes作为其缓冲区的ByteArrayInputStream对象
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            //创建从底层输入流中读取数据的缓冲输入流对象
            bin = new BufferedInputStream(bais);

            //指定输出的文件
            File file = new File(filePath);
            //创建到指定文件的输出流
            fout = new FileOutputStream(file);
            //为文件输出流对接缓冲输出流对象
            bout = new BufferedOutputStream(fout);

            byte[] buffers = new byte[1024];
            int len = bin.read(buffers);
            while (len != -1) {
                bout.write(buffers, 0, len);
                len = bin.read(buffers);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
            bout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bin.close();
                fout.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
