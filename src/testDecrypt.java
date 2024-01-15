import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class testDecrypt {
    public static void main(String[] args) throws Exception {
        String messageFileName = "C:\\Users\\57802\\Desktop\\解密出来的文件.pdf";
        String cctFileName = "C:\\Users\\57802\\Desktop\\cct.properties";
        String userSk = "userSk=userStaticSk=userAttList=[3, 1, 2]D=JTxtWjTeWkxBMxnCyXKdXryf8Jhm9mswF2IeTsd9L93/Xz4eVICz6cfAddXnjkMb2kId+rY0Ry3oP6Ub4++syZriRxjMrgbmYnQsDn4D1J4MHuQ7OyevIf3Gx9anQAA9ERnbSo3GupHr9C4iWKnqQb7VxF0e+SozgrUxyAXLpUsD0=kPQSnvx13LmUKnu9Nulgp15PkILWcYlH7HwSV0iv8/CDYiEq7en/x9BRAnnysQU9oPYFeAdDqt/LXtsoSVsJb5yowf1Ji7wtMY+of01tJffGnnP5846XyUtR464vQlsmwZsQmAX7FIMH/zQhQJdVAFJQA7ewbig2Jq5rpRnt3wkD-3=gXu4gRl48rydvxwsWyGwmOFrZ5Q1nUzf5MGIP7weZ8FU5jNDNEDPhABV/VuuODA1pj6beqWttcmpiocAjO9UayCPLR96F+XDRxX44FO4dEr9+INrnri/bsFIWnJZBxpDw+FsM8AtQCy0Ld+40HGau0koVqBdq3rxut7dwnkccj0D-1=iLndgL4bVRQKDL89GnifFXupiRqUu34luQaiCkmmawCx1CIB3dytCk8RN0mDxkD+M6XKZAxLHYqvix2YuGYfYR7mgd2SnGC4mMi0HxPrXkRbEsHRAZzeYEE639JPdZ7OESRYsqVUpmne//skSoUyy9zjcw4jcb6Aa8moXF62ROID-2=GbPVAUWmYb6NTDrmhQy6CMuJBsX0+dPHcu1VvwWVhLEsAMfCRKm2EvJAq+vIIvwCiQplu8VRXk7fPy+PCH1n1qKMXZBEafi/1rRgMlpatSiGMZfbwZNAMpXH3zchmwLp0NG4QS6GbzBcGhlvC+67juQmlXzplDN7+LIbNDUUc10";


        //密文文件转成MultipartFile
        File outputStreamCtFile = new File("C:\\Users\\57802\\Desktop\\outputStreamToCt.txt");
        MultipartFile ctMultipartFile = getMultipartFileUtil.getMultipartFile(outputStreamCtFile);

        //解密文件，产生一个MultipartFile文件
        OutputStream messageOutputStream = cpabe_NewUtil.decrypt(ctMultipartFile, cctFileName, userSk, messageFileName);

        //将解密出来的MultipartFile类型保存到本地看看正确不
        if (messageOutputStream != null) {
            //将输出流转换为明文文件
            String outputStreamToMessageFileName = "C:\\Users\\57802\\Desktop\\outputStreamToMessage.pdf";
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
