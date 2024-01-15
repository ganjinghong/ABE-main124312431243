import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.lang.Integer.valueOf;

public class cpabeUtil {
    public static boolean decrypt(String ctFileName, String skFileName, String messageFileName) {

        Properties ctProp = loadPropFromFileUtil.loadPropFromFile(ctFileName);
        //下面的这个ppFile可以换成其他的，只要传入a.properties这个文件的路径就可以了，必须是路径，因为读取函数不是自己写的，是系统自带的。
//        Pairing bp = PairingFactory.getPairing(ctProp.getProperty("ppFile"));
        Pairing bp = PairingFactory.getPairing("data/a.properties");

        Properties skProp = loadPropFromFileUtil.loadPropFromFile(skFileName);


        String userSk = skProp.getProperty("userSk");

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


        System.out.println(userSk);
        //恢复用户属性列表String[]类型：先将首尾的方括号去除，然后按","分割
        String userAttListString = userSk.substring(userSk.indexOf("userAttList=") + 12, userSk.indexOf("D="));
        String[] userAttList = userAttListString.substring(1, userAttListString.length() - 1).split(", ");

//

//        String userAttListString = skProp.getProperty("userAttList");
//        //恢复用户属性列表 int[]类型
//        int[] userAttList = Arrays.stream(userAttListString.substring(1, userAttListString.length()-1).split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();


        String CString = ctProp.getProperty("C");
        Element C = bp.getGT().newElementFromBytes(Base64.getDecoder().decode(CString)).getImmutable();
        String C0String = ctProp.getProperty("C0");
        Element C0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C0String)).getImmutable();

//        String DString = skProp.getProperty("D");
//        Element D = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DString)).getImmutable();
//        String D0String = skProp.getProperty("D0");
//        Element D0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(D0String)).getImmutable();

        String DString = userSk.substring(userSk.indexOf("D=") + 2, userSk.indexOf("D0="));
        Element D = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DString)).getImmutable();
        String D0String = userSk.substring(userSk.indexOf("D0=") + 3, userSk.indexOf("D-"));
        Element D0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(D0String)).getImmutable();

        // 从密文中获取访问策略字符串
        String accessTreeString = ctProp.getProperty("Policy");
        // 从访问策略字符串构建访问树对象
        Map<String, TreeNode> accessTree = jsonStringToAccessTreeUtil.jsonStringToAccessTree(accessTreeString);


        for (String key : accessTree.keySet()) {
            if (accessTree.get(key).isLeaf()) {
                // 如果叶子节点的属性值属于属性列表，则将属性对应的密文组件和秘钥组件配对的结果作为秘密值
                if (Arrays.asList(userAttList).contains(accessTree.get(key).att)) {
                    System.out.println("1");
                    String C1tring = ctProp.getProperty("C1-" + accessTree.get(key).att);
                    Element C1 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C1tring)).getImmutable();
                    String C2tring = ctProp.getProperty("C2-" + accessTree.get(key).att);
                    Element C2 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C2tring)).getImmutable();

                    if (userSk.indexOf("D-" + accessTree.get(key).att) != userSk.lastIndexOf("D-")) {
                        String DattString = userSk.substring(userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length(), userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length() + 171);
                        Element Datt = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DattString)).getImmutable();
                        accessTree.get(key).secretShare = bp.pairing(C1, D0).mul(bp.pairing(C2, Datt)).getImmutable();
                        System.out.println("1===========" + DattString);
                    } else {
                        String DattString = userSk.substring(userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length());
                        Element Datt = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DattString)).getImmutable();
                        accessTree.get(key).secretShare = bp.pairing(C1, D0).mul(bp.pairing(C2, Datt)).getImmutable();
                        System.out.println("last===========" + DattString);
                    }

                }
            }
        }


        Element symmetrickey;
        // 进行秘密恢复
        boolean treeOK = nodeRecoverUtil.nodeRecover(accessTree, "0", userAttList, bp);

        if (treeOK) {
            Element egg_alphas = bp.pairing(C0, D).div(accessTree.get("0").secretShare);
            symmetrickey = C.div(egg_alphas);
            String symmetric_ciphertext = ctProp.getProperty("symmetric_ciphertext");

            //修改
            String symmetrickey_String = Base64.getEncoder().withoutPadding().encodeToString(symmetrickey.toBytes());
//            String decryptStr = sm4.decryptStr(message_sm4, CharsetUtil.CHARSET_UTF_8);
            String symmetrickey_String_quit = symmetrickey_String.substring(0, 16);
            SymmetricCrypto sm4 = SmUtil.sm4(symmetrickey_String_quit.getBytes());
//
            //

            //解密函数删除了CharsetUtil.CHARSET_UTF_8，因为我看加密的时候按Hex加密的，解密的时候感觉是不是不对应才报错BadPaddingException: pad block corrupted
            //下面的这个可以做个trycatsh
            String decryptStr = sm4.decryptStr(symmetric_ciphertext, CharsetUtil.CHARSET_UTF_8);
            //下面的这个可以做个trycatsh
            binaryUtil.binaryStringRecover(decryptStr, messageFileName);
            return true;
        } else {
            System.out.println("The access tree is not satisfied.");
            return false;

        }

    }

}