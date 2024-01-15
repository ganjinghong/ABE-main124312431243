import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

public class decrypt_new {
    public static boolean decrypt(String ctFileName, String userSk, String messageFileName) throws Exception {

        //判断密钥是动态密钥还是静态密钥，如果是动态密钥则要监测时间是否没超时；
        if (userSk.indexOf("userStaticSk=") == -1) {
            //动态密钥
            String userTimeString = userSk.substring(userSk.indexOf("userDynamicSk=userKeyTime=") + 26, userSk.indexOf("userAttList="));
            System.out.println(userTimeString);
            StringBuilder userTimeStringBuilder = new StringBuilder(userTimeString);

            for (int i = 0; i < userTimeString.length(); i++) {
                userTimeStringBuilder.setCharAt(i, (char) (userTimeString.charAt(i) - 26));
            }
            System.out.println();

            long time1 = Long.parseLong(userTimeStringBuilder.toString());
            System.out.println(String.valueOf(time1));
            if (time1 > System.currentTimeMillis()) {
                //说明该动态密钥还没超时
                //空操作
            } else {
                //说明该动态密钥超时了
                return false;
            }
        } else {
            //静态密钥就正常执行
            //空操作
        }


        //判断入参userSk
        if (userSk == null) {
            throw new Exception("传入的密钥字符串不能为空");
        }

        //判断入参ctFileName
        if (ctFileName == null) {
            throw new Exception("需要临时保存密文的路径不能为空");
        }else {
            File ctFileNameFile_determine = new File(ctFileName);
            if (ctFileNameFile_determine.exists()){
                //如果文件存在，那么可能是具体文件，也可能是目录，目录是不能传的。
                if (ctFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                } else {
                    throw new Exception("此处已经存在文件，不能作为临时文件路径");
                }
            }else {
                if (ctFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                }else { ////////////////////////////////
                    File tempFile = new File(ctFileName);
                    String tempFileName = tempFile.getAbsolutePath().substring(0, tempFile.getAbsolutePath().lastIndexOf("\\"));
                    File tempFile_two = new File(tempFileName);
                    if (tempFile_two.exists()){
                        //可以执行算法
                    }else {
                        throw new Exception("目录不存在，不能作为临时文件路径");
                    }
                }
            }

        }


        //判断messageFileName
        if (messageFileName == null) {
            throw new Exception("需要临时保存密文的路径不能为空");
        }else {
            File messageFileNameFile = new File(messageFileName);
            if (messageFileNameFile.exists()){
                //如果文件存在，那么可能是具体文件，也可能是目录，目录是不能传的。
                if (messageFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                } else {
                    throw new Exception("此处已经存在文件，不能作为临时文件路径");
                }
            }else {
                if (messageFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                }else {
//                    String tempFileName = ctFileName.substring(0, ctFileName.lastIndexOf("\\"));
                    File tempFile = new File(messageFileName);
                    String tempFileName = tempFile.getAbsolutePath().substring(0, tempFile.getAbsolutePath().lastIndexOf("\\"));
                    File tempFile_two = new File(tempFileName);
                    if (tempFile_two.exists()){
                        //可以执行算法
                    }else {
                        throw new Exception("目录不存在，不能作为临时文件路径");
                    }
                }
            }

        }



        File ctFile = new File(ctFileName);


        Properties ctProp = loadPropFromFileUtil.loadPropFromFile(ctFileName);
        Pairing bp = PairingFactory.getPairing(ctProp.getProperty("ppFile"));

//        Properties skProp = loadPropFromFileUtil.loadPropFromFile(skFileName);
        String userAttListString = userSk.substring(userSk.indexOf("userAttList=") + 12, userSk.indexOf("D="));
        System.out.println(userAttListString);
        //恢复用户属性列表String[]类型：先将首尾的方括号去除，然后按","分割
        String[] userAttList = userAttListString.substring(1, userAttListString.length()-1).split(", ");



        String CString = ctProp.getProperty("C");
        Element C = bp.getGT().newElementFromBytes(Base64.getDecoder().decode(CString)).getImmutable();
        String C0String = ctProp.getProperty("C0");
        Element C0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C0String)).getImmutable();
//
//        String DString = skProp.getProperty("D");
//        Element D = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DString)).getImmutable();
//        String D0String = skProp.getProperty("D0");
//        Element D0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(D0String)).getImmutable();
        String DString = userSk.substring(userSk.indexOf("D=") + 2, userSk.indexOf("D0="));
        Element D = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DString)).getImmutable();
        String D0String = userSk.substring(userSk.indexOf("D0=") + 3, userSk.indexOf("D-"));
        Element D0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(D0String)).getImmutable();


//
        // 从密文中获取访问策略字符串
        String accessTreeString = ctProp.getProperty("Policy");
        // 从访问策略字符串构建访问树对象
        Map<String, TreeNode> accessTree = jsonStringToAccessTreeUtil.jsonStringToAccessTree(accessTreeString);
//
//
//
        for (String key : accessTree.keySet()) {
            if (accessTree.get(key).isLeaf()) {
                // 如果叶子节点的属性值属于属性列表，则将属性对应的密文组件和秘钥组件配对的结果作为秘密值
                if (Arrays.asList(userAttList).contains(accessTree.get(key).att)){
                    String C1tring = ctProp.getProperty("C1-"+accessTree.get(key).att);
                    Element C1 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C1tring)).getImmutable();
                    String C2tring = ctProp.getProperty("C2-"+accessTree.get(key).att);
                    Element C2 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C2tring)).getImmutable();


                    if (userSk.indexOf("D-" + accessTree.get(key).att) != userSk.lastIndexOf("D-")){
                        String DattString = userSk.substring(userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length(), userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length() + 171);
                        Element Datt = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DattString)).getImmutable();
                        accessTree.get(key).secretShare = bp.pairing(C1,D0).mul(bp.pairing(C2,Datt)).getImmutable();
                        System.out.println("1===========" + DattString);
                    } else {
                        String DattString = userSk.substring(userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length());
                        Element Datt = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DattString)).getImmutable();
                        accessTree.get(key).secretShare = bp.pairing(C1,D0).mul(bp.pairing(C2,Datt)).getImmutable();
                        System.out.println("last===========" + DattString);
                    }

                }
            }
        }



        Element symmetrickey;
        // 进行秘密恢复
        boolean treeOK = nodeRecoverUtil.nodeRecover(accessTree, "0", userAttList, bp);

        if (treeOK) {
            Element egg_alphas = bp.pairing(C0,D).div(accessTree.get("0").secretShare);
            symmetrickey = C.div(egg_alphas);
            String symmetric_ciphertext = ctProp.getProperty("symmetric_ciphertext");

            //修改
            String symmetrickey_String = Base64.getEncoder().withoutPadding().encodeToString(symmetrickey.toBytes());
//            String decryptStr = sm4.decryptStr(message_sm4, CharsetUtil.CHARSET_UTF_8);
            String symmetrickey_String_quit = symmetrickey_String.substring(0,16);
            SymmetricCrypto sm4 = SmUtil.sm4(symmetrickey_String_quit.getBytes());
//
            //

            //解密函数删除了CharsetUtil.CHARSET_UTF_8，因为我看加密的时候按Hex加密的，解密的时候感觉是不是不对应才报错BadPaddingException: pad block corrupted
            //下面的这个可以做个trycatsh
            String decryptStr = sm4.decryptStr(symmetric_ciphertext, CharsetUtil.CHARSET_UTF_8);
            //下面的这个可以做个trycatsh
            binaryUtil.binaryStringRecover(decryptStr,messageFileName);
            return true;

            //
//            MultipartFile messageMultipartFile = getMultipartFile(new File(messageFileName.toString()));
//            Path messagePath = Paths.get(messageFileName);
//            Files.delete(messagePath);

//            File messageFile = new File(messageFileName);
//            //将文件转换成输出流
//            OutputStream outputStream = new ByteArrayOutputStream();
//            FileInputStream messageFileInputStream = new FileInputStream(messageFile);
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = messageFileInputStream.read(buffer)) != -1) {
//                outputStream.write(buffer,0,length);
//            }
//            messageFileInputStream.close();
//
//
//            //删除临时文件，如果不存在会抛出文件不存在异常
//            Path messagePath = Paths.get(messageFileName);
//            Files.delete(messagePath);
//            //删除临时文件，在这个算法中，ctFileName是临时文件
//            Path ctPath = Paths.get(ctFileName);
//            Files.delete(ctPath);
//            //messageFileName这个也是临时的文件，删除它之前必须得产生明文流以便能传回
//            return outputStream;
//
        }
        else {
            return false;
        }

    }
}
